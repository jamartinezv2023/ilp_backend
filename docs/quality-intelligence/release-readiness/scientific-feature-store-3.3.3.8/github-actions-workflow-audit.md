# GitHub Actions Workflow Audit

## Scientific Feature Store 3.3.3.8

| File | Workflow | Gradle | Test | JaCoCo verification | Checkstyle main | Checkstyle test | PMD main | SpotBugs main | bootJar | Excludes adaptive tests |
|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| backend-canonical-quality-gate.yml | ILP Backend Canonical Quality Gate | True | True | True | True | True | True | True | True | False |
| backend-enterprise-ci.yml | Backend Enterprise CI | True | True | False | False | False | False | False | False | False |
| backend-fieldwork-ci.yml | ILP Backend Fieldwork CI | True | True | False | True | False | True | True | False | False |
| backend-quality.yml | ILP Backend Quality Pipeline | True | True | True | False | False | True | True | False | True |
| bdd-screenplay-tests.yml | ILP Backend BDD Screenplay Tests | True | True | False | False | False | False | False | True | False |
| ci-cd.yml | ILP Backend CI | True | True | False | False | False | False | False | False | False |
| consumer-contract-tests.yml | ILP Backend Consumer Contract Tests | True | True | False | False | True | False | False | False | False |
| integration-tests.yml | ILP Backend Integration Tests | True | True | False | False | False | False | False | False | False |
| postgres-audit.yml | PostgreSQL CI/CD Audit | False | False | False | False | False | False | False | False | False |
| quality-gate.yml | ILP Backend Quality Gate | True | True | False | False | False | False | False | False | True |
| secret-scanning.yml | Secret Scanning | False | False | False | False | False | False | False | False | False |
| sonarcloud.yml | SonarCloud Analysis | True | False | False | False | False | False | False | False | False |

## Classification rules

- CANONICAL: executes the complete approved Gradle gate.
- SPECIALIZED: validates BDD, contracts, integration, security or infrastructure.
- DUPLICATE: repeats the global quality responsibility.
- NON_COMPLIANT: excludes required tests or omits mandatory controls.
- REVIEW_REQUIRED: requires manual inspection.

## Findings

- NON_COMPLIANT: backend-quality.yml excludes :adaptive-education-service:test.
- NON_COMPLIANT: quality-gate.yml excludes :adaptive-education-service:test.

- CANONICAL WORKFLOW STATUS: FAIL.
- BLOCKER: Flyway migration cannot be applied to the H2 test database.
- EFFECT: 8 adaptive-education-service tests failed in GitHub Actions.
- TAG AUTHORIZATION: NOT AUTHORIZED.
