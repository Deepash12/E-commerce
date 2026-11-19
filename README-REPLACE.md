# QA replacement package

Copy the `tests/` folder and `.github/workflows/ecommerce-comprehensive-qa.yml` into the ROOT of your existing backend repository, replacing files with the same names. The archive also includes requirements-qa.txt and pytest.ini.

This is a drop-in replacement of the existing comprehensive QA package, **not** a replacement for your Java backend.

Changes: function-scoped auth fixtures; coupon >100% validation isolated; address success plain-text handling and listing parsing; invalid cart quantities tested with an actual catalog product; clearer 401 diagnostics. All existing readonly test cases retained.

Run mutations ONLY if the configured QA_BASE_URL connects to an isolated QA backend/database. The workflow currently uses https://e-commerce-2-ibeq.onrender.com; confirm that service uses an isolated QA database before enabling run_mutations.

If 401 persists, it is NOT silently treated as a pass: inspect account roles, JWT validity and backend authorization. No test package can guarantee passing without running against the actual QA deployment.
