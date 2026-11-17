import json
import os
from pathlib import Path
from urllib.parse import quote

import pytest
import requests

BASE = os.getenv("API_BASE_URL", "https://e-commerce-2-ibeq.onrender.com").rstrip("/")
SPEC = json.loads((Path(__file__).resolve().parents[1] / "api-docs.json").read_text())
TIMEOUT = (12, 50)
SESSION = requests.Session()
SESSION.trust_env = True

def call(method, path, token=None, **kwargs):
    headers = kwargs.pop("headers", {})
    if token:
        headers["Authorization"] = f"Bearer {token}"
    try:
        return SESSION.request(method, BASE + path, headers=headers, timeout=TIMEOUT,
                               allow_redirects=False, **kwargs)
    except requests.RequestException as exc:
        pytest.fail(f"NETWORK FAILURE for {method} {path}: {type(exc).__name__}: {exc}")

def assert_no_server_error(resp):
    assert resp.status_code < 500, f"HTTP {resp.status_code}: {resp.text[:350]}"

@pytest.fixture(scope="session")
def user_token():
    email, password = os.getenv("QA_USER_EMAIL"), os.getenv("QA_USER_PASSWORD")
    if not email or not password:
        pytest.skip("QA_USER_EMAIL / QA_USER_PASSWORD secrets not configured")
    resp = call("POST", "/api/auth/login", json={"email": email, "password": password})
    assert resp.status_code == 200, f"Test-user login returned HTTP {resp.status_code}"
    token = resp.json().get("token")
    assert token, "Login response missing token"
    return token

@pytest.fixture(scope="session")
def admin_token():
    email, password = os.getenv("QA_ADMIN_EMAIL"), os.getenv("QA_ADMIN_PASSWORD")
    if not email or not password:
        pytest.skip("QA_ADMIN_EMAIL / QA_ADMIN_PASSWORD secrets not configured")
    resp = call("POST", "/api/auth/login", json={"email": email, "password": password})
    assert resp.status_code == 200, f"Test-admin login returned HTTP {resp.status_code}"
    token = resp.json().get("token")
    assert token, "Admin login response missing token"
    return token

def test_openapi_contract_has_expected_modules():
    assert len(SPEC["paths"]) >= 50
    for path in ("/api/auth/login", "/api/products/view", "/api/cart/view",
                 "/api/orders/checkout", "/api/coupon/createCoupon", "/api/payment/initiate"):
        assert path in SPEC["paths"]

@pytest.mark.parametrize("path", ["/api/products/view", "/api/products/all"])
def test_public_product_listing(path):
    resp = call("GET", path, params={"pageNumber": 0, "pageSize": 5})
    assert resp.status_code == 200, f"{path}: HTTP {resp.status_code}: {resp.text[:300]}"
    assert resp.headers.get("Content-Type", "").lower().startswith("application/json")
    resp.json()

@pytest.mark.parametrize("path", [
    "/api/cart/view", "/api/address/view", "/api/orders/all",
    "/api/profile/view", "/api/wishlist", "/api/coupon/all",
])
def test_protected_routes_reject_anonymous(path):
    resp = call("GET", path)
    assert resp.status_code in (401, 403), f"Anonymous {path} unexpectedly returned {resp.status_code}"

def test_admin_register_rejects_anonymous_invalid_request():
    # Deliberately invalid body; cannot create an admin.
    resp = call("POST", "/api/auth/admin/register", json={})
    assert resp.status_code in (401, 403), (
        f"SECURITY: anonymous admin-registration request reached validation "
        f"(HTTP {resp.status_code}); endpoint may be publicly accessible"
    )

def test_login_rejects_invalid_credentials():
    resp = call("POST", "/api/auth/login", json={
        "email": "nonexistent-qa-account-329857@example.invalid",
        "password": "invalid-qa-password"
    })
    assert resp.status_code in (400, 401, 403), (
        f"Invalid credentials returned unexpected HTTP {resp.status_code}"
    )

@pytest.mark.parametrize("path", [
    "/api/cart/view", "/api/address/view", "/api/orders/all",
    "/api/profile/view", "/api/wishlist", "/api/user/coupons/active/all"
])
def test_user_read_endpoints(path, user_token):
    resp = call("GET", path, token=user_token)
    assert resp.status_code == 200, f"Authenticated GET {path}: HTTP {resp.status_code}: {resp.text[:300]}"
    resp.json()

@pytest.mark.parametrize("path", ["/api/coupon/all", "/api/coupon/view/1"])
def test_regular_user_cannot_read_admin_coupon_endpoints(path, user_token):
    resp = call("GET", path, token=user_token)
    assert resp.status_code in (401, 403), (
        f"USER accessed admin endpoint {path}: HTTP {resp.status_code}"
    )

def test_admin_coupon_list(admin_token):
    resp = call("GET", "/api/coupon/all", token=admin_token)
    assert resp.status_code == 200, f"Admin coupon list HTTP {resp.status_code}: {resp.text[:300]}"
    resp.json()

def test_public_catalog_pagination():
    resp = call("GET", "/api/products/view", params={"pageNumber": 0, "pageSize": 2})
    assert resp.status_code == 200
    payload = resp.json()
    data = payload.get("data", payload) if isinstance(payload, dict) else payload
    if isinstance(data, dict) and "content" in data:
        assert len(data["content"]) <= 2, "Pagination ignored pageSize=2"

def test_frontend_reachable():
    url = os.getenv("FRONTEND_URL", "https://e-commerce-frontend-iota-eight.vercel.app/")
    try:
        resp = SESSION.get(url, timeout=TIMEOUT)
    except requests.RequestException as exc:
        pytest.fail(f"Frontend network failure: {exc}")
    assert resp.status_code == 200, f"Frontend HTTP {resp.status_code}"
    assert "<html" in resp.text.lower(), "Frontend response is not HTML"
