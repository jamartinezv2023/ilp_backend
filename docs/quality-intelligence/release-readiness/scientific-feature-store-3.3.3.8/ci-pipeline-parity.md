# Scientific Feature Store 3.3.3.8
## CI Pipeline Parity Matrix

| Quality activity | Local canonical pipeline | GitHub Actions | Azure DevOps | Release requirement |
|---|---:|---:|---:|---:|
| Clean workspace | PASS | PARTIAL | NOT_ALIGNED | REQUIRED |
| Compile and build | PASS | PASS | PARTIAL | REQUIRED |
| Unit and integration tests | PASS | PARTIAL | PARTIAL | REQUIRED |
| JaCoCo report | PASS | PARTIAL | MISSING | REQUIRED |
| JaCoCo verification | PASS | PARTIAL | MISSING | REQUIRED |
| Checkstyle main | PASS | PARTIAL | MISSING | REQUIRED |
| Checkstyle test | PASS | PARTIAL | MISSING | REQUIRED |
| PMD main | PASS | PARTIAL | MISSING | REQUIRED |
| SpotBugs main | PASS_WITH_CONFIGURED_POLICY | PARTIAL | MISSING | REQUIRED |
| Boot JAR | PASS | PARTIAL | MISSING | REQUIRED |

## Canonical Gradle command

```text
./gradlew clean build test jacocoTestReport
jacocoTestCoverageVerification checkstyleMain checkstyleTest
pmdMain spotbugsMain bootJar
--no-daemon --no-configuration-cache
```

## GitHub Actions assessment

GitHub Actions contains multiple backend workflows. Current evidence does not
yet demonstrate complete parity across all mandatory workflows.

A workflow that excludes `:adaptive-education-service:test` cannot be classified
as a complete release-quality gate for the Scientific Feature Store.

Current classification: PARTIAL.

## Azure DevOps assessment

The versioned primary Azure DevOps pipeline uses Maven `clean package`, while
the current backend release baseline is validated through Gradle 8.6.

It does not execute JaCoCo verification, Checkstyle, PMD, SpotBugs and bootJar
using the canonical Gradle task set.

Current classification: NOT_ALIGNED.

The PostgreSQL audit pipeline is specialized infrastructure verification and is
not a substitute for the Java quality pipeline.

## Release policy

The release tag is not authorized while mandatory GitHub Actions workflows are
red or while CI parity remains unverified.

Azure DevOps alignment may be completed in a dedicated CI migration cycle.
Until then, its non-parity must remain explicitly documented.
