# Reproducible Persistence Certification

## Cycle

3.3.3.8.10 — Reproducible Persistence & Cross-Platform Certification

## Certified scenarios

### Historical PostgreSQL installation

Migration history:

- Baseline 0 — ILP existing schema baseline
- V1 — create assessment scientific persistence
- V2 — create scientific feature store

Validation result:

- Flyway validate: PASS
- History mutation: NONE
- Before/after SHA-256: IDENTICAL
- V1 checksum: PRESERVED
- V2 checksum: PRESERVED

### Empty PostgreSQL installation

Bootstrap result:

- Applied migration: B2__ilp_schema_baseline.sql
- Migration type: SQL_BASELINE
- Target version: 2
- Application tables: 39
- Flyway control tables: 1
- Hibernate validation: PASS

## Architectural decision

Flyway is the source of truth for the PostgreSQL server schema.

Hibernate must validate mappings and must not create scientific
or production database structures.

Historical V1 and V2 migrations are immutable.

New PostgreSQL installations use B2 as the accumulated schema baseline.

Offline clients do not execute PostgreSQL migrations. They use an
independent IndexedDB or SQLite schema and synchronize through the API.