"""Opt-in mutations for an isolated QA database only. Tests use unique data.
No product/order mutations until product multipart DTO and order lifecycle are verified.
"""
from datetime import datetime, timedelta, timezone
from uuid import uuid4
import pytest
from conftest import api, unwrap

pytestmark = pytest.mark.mutation


def coupon_payload(kind):
    now = datetime.now(timezone.utc)
    code = f'QA3{kind[:3]}{uuid4().hex[:10].upper()}'
    amount = 25 if kind == 'FLAT' else 10
    return {
        'couponCode': code, 'couponType': kind,
        'description': 'Automated isolated QA test coupon',
        'minOrderAmount': 200, 'discountAmount': amount,
        'maximumDiscountAmount': 25 if kind == 'FLAT' else 50,
        'validFrom': (now - timedelta(minutes=2)).replace(tzinfo=None).isoformat(timespec='seconds'),
        'expiryAt': (now + timedelta(days=2)).replace(tzinfo=None).isoformat(timespec='seconds'),
        'perUserLimit': 1, 'globalUsageLimit': 5, 'isActive': True,
    }


@pytest.mark.parametrize('kind', ['FLAT', 'PERCENTAGE'])
def test_admin_create_and_disable_coupon(admin_token, kind):
    payload = coupon_payload(kind)
    coupon_id = None
    try:
        created = api('POST', '/api/coupon/createCoupon', token=admin_token, json=payload)
        assert created.status_code in (200, 201), f'{created.status_code}: {created.text[:500]}'
        body = unwrap(created)
        assert isinstance(body, dict)
        coupon_id = body.get('id')
        assert coupon_id, f'Coupon ID not returned: {created.text[:500]}'
        fetched = api('GET', f'/api/coupon/view/{coupon_id}', token=admin_token)
        assert fetched.status_code == 200
    finally:
        if coupon_id is not None:
            # This endpoint toggles state; only call once for newly created active coupons.
            disabled = api('PATCH', f'/api/coupon/toggle/{coupon_id}', token=admin_token)
            assert disabled.status_code == 200, f'Cleanup failed for coupon {coupon_id}: {disabled.text[:250]}'


def test_admin_rejects_duplicate_coupon_code(admin_token):
    payload = coupon_payload('FLAT')
    coupon_id = None
    try:
        first = api('POST', '/api/coupon/createCoupon', token=admin_token, json=payload)
        assert first.status_code in (200, 201), first.text[:400]
        coupon_id = unwrap(first).get('id')
        second = api('POST', '/api/coupon/createCoupon', token=admin_token, json=payload)
        assert second.status_code in (400, 409, 422), second.text[:400]
    finally:
        if coupon_id:
            disabled = api('PATCH', f'/api/coupon/toggle/{coupon_id}', token=admin_token)
            assert disabled.status_code == 200


def test_admin_rejects_invalid_percent_coupon(admin_token):
    payload = coupon_payload('PERCENTAGE')
    payload['discountAmount'] = 150
    r = api('POST', '/api/coupon/createCoupon', token=admin_token, json=payload)
    assert r.status_code in (400, 422), f'Invalid percentage accepted: {r.status_code} {r.text[:250]}'


def test_user_create_and_delete_address(user_token):
    payload = {
        'fullName': 'QA Automated Test', 'phone': '9876543210',
        'addressLine1': 'QA Test Street 1', 'addressLine2': 'Automated Test',
        'city': 'Ajmer', 'state': 'Rajasthan', 'postalCode': '305001',
        'country': 'India', 'landmark': 'QA Test Landmark', 'isDefault': False,
    }
    address_id = None
    try:
        r = api('POST', '/api/address/add', token=user_token, json=payload)
        assert r.status_code in (200, 201), f'{r.status_code}: {r.text[:400]}'
        data = unwrap(r)
        if isinstance(data, dict):
            address_id = data.get('id')
        if not address_id:
            # OpenAPI declares a generic response; inspect read listing for the created record.
            listing = api('GET', '/api/address/view', token=user_token,
                          params={'PageNumber': 0, 'PageSize': 100})
            assert listing.status_code == 200
            rows = unwrap(listing).get('data', [])
            matches = [a for a in rows if a.get('addressLine1') == payload['addressLine1']
                       and a.get('fullName') == payload['fullName']]
            assert matches, 'Created address not visible in address list'
            address_id = matches[-1]['id']
        fetched = api('GET', f'/api/address/{address_id}', token=user_token)
        assert fetched.status_code == 200
    finally:
        if address_id:
            deleted = api('DELETE', f'/api/address/delete/address/{address_id}', token=user_token)
            assert deleted.status_code == 200, f'Cleanup failed for address {address_id}'
