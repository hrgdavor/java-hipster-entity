# Core Concepts

## Interface-first entities

In `hipster-entity`, entities are defined as interfaces rather than concrete POJOs.
That means the shape of your data is expressed directly in Java method signatures.

Example:

```java
public interface PersonEntity extends EntityBase<String> {
    String id();
}

public interface PersonSummary extends PersonEntity {
    String firstName();
    String lastName();
    String email();
}
```

## Views

A view is a projection of an entity. Views extend a base entity interface and add fields.
Common view types include:

- `Summary` — a compact read-only snapshot
- `Details` — a richer read model
- `UpdateForm` — a write-target view for updates

## Field metadata

The tooling generates a companion enum for each view. The enum is named with an underscore suffix,
for example `PersonSummary_`.

That enum contains:

- field names that exactly match accessor names
- field type metadata
- a `forName(String)` lookup method
- a `ViewMeta` instance for generic runtime use

This makes your schema available at compile time without reflection.

## EntityBase vs Identifiable

- `EntityBase<ID>` marks a type as an entity type.
- `Identifiable<ID>` is used when the view has an explicit identity method such as `id()`.

Use `Identifiable` only when identity is meaningful for the view.

## Field sources

Fields can represent different kinds of data:

- `COLUMN` — regular stored data
- `DERIVED` — computed values
- `JOINED` — values from a related entity or joined table

For users, this means you can express whether a field is stored, computed, or sourced from another object.

## Materialization levels

`hipster-entity` supports an adoption ladder:

- **Minimal** — interface + generated metadata
- **Record** — interface + immutable record implementation
- **Write interface** — add update-capable shapes
- **Builder** — fluent construction and change tracking

For a deeper explanation, see [Materialization Guide](materialization-guide.md).
