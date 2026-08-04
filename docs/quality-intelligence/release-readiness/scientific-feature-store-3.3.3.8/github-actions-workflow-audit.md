# GitHub Actions Workflow Audit

## Scientific Feature Store 3.3.3.8

| File | Workflow | Gradle | Test | JaCoCo verification | Checkstyle main | Checkstyle test | PMD main | SpotBugs main | bootJar | Excludes adaptive tests |
|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|

## Classification rules

- CANONICAL: executes the complete approved Gradle gate.
- SPECIALIZED: validates a distinct concern such as BDD, contracts or integration.
- DUPLICATE: repeats the same global quality responsibility.
- NON_COMPLIANT: excludes required tests or omits mandatory quality controls.
- REVIEW_REQUIRED: purpose cannot be determined automatically.

## Initial findings


Final disposition of legacy workflows requires review of their complete jobs and triggers.
