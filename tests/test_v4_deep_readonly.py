"""Deep, non-mutating regression tests grounded in supplied OpenAPI and source code.
Test cases are parameterized and counted individually in JUnit output.
"""
import pytest
from conftest import api, unwrap

DENIED = (401, 403)
BAD = (400, 401, 403, 404, 405, 409, 415, 422)

def check_denied(r):
    assert r.status_code in DENIED, f"Security bypass: HTTP {r.status_code}: {r.text[:300]}"

# Explicit role boundary checks, including write endpoints with syntactically valid requests.
PROTECTED_WRITES = [
 ('POST','/api/products/add', {'data':{'dto':'{}'}}),
 ('PUT','/api/products/edit/999999999', {'data':{'dto':'{}'}}),
 ('PUT','/api/products/enable-disable/999999999', {'params':{'flag':'false'}}),
 ('POST','/api/coupon/createCoupon', {'json':{}}),
 ('PUT','/api/coupon/updateCoupon/999999999', {'json':{}}),
 ('PATCH','/api/coupon/toggle/999999999', {}),
 ('POST','/api/cart/addToCart', {'json':{'productId':999999999,'quantity':1}}),
 ('PUT','/api/cart/update/quantity', {'json':{'productId':999999999,'quantity':1}}),
 ('DELETE','/api/cart/999999999', {}),
 ('PUT','/api/wishlist/toggleWishlist/999999999', {}),
 ('POST','/api/orders/checkout', {'json':{'addressId':999999999}}),
 ('DELETE','/api/orders/cancel/999999999', {}),
 ('POST','/api/address/add', {'json':{}}),
 ('PUT','/api/address/update/999999999', {'json':{}}),
 ('DELETE','/api/address/delete/address/999999999', {}),
 ('POST','/api/reviewAndRating/create', {'json':{}}),
 ('PUT','/api/reviewAndRating/reviews/update/999999999', {'json':{}}),
 ('DELETE','/api/reviewAndRating/reviews/delete/999999999', {}),
 ('PUT','/api/reviewAndRating/product/update/likes/999999999', {'params':{'action':'LIKE'}}),
 ('POST','/api/payment/initiate', {'params':{'paymentMethod':'UPI'},'json':{'amount':1}}),
 ('POST','/api/payment/complete/999999999', {}),
 ('DELETE','/api/user/coupons/applied/remove', {}),
 ('DELETE','/api/cart/coupon', {}),
]

@pytest.mark.parametrize('method,path,kwargs', PROTECTED_WRITES, ids=[f'{m}-{p}' for m,p,_ in PROTECTED_WRITES])
def test_anonymous_mutation_is_denied(method,path,kwargs):
    check_denied(api(method,path,**kwargs))

ADMIN_WRITES = PROTECTED_WRITES[:6]
@pytest.mark.parametrize('method,path,kwargs', ADMIN_WRITES, ids=[f'{m}-{p}' for m,p,_ in ADMIN_WRITES])
def test_user_cannot_modify_admin_resources(user_token,method,path,kwargs):
    check_denied(api(method,path,token=user_token,**kwargs))

ADMIN_READS = [
 '/api/products/view','/api/coupon/all','/api/coupon/view/999999999'
]
@pytest.mark.parametrize('path',ADMIN_READS)
def test_guest_cannot_read_admin_resources(path):
    check_denied(api('GET',path))

@pytest.mark.parametrize('path',ADMIN_READS)
def test_user_cannot_read_admin_resources(path,user_token):
    check_denied(api('GET',path,token=user_token))

PRIVATE_READS = [
 '/api/cart/view','/api/wishlist','/api/address/view','/api/address/999999999',
 '/api/orders/all','/api/orders/view/999999999',
 '/api/profile/view','/api/reviewAndRating/reviews/myReviews',
 '/api/user/coupons/active/all', '/api/user/coupons/active/view/999999999'
]
@pytest.mark.parametrize('path',PRIVATE_READS)
def test_guest_cannot_read_private_data(path):
    check_denied(api('GET',path))

PUBLIC_READS = [
 '/api/products/all','/api/category/view/all'
]
@pytest.mark.parametrize('path',PUBLIC_READS)
def test_public_endpoints_allow_anonymous(path):
    r=api('GET',path)
    assert r.status_code==200, f'{path}: {r.status_code} {r.text[:300]}'

@pytest.mark.parametrize('page,size',[(0,1),(0,2),(0,5),(1,1),(1,3),(2,2)])
def test_catalog_pagination_bounds(page,size):
    r=api('GET','/api/products/all',params={'pageNumber':page,'pageSize':size})
    assert r.status_code==200, r.text[:300]
    body=unwrap(r)
    assert isinstance(body.get('content'),list)
    assert len(body['content'])<=size
    assert body.get('currentPage',page)==page

@pytest.mark.parametrize('sortdir',['asc','desc'])
def test_catalog_sort_by_id(sortdir):
    r=api('GET','/api/products/all',params={'sortBy':'id','sortDir':sortdir,'pageSize':20})
    assert r.status_code==200,r.text[:300]
    ids=[x['id'] for x in unwrap(r)['content']]
    assert ids==sorted(ids,reverse=(sortdir=='desc'))

@pytest.mark.parametrize('keyword',['QA_NONEXISTENT_PRODUCT_84f13e',''])
def test_catalog_keyword_filter(keyword):
    r=api('GET','/api/products/all',params={'keyword':keyword,'pageSize':10})
    assert r.status_code==200,r.text[:300]
    items=unwrap(r)['content']
    if keyword:
        assert not items, f'Unexpected product matching unique keyword: {items[:1]}'

@pytest.mark.parametrize('min_price,max_price',[(0,100),(100,1000),(1000,1000000)])
def test_catalog_price_range(min_price,max_price):
    r=api('GET','/api/products/all',params={'minPrice':min_price,'maxPrice':max_price,'pageSize':20})
    assert r.status_code==200,r.text[:300]
    for item in unwrap(r)['content']:
        price=item.get('finalPrice')
        if price is not None:
            assert min_price <= float(price) <= max_price, f'Unexpected price for product {item.get("id")}: {price}'

@pytest.mark.parametrize('id',[-1,999999999999])
def test_nonexistent_product_not_success(id):
    r=api('GET',f'/api/products/view/{id}')
    assert r.status_code in (400,404),f'Nonexistent product returned {r.status_code}: {r.text[:300]}'

@pytest.mark.parametrize('rating',[-1,0,6,999])
def test_review_rating_invalid_input_cannot_be_anonymous(sample_product_id,rating):
    payload={'orderId':1,'orderItemId':1,'productId':sample_product_id,'rating':rating,'title':'QA','comment':'QA'}
    check_denied(api('POST','/api/reviewAndRating/create',json=payload))

@pytest.mark.parametrize('method,path',[
 ('GET','/api/products/view'),('GET','/api/coupon/all'),
 ('GET','/api/cart/view'),('GET','/api/orders/all'),
 ('POST','/api/orders/checkout'),('POST','/api/coupon/createCoupon')
])
def test_invalid_bearer_token_rejected(method,path):
    kwargs={'json':{}} if method=='POST' else {}
    r=api(method,path,headers={'Authorization':'Bearer deliberately.invalid.token'},**kwargs)
    check_denied(r)

@pytest.mark.parametrize('path',['/api/products/all','/api/category/view/all'])
def test_public_api_returns_json(path):
    r=api('GET',path)
    assert r.status_code==200
    assert 'json' in r.headers.get('content-type','').lower()
    assert isinstance(r.json(),dict)

@pytest.mark.parametrize('size',[1,3,5])
def test_reviews_pagination(sample_product_id,size):
    r=api('GET',f'/api/reviewAndRating/products/{sample_product_id}/reviews',params={'pageNumber':0,'pageSize':size})
    assert r.status_code==200,r.text[:300]
    assert len(unwrap(r)['content'])<=size

@pytest.mark.parametrize('path', ['/api/cart/view','/api/wishlist','/api/orders/all','/api/address/view'])
def test_authenticated_user_private_read(user_token,path):
    r=api('GET',path,token=user_token)
    assert r.status_code==200,f'{path}: {r.status_code} {r.text[:300]}'

# Checks response fields; may reveal actual contract violations.
def test_cart_arithmetic(user_token):
    r=api('GET','/api/cart/view',token=user_token)
    assert r.status_code==200
    body=unwrap(r)
    items=body.get('items',[])
    from decimal import Decimal
    expected=sum((Decimal(str(x['totalPrice'])) for x in items),Decimal('0'))
    assert abs(Decimal(str(body['grandTotal']))-expected)<=Decimal('0.02')
    assert all(int(x['quantity'])>0 for x in items)

def test_wishlist_no_duplicate_products(user_token):
    r=api('GET','/api/wishlist',token=user_token,params={'PageNumber':0,'PageSize':100})
    assert r.status_code==200
    content=unwrap(r).get('content',[])
    ids=[x['id'] for x in content]
    assert len(ids)==len(set(ids))

def test_order_page_shape(user_token):
    r=api('GET','/api/orders/all',token=user_token,params={'pageNumber':0,'pageSize':5})
    assert r.status_code==200
    body=unwrap(r)
    assert len(body.get('content',[]))<=5
    for order in body.get('content',[]):
        assert order.get('id') is not None
        assert float(order.get('totalAmount',0))>=0

def test_coupon_admin_pagination(admin_token):
    r=api('GET','/api/coupon/all',token=admin_token,params={'pageNumber':0,'pageSize':5})
    assert r.status_code==200
    assert len(unwrap(r).get('content',[]))<=5

@pytest.mark.parametrize('coupon_type',['FLAT','PERCENTAGE'])
def test_coupon_admin_filter(admin_token,coupon_type):
    r=api('GET','/api/coupon/all',token=admin_token,params={'couponType':coupon_type,'pageSize':20})
    assert r.status_code==200
    for coupon in unwrap(r).get('content',[]):
        assert coupon['couponType']==coupon_type

@pytest.mark.parametrize('path',['/api/products/view','/api/coupon/all'])
def test_admin_reads_admin_data(admin_token,path):
    r=api('GET',path,token=admin_token)
    assert r.status_code==200,f'{path}: {r.status_code} {r.text[:300]}'
