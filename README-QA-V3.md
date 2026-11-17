# Deployed E-commerce QA V3

Uses the provided OpenAPI spec for endpoint paths, DTOs, and response structures.

## Install
Copy `.github/workflows/deployed-ecommerce-qa-v3.yml`, `tests/`, `pytest.ini`, `requirements-qa.txt`, and `api-docs.json` into the backend repository root. **Keep your existing QA V2 workflow and tests if you wish**; V3 tests are in separate files but may be collected by V2 if V2 runs `pytest tests`.

Push changes and open GitHub Actions -> Deployed E-commerce QA V3 -> Run workflow.

**First run: leave `run_mutations` unchecked.** It executes read-only requests and requests that should be rejected before making changes.

**Second run: check `run_mutations` ONLY if Render points to an isolated QA database** and you're comfortable with adding/disabling test coupons and creating/deleting a test address. Successful coupon tests leave disabled QA test coupon records; no permanent coupon deletion API is present.

The V3 workflow uses existing four Repository secrets: QA_USER_EMAIL, QA_USER_PASSWORD, QA_ADMIN_EMAIL, QA_ADMIN_PASSWORD.

Reports: download artifact `ecommerce-qa-v3-results`, upload it to ChatGPT for diagnosis.

## Scope / known limitations
- `GET /api/products/view` is ADMIN-only; `GET /api/products/all` is public.
- HTTP 401 or 403 are accepted for unauthenticated denials; a signed-in USER is denied access to ADMIN routes.
- The OpenAPI schema does not define the nested JSON DTO for multipart product creation (`dto` is documented as a string). Product create/edit, stock deduction, checkout/payment, and review creation after purchase are **not automated in this safe V3 package**. Those require a confirmed working multipart DTO and order/payment response examples, and separate isolated test data.
- OpenAPI does not declare endpoint authorization, so role rules reflect the user's confirmed design; failures may expose additional mismatches.
- A failed cleanup assertion means a QA-created record may remain; review the QA DB.
- Test data uses QA-only coupon prefixes. Coupon timestamps are local ISO without timezone per OpenAPI `date-time`; adjust if your backend expects timezone offsets.
