import os
import time
import pytest
import requests

BASE = os.getenv('QA_BASE_URL', 'https://e-commerce-2-ibeq.onrender.com').rstrip('/')
SESSION = requests.Session()


def api(method, path, token=None, **kwargs):
    headers = dict(kwargs.pop('headers', {}))
    if token:
        headers['Authorization'] = f'Bearer {token}'
    response = SESSION.request(method, BASE + path, headers=headers, timeout=90, **kwargs)
    return response


def unwrap(response):
    payload = response.json()
    return payload.get('data', payload) if isinstance(payload, dict) else payload


def pytest_addoption(parser):
    parser.addoption('--mutations', action='store_true', default=False,
                     help='Enable tests that modify the isolated QA database')


def pytest_collection_modifyitems(config, items):
    if not config.getoption('--mutations'):
        skip = pytest.mark.skip(reason='Mutations disabled: rerun with --mutations on isolated QA DB')
        for item in items:
            if 'mutation' in item.keywords:
                item.add_marker(skip)


@pytest.fixture(scope='function')
def user_token():
    email, password = os.getenv('QA_USER_EMAIL'), os.getenv('QA_USER_PASSWORD')
    if not email or not password:
        pytest.skip('QA USER repository secrets not configured')
    response = api('POST', '/api/auth/login', json={'email': email, 'password': password})
    assert response.status_code == 200, f'USER login failed: HTTP {response.status_code}'
    token = unwrap(response).get('token')
    assert token, 'USER login response missing token'
    return token


@pytest.fixture(scope='function')
def admin_token():
    email, password = os.getenv('QA_ADMIN_EMAIL'), os.getenv('QA_ADMIN_PASSWORD')
    if not email or not password:
        pytest.skip('QA ADMIN repository secrets not configured')
    response = api('POST', '/api/auth/login', json={'email': email, 'password': password})
    assert response.status_code == 200, f'ADMIN login failed: HTTP {response.status_code}'
    token = unwrap(response).get('token')
    assert token, 'ADMIN login response missing token'
    return token


@pytest.fixture(scope='session')
def sample_product_id():
    r = api('GET', '/api/products/all', params={'pageNumber': 0, 'pageSize': 10})
    assert r.status_code == 200
    data = unwrap(r)
    products = data.get('content', []) if isinstance(data, dict) else []
    if not products:
        pytest.skip('QA catalog has no products')
    return products[0]['id']
