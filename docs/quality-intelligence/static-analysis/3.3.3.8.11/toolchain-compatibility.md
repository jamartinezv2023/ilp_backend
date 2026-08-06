# Static Analysis Toolchain Compatibility

## Certified environment

- Java: 17
- Gradle: 8.6
- PMD: 7.26.0
- Spring Boot: 3.3.5
- Testcontainers: 1.21.4
- PostgreSQL test image: postgres:16-alpine

## PMD compatibility decision

PMD 7.6.0 generated an internal ASM parser exception while processing
the project bytecode.

PMD 7.26.0 completed the same analysis without parser exceptions and
without changing the Java or Gradle versions.

## Decision

PMD 7.26.0 is the certified static-analysis engine for the backend
release candidate.