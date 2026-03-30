# Roadmap & Implementation Status

This document tracks roadmap progress, current work, and changes in direction for the hipster-entity project.

## 1. Roadmap Checklist

- [ ] Core entity interface contract (marker interface, per-package semantics)
- [ ] View interface hierarchy rules (summary/details/update patterns)
- [ ] Type divergence analyzer + converter manifest generation
- [ ] Projection + DTO marker pattern for SQL/NoSQL direct JSON output
- [ ] Mapper generation from `TypeDescriptor` deep flags
- [ ] Annotation metadata exposure in generated view enums (FieldAnnotation)
- [ ] API/core/module responsibility split enforced by generator

## 2. Milestone status

| Milestone                     | Owner     | Status      | Notes                       |
| ----------------------------- | --------- | ----------- | --------------------------- |
| `EntityBase` marker API       | Core team | Done        | In `hipster-entity-api`     |
| Converter manifest proposal   | Core team | In progress | Design in `doc/brainstorm`  |
| SQL/Mongo projection pipeline | TBD       | Draft       | Begin in `doc/brainstorm`   |
| Roadmap/architecture split    | Infra     | Done        | Folder restructure complete |

## 3. Decision traceability

| Decision  | Topic                                      | Delivery status | Notes                                                                                       |
| --------- | ------------------------------------------ | --------------- | ------------------------------------------------------------------------------------------- |
| `DEC-001` | Interface-first entity model               | Accepted        | Root contract and naming rules are documented                                               |
| `DEC-002` | Doc structure separation                   | Implemented     | Folder split and linked docs exist                                                          |
| `DEC-003` | Projection-oriented read path              | Proposed        | Needs adapter shape and benchmark criteria                                                  |
| `DEC-004` | Generated metadata over reflection         | Proposed        | Needs metadata sufficiency and versioning policy                                            |
| `DEC-005` | Field-source semantics                     | Proposed        | Needs write-path rules and diagnostics policy                                               |
| `DEC-006` | Build-time type divergence validation      | Proposed        | Needs converter registry and validation UX                                                  |
| `DEC-007` | Projection performance vs ergonomics       | Proposed        | Needs layered API examples and benchmarks                                                   |
| `DEC-008` | Builder policy and naming guarantees       | Proposed        | Needs final builder API and merge policy decisions                                          |
| `DEC-009` | Source-visible generation strategy         | Proposed        | Needs freeze semantics, patching rules, and sidecar workflow                                |
| `DEC-010` | Proxy-backed entity/view bridge            | Proposed        | Needs dispatch rules, strict diagnostics defaults, and proxy vs generated benchmarks        |
| `DEC-011` | Automatic builder interface generation     | Proposed        | Needs canonical builder interface contract and compatibility test kit                       |
| `DEC-012` | Update-array and change-tracking semantics | Proposed        | Needs touched/dirty/null semantics and merge-mode contract                                  |
| `DEC-013` | Optional per-view impl. selection factory  | Proposed        | Optional module; needs override precedence, fallback policy, and provider ordering contract |

## 4. Direction change log

- `2026-03-30`: moved docs into `doc/brainstorm`, `doc/architecture`, `doc/roadmap`.
- `2026-03-30`: added projection/JSON streaming path section to brainstorm.

## 5. Entity Rule Quick Reference

This section is a delivery-oriented summary. Architecture documents remain the source of truth:
- [Entity interface design](../architecture/README.md)
- [Architecture decisions](../architecture/DECISIONS.md)

Current high-level rules:

1. Entity contract shape
- Every entity SHOULD have a minimal marker/root interface.
- The root interface MUST extend `hr.hrg.hipster.entity.api.EntityBase<IdT>`.
- Views SHOULD extend the root interface rather than redefining identity independently.

2. Package and naming conventions
- Each entity SHOULD live in its own package.
- View names SHOULD follow stable patterns such as `Summary`, `Details`, and `Update`.
- Accessors SHOULD remain record-style property methods.

3. Read/write semantics
- Views MAY declare mode semantics such as read-only or write-only.
- Derived and joined fields SHOULD not silently participate in normal write-path generation.

4. Module boundaries
- `hipster-entity-api` MUST contain shared contracts and annotations only.
- `hipster-entity-core` SHOULD contain infrastructure and generic behavior.
- `hipster-entity-example` MAY act as the canonical validation/demo module for concrete interfaces.

5. Tooling expectations
- Tooling MUST validate marker inheritance, package structure, and generated metadata assumptions.
- Generation SHOULD remain deterministic and aligned with the accepted ADR set.
