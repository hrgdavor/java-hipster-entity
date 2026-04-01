# hipster-entity-api

This module contains the base API contracts for entities and shared annotations.

## Intent

- `EntityBase` is the canonical marker for identity semantics.
- All core and tooling modules depend on this module for surface interface types.
- Additional annotations or shared API interfaces can be added here (e.g., `@Entity`, `@Id`, `@ReadOnly`).
