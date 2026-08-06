# Offline-First Architecture

## Purpose

Guarantee continuity of educational and scientific operations in
institutions where Internet connectivity is intermittent or unavailable.

## Operating modes

1. Cloud-connected mode
2. Institution-local server mode
3. Browser offline mode
4. Deferred synchronization mode

## Persistence responsibilities

### PostgreSQL central

Stores canonical synchronized institutional and scientific data.

### PostgreSQL institutional

Supports local institutional operation when Internet access is
unavailable. The schema is managed by Flyway using the same approved
baseline and versioned migrations as the central backend.

### IndexedDB

Stores browser drafts, pending commands, synchronization metadata and
the minimum reference data required for offline operation.

### Hibernate

Validates PostgreSQL mappings. It must not create or modify production
or scientific schemas.

### Flyway

Owns PostgreSQL central and institutional schemas. It does not manage
IndexedDB or SQLite.

## Synchronization aggregates

- SyncNode
- SyncOperation
- SyncCursor
- SyncConflict
- SyncAudit

## Required operation metadata

- operationId
- originNodeId
- entityType
- entityId
- operationType
- aggregateVersion
- logicalTimestamp
- clientCreatedAt
- serverReceivedAt
- payloadHash

## Idempotency

Every offline command must contain a globally unique
client operation identifier.

The server must enforce uniqueness for clientOperationId so repeated
delivery does not create duplicate scientific or educational records.

## Conflict policy

- Draft assessment responses use optimistic concurrency.
- Completed assessments are immutable.
- Scientific feature vectors are append-only and versioned.
- Consent records cannot be silently overwritten.
- Deletions are represented through tombstones.
- Conflicts must generate auditable records.

## Client persistence

The React client will evolve into a PWA with:

- service worker;
- application shell caching;
- IndexedDB;
- local outbox;
- connectivity detection;
- retry with exponential backoff;
- explicit synchronization state;
- conflict notification.

## Deferred implementation

This cycle defines and certifies the architecture only.

IndexedDB adapters, synchronization APIs, server inbox/outbox,
conflict resolution and PWA installation are implemented in a
subsequent controlled cycle.