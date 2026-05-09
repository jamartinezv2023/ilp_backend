# Event Contracts v1

This directory contains versioned JSON contracts for shared domain events.

These contracts are used to protect compatibility between services in an event-driven architecture.

## Rules

- Do not remove required fields from an existing version.
- Do not change field meanings in-place.
- Additive changes should be backward compatible.
- Breaking changes require a new version directory, for example `v2`.
