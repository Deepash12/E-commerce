# Advanced QA Phase 2 — Additive package

**This ZIP adds new files. It does NOT replace or change the original QA suite.**

## Installation
1. Extract ZIP into the **root** of your existing E-commerce backend repository.
2. Commit/push `tests/test_phase2_*.py` and `.github/workflows/ecommerce-qa-phase2.yml`.
3. GitHub Settings > Secrets and variables > Actions > Variables: set `QA_BASE_URL` to your **separate QA deployment URL** (HTTPS). Do not point this at production. Existing `QA_USER_*` and `QA_ADMIN_*` secrets are reused.
4. Actions > **E-commerce Advanced QA Phase 2** > Run workflow, `run_mutations=false` first.
5. Download `ecommerce-phase2-results` artifact for actual pass/fail results.

## Optional tests
- Ownership/IDOR: configure secrets `QA_USER2_EMAIL`, `QA_USER2_PASSWORD`, and variables `QA_USER1_ADDRESS_ID`, `QA_USER1_ORDER_ID` referring to **first user's** test resources. Without these, tests skip.
- Mutation (invalid cart quantity on a real disposable product): configure variables `QA_ISOLATED_DB_CONFIRMED=true`, `QA_DISPOSABLE_PRODUCT_ID` and enable `run_mutations=true`. Only set this if the app really connects to an isolated QA database. This flag is an acknowledgement, not an automated verification.

## Coverage
- Public vs authenticated vs admin-only GET endpoints
- Malformed JWT rejection
- Catalog product IDs, product details, stock/price sanity
- Authenticated order/cart/address reads
- Cross-user address/order read isolation (optional)
- Invalid cart quantity with a real disposable product (optional)

## Not yet automated
Concurrent checkout, order cancellation/stock restoration, real payment processing, rate limiting and frontend browser E2E are **not included**. These need safe test-data provisioning, known response schemas and/or dedicated load/browser environments. The suite does not claim those features pass.

## Important
- Existing coupon/address 401 issues remain outside this new suite; it does not hide or fix them.
- These are test **assertions**, not verified outcomes. No live endpoint testing was performed during packaging.
- Avoid uploading private JUnit artifacts publicly if they contain sensitive response bodies.
