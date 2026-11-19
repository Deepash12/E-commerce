"""Read-only consistency checks. No mutation, checkout, payment or load traffic."""
from decimal import Decimal, InvalidOperation
import pytest
from conftest import api, unwrap

def as_decimal(value):
    try:
        return Decimal(str(value))
    except (InvalidOperation, TypeError):
        pytest.fail(f'Invalid numeric amount: {value!r}')

def test_catalog_page_has_unique_product_ids():
    r = api('GET','/api/products/all',params={'pageNumber':0,'pageSize':50})
    assert r.status_code == 200
    data = unwrap(r)
    assert isinstance(data,dict) and isinstance(data.get('content'),list)
    ids = [p['id'] for p in data['content']]
    assert len(ids) == len(set(ids)), 'Duplicate product IDs on the same catalog page'

def test_product_details_match_catalog_id(sample_product_id):
    r=api('GET',f'/api/products/view/{sample_product_id}')
    assert r.status_code==200
    p=unwrap(r)
    assert isinstance(p,dict), f'Unexpected product response: {type(p).__name__}'
    assert str(p.get('id')) == str(sample_product_id)

def test_product_stock_is_nonnegative(sample_product_id):
    r=api('GET',f'/api/products/view/{sample_product_id}')
    assert r.status_code==200
    p=unwrap(r)
    assert 'stockQuantity' in p, 'Product detail missing stockQuantity'
    assert as_decimal(p['stockQuantity'])>=0

def test_product_prices_are_nonnegative(sample_product_id):
    r=api('GET',f'/api/products/view/{sample_product_id}')
    assert r.status_code==200
    p=unwrap(r)
    for field in ('price','discountPrice','finalPrice'):
        if p.get(field) is not None:
            assert as_decimal(p[field])>=0, f'{field} is negative'

def test_orders_are_readable_as_page(user_token):
    r=api('GET','/api/orders/all',token=user_token,params={'pageNumber':0,'pageSize':10})
    assert r.status_code==200, f'Orders API: {r.status_code} {r.text[:160]!r}'
    payload=unwrap(r)
    assert isinstance(payload,(dict,list)), f'Unexpected orders response: {type(payload).__name__}'

def test_cart_is_readable(user_token):
    r=api('GET','/api/cart/view',token=user_token)
    assert r.status_code==200, f'Cart API: {r.status_code} {r.text[:160]!r}'

def test_address_list_is_readable(user_token):
    r=api('GET','/api/address/view',token=user_token)
    assert r.status_code==200, f'Address API: {r.status_code} {r.text[:160]!r}'
