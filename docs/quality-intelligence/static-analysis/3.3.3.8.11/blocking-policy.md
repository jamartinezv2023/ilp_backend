# Static Analysis Blocking Policy

## PMD

- PMD violations are release-blocking.
- Main and test analysis must complete successfully.
- Parser exceptions are not accepted.
- New violations are prohibited.
- Global suppression is prohibited.
- Local suppression requires explicit domain justification.

## SpotBugs

- Security findings are release-blocking.
- Priority 1 findings are release-blocking.
- Correctness findings must be corrected or explicitly classified.
- Historical exclusions must identify the exact class and bug pattern.
- New exclusions require documented approval.

## Release criterion

A release candidate cannot be certified unless the complete quality
gate finishes successfully.