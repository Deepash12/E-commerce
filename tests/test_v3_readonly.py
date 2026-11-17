import pytest
from conftest import api, unwrap


def assert_denied(r):
    assert r.status_code in (401, 403), f'Expected auth denial, got {r.status_code}: {r.text[:250]}'


def test_openapi_available():
    r = api('GET', '/v3/api-docs')
    assert r.status_code == 200
    assert 'paths' in r.json()


def test_public_catalog():
    r = api('GET', '/api/products/all')
    assert r.status_code == 200
    assert isinstance(unwrap(r).get('content'), list)


@pytest.mark.parametrize('size', [1, 2, 5])
def test_public_catalog_pagination(size):
    r = api('GET', '/api/products/all', params={'pageNumber': 0, 'pageSize': size})
    assert r.status_code == 200
    assert len(unwrap(r).get('content', [])) <= size


def test_public_product_details(sample_product_id):
    r = api('GET', f'/api/products/view/{sample_product_id}')
    assert r.status_code == 200


def test_public_reviews(sample_product_id):
    r = api('GET', f'/api/reviewAndRating/products/{sample_product_id}/reviews')
    assert r.status_code == 200
    assert isinstance(unwrap(r).get('content'), list)


def test_public_rating_summary(sample_product_id):
    r = api('GET', f'/api/reviewAndRating/products/{sample_product_id}/rating-summary')
    assert r.status_code == 200


def test_public_categories():
    r = api('GET', '/api/category/view/all')
    assert r.status_code == 200


def test_anonymous_admin_listing_denied():
    assert_denied(api('GET', '/api/products/view'))


def test_user_admin_listing_denied(user_token):
    assert_denied(api('GET', '/api/products/view', token=user_token))


def test_admin_product_listing(admin_token):
    r = api('GET', '/api/products/view', token=admin_token)
    assert r.status_code == 200
    assert isinstance(unwrap(r).get('content'), list)


def test_admin_product_pagination(admin_token):
    r = api('GET', '/api/products/view', token=admin_token,
            params={'pageNumber': 0, 'pageSize': 2})
    assert r.status_code == 200
    assert len(unwrap(r).get('content', [])) <= 2


@pytest.mark.parametrize('path', ['/api/cart/view', '/api/wishlist', '/api/address/view',
                                  '/api/orders/all', '/api/reviewAndRating/reviews/myReviews'])
def test_anonymous_user_data_denied(path):
    assert_denied(api('GET', path))


@pytest.mark.parametrize('path', ['/api/cart/view', '/api/wishlist', '/api/address/view',
                                  '/api/orders/all', '/api/reviewAndRating/reviews/myReviews'])
def test_user_data_readable(path, user_token):
    r = api('GET', path, token=user_token)
    assert r.status_code == 200, f'{path}: {r.status_code} {r.text[:250]}'


@pytest.mark.parametrize('path', ['/api/coupon/all', '/api/products/view'])
def test_user_admin_data_denied(path, user_token):
    assert_denied(api('GET', path, token=user_token))


def test_admin_coupon_listing(admin_token):
    r = api('GET', '/api/coupon/all', token=admin_token)
    assert r.status_code == 200


def test_user_active_coupons(user_token):
    r = api('GET', '/api/user/coupons/active/all', token=user_token)
    assert r.status_code == 200


def test_invalid_login_rejected():
    r = api('POST', '/api/auth/login', json={'email': 'nobody-qa@example.invalid', 'password': 'incorrect'})
    assert r.status_code in (400, 401, 403, 404)


def test_anonymous_create_review_denied(sample_product_id):
    payload = {'orderId': 1, 'orderItemId': 1, 'productId': sample_product_id,
               'rating': 5, 'title': 'QA', 'comment': 'should never be created'}
    assert_denied(api('POST', '/api/reviewAndRating/create', json=payload))


def test_anonymous_create_coupon_denied():
    assert_denied(api('POST', '/api/coupon/createCoupon', json={}))


def test_user_create_coupon_denied(user_token):
    assert_denied(api('POST', '/api/coupon/createCoupon', token=user_token, json={}))


def test_anonymous_checkout_denied():
    assert_denied(api('POST', '/api/orders/checkout', json={'addressId': 1}))
