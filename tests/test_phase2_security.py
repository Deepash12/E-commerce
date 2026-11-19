"""Phase 2: non-destructive security and access-control checks.
Run against QA. These tests intentionally do not create or modify records.
"""
import pytest
from conftest import api

# API-specific status expectations: 401 unauthenticated, 403 insufficient role.
PRIVATE_GET = [
    '/api/profile/view', '/api/cart/view', '/api/wishlist',
    '/api/address/view', '/api/orders/all',
    '/api/reviewAndRating/reviews/myReviews',
]
ADMIN_GET = ['/api/products/view', '/api/coupon/all']
PUBLIC_GET = ['/api/products/all', '/api/category/view/all', '/api/sub-category/view/all']

@pytest.mark.parametrize('path', PRIVATE_GET + ADMIN_GET)
def test_anonymous_cannot_read_private_resources(path):
    r = api('GET', path)
    assert r.status_code in (401, 403), f'{path}: expected auth rejection; got HTTP {r.status_code}, body={r.text[:200]!r}'

@pytest.mark.parametrize('path', ADMIN_GET)
def test_user_cannot_read_admin_resources(path, user_token):
    r = api('GET', path, token=user_token)
    assert r.status_code in (401, 403), f'{path}: user unexpectedly permitted: HTTP {r.status_code}'

@pytest.mark.parametrize('path', PUBLIC_GET)
def test_public_catalog_is_available(path):
    r = api('GET', path)
    assert r.status_code == 200, f'{path}: HTTP {r.status_code}, body={r.text[:160]!r}'

@pytest.mark.parametrize('path', ['/api/profile/view','/api/cart/view','/api/address/view','/api/orders/all'])
def test_valid_user_can_read_own_resources(path, user_token):
    r = api('GET', path, token=user_token)
    assert r.status_code == 200, f'{path}: HTTP {r.status_code}, body={r.text[:160]!r}'

@pytest.mark.parametrize('path', ['/api/profile/view','/api/cart/view','/api/address/view'])
def test_malformed_bearer_token_is_rejected(path):
    r = api('GET', path, headers={'Authorization':'Bearer definitely-not-a-jwt'})
    assert r.status_code in (401, 403), f'{path}: malformed JWT unexpectedly accepted: {r.status_code}'

@pytest.mark.parametrize('path', ADMIN_GET)
def test_admin_can_read_admin_resources(path, admin_token):
    r = api('GET', path, token=admin_token)
    assert r.status_code == 200, f'{path}: HTTP {r.status_code}, body={r.text[:160]!r}'
