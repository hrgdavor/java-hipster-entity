# Entity Interface Design (Starting Point)

The architecture is metadata-first: interface contracts and generated metadata are the primary outputs.
Runtime pieces built on top of that metadata, such as proxies, builders, factories, or adapters, are optional building blocks that projects may adopt selectively.

Related decisions and implementation guides:

- [Architecture decisions index](DECISIONS.md) — all architecture decisions (Accepted, Proposed, Trial, etc.)
- [DEC-014: EnumSet concrete dispatch strategy](decisions/DEC-014.md) — concrete dispatch specialization for change tracking
- [EnumSet implementation guide](enumset-implementation-and-jmh.md) — how-to reference with JMH profile and measured results

## 1. Core principle

All entities are defined as interfaces that intentionally align with records and not JavaBeans.

- A *base entity interface* is the minimal contract containing only the primary key.
- `hr.hrg.hipster.entity.api.EntityBase<ID>` marks an object as an entity type.

Example:

```java
import hr.hrg.hipster.entity.api.EntityBase;

public interface PersonEntity extends EntityBase<String> {
    // entity marker
}
```

## 2. Extended views

Additional entity views are defined as interfaces extending the base entity interface.
This makes each view a part of the same entity contract and allows safe type relationships.

Rules:
- Each entity lives in its own package: `hr.hrg.entity.<entity-name>`.
- The marker entity interface extends `EntityBase` and is empty (only identity).
- All views (summary/details/patch/fetch) are in the same package and extend the entity marker interface.

Example:

```java

public interface PersonSummary extends PersonEntity {
    String firstName();
    String lastName();
}

public interface PersonDetails extends PersonEntity {
    String email();
    String phoneNumber();
}
```

## 3. CRUD view layering for modern apps

- Persistence layer marker: `Auditable<ID>` for DB metadata fields (created/updated timestamps, actor IDs, and related audit data)
- Domain layer entity marker: `PersonEntity extends EntityBase<Long>`
- Business query views: `PersonSummary`, `PersonDetails`
- DTO output: `PersonDto extends PersonSummary`
- Input forms: `PersonCreateForm`, `PersonUpdateForm` (write-target, may overlap with DTOs)

### Sample architecture

```java
// API module
public interface Identifiable<ID> { ID id(); }
public interface Auditable<ID> extends Identifiable<ID> {
    Instant createdAt();
    Instant updatedAt();
}

// Domain module
public interface PersonEntity extends Auditable<Long> {}
public interface PersonSummary extends PersonEntity { String firstName(); String lastName(); }
public interface PersonDetails extends PersonSummary { String email(); String phoneNumber(); }

// API shapes
@View(read = BooleanOption.TRUE, write = BooleanOption.FALSE)
public interface PersonDto extends PersonSummary {}

@View(read = BooleanOption.FALSE, write = BooleanOption.TRUE)
public interface PersonCreateForm {
    String firstName();
    String lastName();
    String email();
}

@View(read = BooleanOption.FALSE, write = BooleanOption.TRUE)
public interface PersonUpdateForm extends PersonCreateForm, PersonEntity {}
```

## 4. Benefits

- Strong, compile-time separation of minimal identity and rich views
- Simple, interface-based composition and query projection support
- Clear semantics: anything that extends `EntityBase` is an entity
- Primitive return types (e.g., `int`) are normalized to boxed class in generated enum declarations, with JSON metadata carrying `type` as boxed class, `unboxed` as primitive type, and `primitive` marker.

## 5. Field source classification

Use `@FieldSource` on view interface methods to declare the data origin of each field:

| Kind | Meaning | Example |
|------|---------|---------|
| `COLUMN` | Directly mapped to a DB column (default) | `String firstName()` |
| `DERIVED` | Computed from other fields, not stored | `@FieldSource(kind=DERIVED, expression="...")` |
| `JOINED` | Sourced from a related table via join | `@FieldSource(kind=JOINED, relation="department.name")` |

When `@FieldSource` is absent, the field defaults to `COLUMN`.

```java
public interface PersonSummary extends PersonEntity {
    String firstName();                                          // COLUMN

    @FieldSource(kind = FieldKind.DERIVED, expression = "YEAR(NOW()) - YEAR(birthDate)")
    Integer age();                                               // DERIVED

    @FieldSource(kind = FieldKind.JOINED, relation = "department.name")
    String departmentName();                                     // JOINED
}
```

The metadata generator aggregates all fields from all views into an `allFields` list on the entity, tracking which views expose each field.

## 6. Guidelines

1. Keep `EntityBase` minimal; only include the primary key.
2. For each domain entity, define a root interface that extends `EntityBase`.
3. For optional / supplemental views, add extra interfaces extending the root entity interface.
4. Use naming conventions like `XEntity`, `XSummary`, `XDetails`.
5. View metadata enum (`<ViewName>Property`) must include:
   - all properties from transitive view/marker inheritance
   - the base `id` property first
   - no duplicate property entries (child property wins if present)
6. Annotate non-column fields with `@FieldSource` so generators can distinguish columns from derived/joined fields.

## 7. Entity metadata example

Repository tooling generates `X.metadata.json` and `XViewProperty` enums to avoid runtime reflection.

Example JSON for `Person`:

```json
{
  "entityName": "Person",
  "package": "hr.hrg.hipster.entity.person",
  "markerInterface": "PersonEntity",
  "idType": "Long",
  "views": [
    {
      "name": "PersonSummary",
      "extends": ["PersonEntity"],
      "read": true,
      "write": false,
      "properties": [
        {"name": "firstName", "type": "String"},
        {"name": "lastName", "type": "String"},
        {
          "name": "metadata",
          "type": {
            "type": "java.util.Map",
            "genericArguments": [
              {"type": "java.lang.String"},
              {
                "type": "java.util.List",
                "genericArguments": [{"type": "java.lang.Long"}]
              }
            ]
          }
        }
      ]
    }
  ]
}
```

Example generated enum:

```java
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public enum PersonSummaryProperty {
    firstName(java.lang.String.class),
    lastName(java.lang.String.class),
    metadata(TypeUtils.parameterizedType(java.util.Map.class, java.lang.String.class, TypeUtils.parameterizedType(java.util.List.class, java.lang.Long.class)));

    private final Type propertyType;

    PersonSummaryProperty(Type propertyType) {
        this.propertyType = propertyType;
    }

    public String getPropertyName() { return name(); }
    public Type getPropertyType() { return propertyType; }

    // Utility moved to hr.hrg.hipster.entity.api.TypeUtils and should be used by generated code.
}
```

