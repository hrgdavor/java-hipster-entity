# Materialization Guide

`hipster-entity` is designed so you can start small and add layers later.

## Level 0 — Minimal

Start with a view interface and generated field metadata.
This gives you compile-time field definitions and `ViewMeta` support without any concrete implementation.

What you get:

- `PersonSummary_` metadata enum
- `ViewMeta` runtime support
- a compact, interface-first contract

Use this when you want schema metadata and generic adapters.

## Level 1 — Record

Add a record implementation or use generated record materialization.
This gives you a concrete immutable object with the same interface shape.

What you get:

- immutable data objects
- easy integration with serialization frameworks
- a simple, stable runtime representation

## Level 2 — Write interface

Add an update-capable view interface for forms or patch payloads.
This enables proxy-backed setters or generated write helpers.

What you get:

- explicit write-target contracts
- separate read and write shapes
- safer update flows

## Level 3 — Builder

Add a generated builder to construct views fluently.
Builders can also track changes and support merge semantics.

What you get:

- fluent construction APIs
- change tracking for partial updates
- a clearer creation workflow

## Choosing a level

| Need | Recommended level |
|---|---|
| Read-only view metadata | Level 0 |
| Immutable DTO or JSON shape | Level 1 |
| Form/update payloads | Level 2 |
| Fluent creation and patch support | Level 3 |

## See also

- [Getting Started](getting-started.md)
- [Core Concepts](core-concepts.md)
