"""Opt-in mutation checks on dedicated QA account/DB only.
Requires both --mutations and QA_ISOLATED_DB_CONFIRMED=true. Uses a disposable product
and never places an order. Avoids creating false positives from nonexistent products.
"""
import os
import pytest
from conftest import api
pytestmark=pytest.mark.mutation

@pytest.fixture(scope='module',autouse=True)
def isolated_db_guard():
    if os.getenv('QA_ISOLATED_DB_CONFIRMED','').lower() != 'true':
        pytest.skip('Mutation tests blocked: set QA_ISOLATED_DB_CONFIRMED=true only for isolated QA DB')

@pytest.fixture(scope='module')
def qa_product_id():
    product=os.getenv('QA_DISPOSABLE_PRODUCT_ID')
    if not product or not product.isdecimal():
        pytest.skip('QA_DISPOSABLE_PRODUCT_ID not configured; use an isolated disposable product')
    return int(product)

@pytest.mark.parametrize('quantity',[0,-1,-100])
def test_cart_rejects_invalid_quantity_on_existing_product(user_token,qa_product_id,quantity):
    detail=api('GET',f'/api/products/view/{qa_product_id}')
    assert detail.status_code==200, f'Disposable product must exist: HTTP {detail.status_code}'
    r=api('POST','/api/cart/addToCart',token=user_token,
          json={'productId':qa_product_id,'quantity':quantity})
    assert r.status_code in (400,422), (
        f'Invalid quantity {quantity} not rejected with validation error: '
        f'HTTP {r.status_code}, body={r.text[:160]!r}'
    )
