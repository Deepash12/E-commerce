import os
import time
from datetime import datetime, timezone
import pytest
import requests

@pytest.fixture(scope="session", autouse=True)
def wait_for_backend():
    """Wait up to 6 minutes for Render free-tier cold start, before test assertions."""
    base = os.getenv("API_BASE_URL", "https://e-commerce-2-ibeq.onrender.com").rstrip("/")
    url = base + "/api/products/view?pageNumber=0&pageSize=1"
    deadline = time.monotonic() + 360
    attempts = 0
    last = None
    while time.monotonic() < deadline:
        attempts += 1
        try:
            r = requests.get(url, timeout=(15, 55), allow_redirects=False)
            # Any non-5xx HTTP response proves backend is responding; 401 may be expected.
            if r.status_code < 500:
                print(f"\n[WARMUP] Backend responsive: HTTP {r.status_code}; attempts={attempts}", flush=True)
                return
            last = f"HTTP {r.status_code}"
        except requests.RequestException as e:
            last = f"{type(e).__name__}: {e}"
        print(f"\n[WARMUP] Attempt {attempts}: {last}", flush=True)
        time.sleep(10)
    pytest.fail(f"Backend did not become responsive after 360s. Last result: {last}")
