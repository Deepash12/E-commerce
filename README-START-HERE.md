# E-commerce Comprehensive QA — ONE COMMIT

This package extends QA V3 with deeper security, pagination, data-integrity and negative-input tests. All test files are supplied together.

## Installation
1. Extract ZIP. Copy **contents** (not the containing folder) to the root of the BACKEND repository (same directory as pom.xml).
2. Allow replacement of `api-docs.json`, `pytest.ini`, and `requirements-qa.txt` if prompted. Do NOT replace Java source code.
3. Commit the new `.github/workflows/ecommerce-comprehensive-qa.yml`, `tests/`, `api-docs.json`, `pytest.ini`, `requirements-qa.txt` and README files together.
4. Run **E-commerce Comprehensive QA (One Commit)** in GitHub Actions. Leave `run_mutations` unchecked for first run.
5. Download artifact `ecommerce-comprehensive-qa-results` and share it for failure triage.

## Commands
```bash
git add .github/workflows/ecommerce-comprehensive-qa.yml tests/ api-docs.json pytest.ini requirements-qa.txt README-START-HERE.md README-QA-V3.md
git commit -m "Add comprehensive e-commerce QA regression suite"
git push origin master
```
Use your actual branch if not `master`.

## Safety
- Read-only mode calls GET and invalid/unauthorized POST/PUT/PATCH/DELETE requests, which are expected to be denied. It does not intentionally create valid resources.
- Mutation mode creates and disables QA coupons, creates and deletes a QA address, and probes invalid inputs. **Only run on a separate QA database.** Some failed cleanup may leave test records. Back up QA DB before mutation mode.
- The suite uses existing GitHub repository secrets QA_USER_EMAIL, QA_USER_PASSWORD, QA_ADMIN_EMAIL, QA_ADMIN_PASSWORD.
- `pytest` assertions may uncover genuine bugs or incorrect business assumptions; report failures for review, not automatic changes to production security.
- The workflow does not deploy backend code or alter your existing Version 2 workflow.

## Scope limitations
- This package is NOT a guarantee of all possible bugs. It does not yet automate real payment processing, multi-user concurrent checkout, stock deduction, full product-image lifecycle, or browser UI workflows. These require isolated test-product setup and verified business behavior.
- Some security tests expect Spring Security to deny unauthorized requests with 401 or 403; unexpected behavior will be reported.
- Source review found product DTO numeric fields annotated with `@NotBlank`; this is a potential validation issue to investigate, not proof of a bug.
