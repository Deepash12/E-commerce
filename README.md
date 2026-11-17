# E-commerce GitHub Actions QA — version 2

You have already configured these GitHub Actions secrets:
`QA_USER_EMAIL`, `QA_USER_PASSWORD`, `QA_ADMIN_EMAIL`, `QA_ADMIN_PASSWORD`.
Do **not** share them in chat or commit them to Git.

## Replace old files
Extract this ZIP. Copy its contents into the root of your existing **backend** project,
choosing **Replace** when Windows asks. Keep `.github/workflows/` intact.
In VS Code terminal:

```bash
git add .github/workflows/deployed-ecommerce-qa.yml tests/ api-docs.json requirements-qa.txt pytest.ini
git commit -m "Improve automated QA with Render warm-up and authenticated checks"
git push
```

Open GitHub > Actions > Deployed E-commerce QA > Run workflow.
Download the `ecommerce-qa-results` artifact from the finished run and upload it here.

## What changed
- Waits up to 360 seconds for a cold Render instance to respond before assertions.
- Gives each request up to 75 seconds for the response.
- Uses the configured GitHub Secrets for USER and ADMIN login tests.
- Preserves JUnit XML and text output even when tests fail.
- Product catalog public-access expectations are explicitly identified as contract checks.

## Important scope and safety
This suite has 22 tests: API contract, read-only smoke, auth/security negative
checks, and authenticated GETs. It is **not yet full CRUD or E2E testing**.
It does not intentionally create or delete products, orders, coupons or users.
An intentionally invalid admin-registration request is sent to test anonymous access.

If `/api/auth/admin/register` responds 400 instead of 401/403, review the
security configuration. That is a security concern requiring investigation,
not proof that an admin account can be created anonymously.

Use a Neon test branch and dedicated test accounts. Do not post JWTs, passwords,
DB credentials or full sensitive server logs publicly.
