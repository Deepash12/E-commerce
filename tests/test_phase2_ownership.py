"""Optional cross-user IDOR checks. Requires QA_USER2_EMAIL/PASSWORD and known user-1 resources.
Read-only: never guesses or enumerates IDs. Only uses IDs supplied by the QA owner.
"""
import os
import pytest
from conftest import api, unwrap

@pytest.fixture(scope='module')
def user2_token():
    email,password=os.getenv('QA_USER2_EMAIL'),os.getenv('QA_USER2_PASSWORD')
    if not email or not password: pytest.skip('Second QA user credentials not configured')
    r=api('POST','/api/auth/login',json={'email':email,'password':password})
    assert r.status_code==200, f'Second QA user login: HTTP {r.status_code}'
    token=unwrap(r).get('token')
    assert token, 'Second QA user login missing token'
    return token

@pytest.mark.parametrize('path_template,id_var',[
    ('/api/address/{id}','QA_USER1_ADDRESS_ID'),
    ('/api/orders/view/{id}','QA_USER1_ORDER_ID'),
])
def test_second_user_cannot_view_first_user_resource(user2_token,path_template,id_var):
    rid=os.getenv(id_var)
    if not rid: pytest.skip(f'{id_var} not configured (must be owned by QA_USER_EMAIL)')
    assert rid.isdecimal(), f'{id_var} must be a positive integer'
    r=api('GET',path_template.format(id=rid),token=user2_token)
    assert r.status_code in (403,404), (
        f'Possible IDOR: second user requested first-user resource; HTTP {r.status_code}, body={r.text[:160]!r}'
    )
