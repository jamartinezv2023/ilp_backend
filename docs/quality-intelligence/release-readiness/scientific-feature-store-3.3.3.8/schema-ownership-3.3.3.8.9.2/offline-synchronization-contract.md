# Offline Synchronization Contract

## Scientific Feature Store 3.3.3.8

### Architectural principles

- Flyway owns the PostgreSQL server schema.
- Hibernate validates mappings and does not create production tables.
- Offline clients use a local database independent from PostgreSQL migrations.
- Identifiers must be generated without requiring network connectivity.
- Synchronization operations must be idempotent.
- Deletions must be representable as tombstones when records are synchronized.
- The backend must reject duplicate synchronization commands safely.

### Initial conflict policy

- Immutable scientific observations: reject conflicting mutation.
- Draft assessment responses: last-write-wins only when version checks succeed.
- Completed assessment responses: server state is authoritative.
- Scientific feature vectors: append or generate new version; do not overwrite historical vectors.

### Planned synchronization metadata

| Field | Purpose |
|---|---|
| origin_node_id | Identifies the offline device or institutional node |
| client_operation_id | Makes synchronization requests idempotent |
| client_created_at | Preserves local creation time |
| server_received_at | Records ingestion time |
| version | Supports optimistic concurrency |
| deleted_at | Represents synchronized logical deletion |
| sync_status | Tracks pending, synchronized or conflicted state on the client |

### Deferred implementation

- Client outbox
- Server synchronization inbox
- Conflict-resolution endpoint
- Incremental pull cursor
- IndexedDB or SQLite adapter
