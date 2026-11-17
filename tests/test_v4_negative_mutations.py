"""Opt-in negative validation tests. Must run only against isolated QA DB."""
from uuid import uuid4
from datetime import datetime,timedelta,timezone
import pytest
from conftest import api
pytestmark=pytest.mark.mutation

def coupon_payload():
    now=datetime.now(timezone.utc).replace(tzinfo=None)
    return {'couponCode':'QA_NEG_'+uuid4().hex[:12].upper(),
      'couponType':'PERCENTAGE','description':'QA invalid coupon test',
      'minOrderAmount':200,'discountAmount':10,'maximumDiscountAmount':50,
      'validFrom':(now-timedelta(hours=1)).isoformat(timespec='seconds'),
      'expiryAt':(now+timedelta(days=1)).isoformat(timespec='seconds'),
      'perUserLimit':1,'globalUsageLimit':10,'isActive':True}

@pytest.mark.parametrize('field,value',[
 ('discountAmount',101),('discountAmount',-1),
 ('maximumDiscountAmount',0),('minOrderAmount',-1),
 ('perUserLimit',0),('globalUsageLimit',0),
 ('couponCode',''),
])
def test_invalid_coupon_rejected(admin_token,field,value):
    payload=coupon_payload(); payload[field]=value
    r=api('POST','/api/coupon/createCoupon',token=admin_token,json=payload)
    assert r.status_code in (400,409,422),f'Invalid {field} accepted: {r.status_code} {r.text[:300]}'

def test_coupon_invalid_date_range_rejected(admin_token):
    payload=coupon_payload();payload['expiryAt']=payload['validFrom']
    r=api('POST','/api/coupon/createCoupon',token=admin_token,json=payload)
    assert r.status_code in (400,422),f'Invalid coupon dates accepted: {r.status_code} {r.text[:300]}'

@pytest.mark.parametrize('quantity',[0,-1,-100])
def test_cart_rejects_invalid_quantity(user_token,quantity):
    r=api('POST','/api/cart/addToCart',token=user_token,json={'productId':999999999,'quantity':quantity})
    assert r.status_code in (400,404,422),f'Invalid quantity accepted: {r.status_code} {r.text[:300]}'

@pytest.mark.parametrize('phone,postal',[
 ('123','305001'),('9876543210','12'),('abcdefghij','305001'),
])
def test_address_rejects_invalid_fields(user_token,phone,postal):
    payload={'fullName':'QA Invalid','phone':phone,'addressLine1':'QA TEST',
      'city':'Ajmer','state':'Rajasthan','postalCode':postal,'country':'India',
      'landmark':'QA','isDefault':False}
    r=api('POST','/api/address/add',token=user_token,json=payload)
    assert r.status_code in (400,422),f'Invalid address accepted: {r.status_code} {r.text[:300]}'
