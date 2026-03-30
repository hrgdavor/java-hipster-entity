# Architecture Decisions

This document records architecture decisions that have moved beyond brainstorming and are treated as current project direction.

Use this file as a lightweight ADR log:
- `Proposed` for a candidate decision under review
- `Trial` for a direction being exercised before full acceptance
- `Accepted` for an agreed direction
- `Superseded` when replaced by a newer decision
- `Rejected` when explicitly not adopted

Normative terms follow RFC-style intent:
- `MUST` = required invariant
- `SHOULD` = recommended default
- `MAY` = optional behavior

## Decision Template

```md
## DEC-XXX: Short title

- Status: Proposed | Trial | Accepted | Superseded | Rejected
- Date: YYYY-MM-DD
- Owners: team or person
- Related docs: links to brainstorm / roadmap / code
- Supersedes: DEC-... | -
- Superseded by: DEC-... | -

### Context
Why this decision is needed.

### Decision
What is being decided.

### Consequences
- Positive effects
- Negative effects
- Follow-up work

### Acceptance criteria
- Observable condition 1
- Observable condition 2
```

## DEC-001: Interface-first entity model

- Status: Accepted
- Date: 2026-03-30
- Owners: project
- Related docs: [Entity interface design](README.md), [DEC-001 brainstorm](../brainstorm/dec-001-interface-first-entity-model.md), [Roadmap](../roadmap/README.md)
- Supersedes: -
- Superseded by: -

### Context
The project needs a stable entity contract that supports generated metadata, compile-time analysis, and multiple view shapes without forcing JavaBean-style mutable classes.

### Decision
Domain entities are modeled as interfaces. Each entity has a minimal marker/root interface, and additional views extend that base interface to express read, write, summary, and details shapes.

The model is intentionally aligned with record-style naming conventions:
- Accessor methods should use property-style names (for example `id()`, `firstName()`, `age()`) rather than JavaBean getter names.
- Generated metadata and code generation should treat these methods as record-like components.

The project is metadata-first:
- Interface contracts and generated metadata are the canonical outputs.
- Runtime building blocks such as builders, proxies, factories, and adapters are optional layers that may be adopted selectively.
- Projects may consume the metadata directly and skip optional runtime modules entirely.

Generated builder usage is explicitly supported as a compatibility layer:
- Builders are optional generated construction helpers for DTO/materialized outputs.
- Builders must follow the same property naming contract as the record-aligned accessors.
- Builder generation must not redefine the architectural source of truth; interfaces and metadata remain canonical.

### Consequences
- Improves compatibility with generated metadata and projection-oriented tooling.
- Keeps domain contracts small and composable.
- Requires generators and documentation to handle inheritance and transitive property aggregation correctly.
- Enables consistent naming across interfaces, potential records, and generated builders.
- Reduces adapter glue when moving between interface projections and generated immutable/materialized forms.

### Acceptance criteria
- Entity contracts MUST remain interface-first, with a minimal root or marker interface per entity.
- Accessors SHOULD remain record-style property methods rather than JavaBean getters.
- Generated metadata and builder policy MUST treat interface methods as the canonical contract surface.
- Optional runtime modules MUST NOT become a hard prerequisite for consuming entity metadata.

## DEC-002: Separate brainstorming, architecture, and roadmap documentation

- Status: Accepted
- Date: 2026-03-30
- Owners: project
- Related docs: [Docs index](../README.md), [Brainstorm](../brainstorm/README.md), [DEC-002 brainstorm](../brainstorm/dec-002-doc-structure-separation.md), [Roadmap](../roadmap/README.md)
- Supersedes: -
- Superseded by: -

### Context
Concept exploration, agreed architecture, and implementation tracking serve different purposes and become hard to manage when mixed in one flat documentation area.

### Decision
Documentation is split into three top-level categories under `doc/`:
- `brainstorm` for exploratory design work
- `architecture` for agreed direction and stable contracts
- `roadmap` for progress tracking and changes in direction

### Consequences
- Makes document intent clearer.
- Reduces ambiguity about whether a proposal is exploratory or accepted.
- Requires lightweight cross-linking between folders so related material stays discoverable.

### Acceptance criteria
- New exploratory design material SHOULD go under `doc/brainstorm`.
- Accepted architectural direction MUST be represented in `doc/architecture`.
- Execution status and sequencing SHOULD be tracked in `doc/roadmap`.

## DEC-003: Projection-oriented read path is a first-class architecture topic

- Status: Proposed
- Date: 2026-03-30
- Owners: project
- Related docs: [Brainstorm proposals](../brainstorm/README.md), [DEC-003 brainstorm](../brainstorm/dec-003-projection-oriented-read-path.md), [Field type divergence](field-type-divergence.md)
- Supersedes: -
- Superseded by: -

### Context
The project is exploring DTO interfaces used as read markers for SQL and NoSQL queries, including direct JSON output paths that bypass full entity materialization.

### Decision
Treat projection-based reads, streaming JSON output, and low-allocation mapping as an explicit architecture track rather than an incidental generator optimization.

### Consequences
- Encourages design work around performance, memory usage, and GC pressure early.
- Keeps developer ergonomics in scope instead of optimizing only for throughput.
- Needs follow-up design on adapter APIs, driver integration points, and benchmark criteria before acceptance.

### Out of scope
- Write-path behavior — this decision covers read paths only; write semantics are covered by DEC-005.
- Specific driver integration (JDBC, R2DBC, MongoDB) — integration points are follow-up design work, not resolved here.
- Caching or result memoization — projection responsibility ends at mapping; retention strategy is separate.
- Benchmark tooling infrastructure — benchmarks are a prerequisite for acceptance but are not defined within this decision.

### Acceptance criteria
- Read-path design MUST explicitly define projection contracts separate from write-path assumptions.
- The chosen approach SHOULD support SQL and NoSQL read use cases.
- Adapter API and benchmark criteria MUST be documented before acceptance.

## DEC-004: Generated metadata is preferred over runtime reflection

- Status: Proposed
- Date: 2026-03-30
- Owners: project
- Related docs: [Entity interface design](README.md), [Brainstorm proposals](../brainstorm/README.md), [DEC-004 brainstorm](../brainstorm/dec-004-generated-metadata-over-reflection.md), [Annotation processing vs generated metadata rationale](../brainstorm/annotation-processing-vs-generated-metadata.md)
- Supersedes: -
- Superseded by: -

### Context
The project relies on interface-based entity and view contracts. Tooling needs structural information such as property names, generic types, inheritance, field origin, and annotations without paying repeated runtime reflection costs or requiring broad runtime classpath inspection.

### Decision
Prefer generated metadata artifacts and generated helper types as the primary way to expose entity and view structure. Runtime reflection should be treated as a fallback or development aid, not the main architecture.

Where practical, the project should prefer explicit generated helper code over repeated reflective adaptation so the extracted structure can be used directly in hot paths.

### Consequences
- Reduces runtime introspection cost and improves determinism.
- Moves structural discovery work out of startup/runtime and into explicit generated artifacts.
- Improves portability for downstream tooling because metadata can be consumed without requiring a JVM reflection phase.
- Creates a clearer optimization path than reflection-heavy models because structure is materialized explicitly rather than rediscovered dynamically.
- Makes it easier to generate direct mappers, serializers, validators, and adapters that reduce runtime allocation and repeated reflective lookup.
- Keeps generator responsibilities central and explicit.
- Increases pressure on code generation quality, metadata versioning, and backward compatibility.

### Out of scope
- Annotation vocabulary definition — which annotations exist and what they mean semantically is addressed in DEC-005.
- Build plugin or generator invocation mechanism — how generation is triggered is addressed in DEC-009.
- Runtime serialization formats — metadata is a structural tooling artifact, not a wire or persistence format.
- Metadata storage format on disk — format details (class, JSON, binary) are follow-up implementation choices.

### Acceptance criteria
- Generated metadata MUST be sufficient for primary tooling scenarios without requiring runtime reflection.
- Reflection MAY exist only as a fallback or diagnostics aid.
- Metadata versioning and compatibility expectations SHOULD be explicit before acceptance.

## DEC-005: Field-source semantics drive mapper and write-path behavior

- Status: Proposed
- Date: 2026-03-30
- Owners: project
- Related docs: [Entity interface design](README.md), [Field type divergence](field-type-divergence.md), [DEC-005 brainstorm](../brainstorm/dec-005-field-source-semantics.md)
- Supersedes: -
- Superseded by: -

### Context
The project already distinguishes direct columns from derived and joined fields. Those distinctions are architectural, not cosmetic, because they affect what can be written back, what requires query support, and what should be validated or skipped.

### Decision
Treat field-source metadata such as `COLUMN`, `DERIVED`, and `JOINED` as a first-class semantic input to generator logic. Read mapping, write mapping, validation, and diagnostics should all use this classification.

### Consequences
- Clarifies generator behavior for fields that are not directly persisted.
- Improves safety for write models by preventing accidental persistence of derived or relation-backed values.
- Requires consistent annotation semantics and explicit handling of ambiguous cases.

### Out of scope
- SQL or query generation — field semantics classify fields; how joins or derived expressions are queried is a separate responsibility.
- UI-layer or business validation — write-path safety here concerns persistence correctness, not form validation.
- Join execution strategy — `JOINED` classifies field origin; how joins are performed by the underlying store is not addressed here.
- Nullability policy — null handling in mapping is a separate concern from field-source classification.

### Acceptance criteria
- Field origin MUST distinguish at least `COLUMN`, `DERIVED`, and `JOINED` semantics.
- Write-path generation MUST not silently treat derived/joined fields like ordinary persisted columns.
- Diagnostics SHOULD surface ambiguous or invalid write behavior.

## DEC-006: Type divergence should fail early through build-time validation

- Status: Proposed
- Date: 2026-03-30
- Owners: project
- Related docs: [Field type divergence](field-type-divergence.md), [Brainstorm proposals](../brainstorm/README.md), [DEC-006 brainstorm](../brainstorm/dec-006-build-time-type-divergence-validation.md)
- Supersedes: -
- Superseded by: -

### Context
Different views may intentionally expose the same logical field with different Java types. If converter requirements are discovered only at runtime, failures become harder to diagnose and may surface late in production-like flows.

### Decision
Detect type divergence during generation or build validation and require explicit converter coverage for incompatible source/target type pairs.

### Consequences
- Moves mapping failures earlier in the development cycle.
- Makes converter requirements visible and reviewable.
- Introduces additional generator diagnostics and converter registry design work.

### Out of scope
- Converter implementation — converters are declared and registered; this decision does not prescribe how conversion logic is written.
- Runtime type checking — validation is build/generation time only; runtime type mismatches are not addressed here.
- Nullability and null-propagation policy — null handling is a separate concern from type compatibility.
- Custom type mapping for third-party types — the registry contract is in scope; populating it for external libraries is follow-up work.

### Acceptance criteria
- Divergent source/target type pairs MUST be discoverable during generation or build validation.
- Missing required converters MUST fail with actionable diagnostics.
- Boxing/unboxing-only differences SHOULD not require semantic converters.

## DEC-007: Projection-based reads must balance performance and ergonomics

- Status: Proposed
- Date: 2026-03-30
- Owners: project
- Related docs: [Brainstorm proposals](../brainstorm/README.md), [DEC-007 brainstorm](../brainstorm/dec-007-projection-performance-ergonomics-balance.md), [Roadmap](../roadmap/README.md)
- Supersedes: -
- Superseded by: -

### Context
One of the most promising design directions is using DTO interfaces as lightweight read contracts for SQL and NoSQL queries, potentially short-circuiting directly into JSON output without full entity or DTO object materialization. That direction has real performance upside, but it can also become unpleasant if the API is too low-level or fragile.

### Decision
If projection-based read paths are adopted, they must be designed as explicit developer-facing APIs, not only as an internal optimization path. Performance and low allocation matter, but the model must stay understandable and composable for application developers.

### Consequences
- Keeps ergonomics in scope alongside throughput and memory use.
- Encourages stable APIs around projection binders, JSON sinks, or generated projection adapters.
- Requires benchmarks and examples to justify complexity before the ADR can be accepted.

### Out of scope
- SQL query optimisation or query plan control — projection adapters consume query results; they do not influence query shape.
- Caching or pooling of projection instances — out of scope until allocation patterns are established by benchmarks.
- Mutation operations — this decision covers the read path only; write semantics are addressed in DEC-005.
- Streaming pagination — how large result sets are paginated is a driver/query concern, not a projection-adapter concern.

### Acceptance criteria
- Performance-oriented projection APIs MUST have a developer-facing shape, not only an internal optimization path.
- The design SHOULD define at least one ergonomic materialization path and one lower-level fast path.
- Benchmarks and examples MUST exist before acceptance.

## DEC-008: Generated builder policy and naming guarantees

- Status: Proposed
- Date: 2026-03-30
- Owners: project
- Related docs: [Entity interface design](README.md), [ADR guide for developers](ADR-GUIDE.md), [DEC-008 brainstorm](../brainstorm/dec-008-builder-policy-and-naming-guarantees.md), [Roadmap](../roadmap/README.md)
- Supersedes: -
- Superseded by: -

### Context
The architecture already favors interface-first modeling and record-aligned accessor names. As generated materialized DTO forms and construction helpers are introduced, developer experience and interoperability depend on stable naming and predictable builder behavior.

### Decision
Builder generation is supported as an optional companion to interface and metadata generation, with strict naming alignment:
- Property accessors remain record-style (for example: `id()`, `firstName()`, `age()`).
- Generated builder methods must map one-to-one to the same logical property names.
- Builder APIs must be deterministic across generator runs (ordering and method names should not drift).
- Builders are convenience construction artifacts; they do not replace interface contracts or generated metadata as architectural source of truth.
- Generated builders must provide a constructor or factory that accepts an instance implementing the source interface, enabling direct materialization from projection-only paths.
- Constructor-based ingestion is the primary and preferred fast path for ergonomics and performance.
- Update-via-view workflows are optional and may be provided as `mergeFrom(...)` style methods.

Recommended generated API shape:

```java
public final class PersonSummaryBuilder {
	public PersonSummaryBuilder() {}

	// Copy from any interface-compatible view
	public PersonSummaryBuilder(PersonSummary source) {
		this.id(source.id());
		this.firstName(source.firstName());
		this.lastName(source.lastName());
	}

	// Optional: update existing builder state from another view (patch-like)
	public PersonSummaryBuilder mergeFrom(PersonSummary source) {
		return this
			.id(source.id())
			.firstName(source.firstName())
			.lastName(source.lastName());
	}
}
```

### Consequences
- Improves ergonomics for constructing materialized outputs while preserving projection-friendly contracts.
- Reduces mapping glue between generated builders, records, and interface projections.
- Adds generator compatibility responsibilities, including naming stability and backward-compatibility expectations for generated builder APIs.
- Makes non-materialized read paths practical by allowing direct copy into a generated materialized form.
- Keeps default usage simple: `new Builder(view)` is canonical.
- If merge methods are generated, they require clear merge semantics (overwrite rules and optional null-handling policy).

### Out of scope
- Input validation — builders are pure construction helpers; validation belongs at the service or use-case layer.
- Thread safety — builders are not required to be thread-safe; callers own the builder lifecycle.
- Serialization shape — builder structure does not need to match any wire or persistence format.
- Immutability of the produced object — builders produce a materialized output; whether that output is immutable is the consumer's responsibility.

### Acceptance criteria
- Generated builders MUST support constructor or factory ingestion from an interface-compatible source.
- Constructor-based ingestion SHOULD remain the primary ergonomic and performance path.
- Optional merge methods MAY exist only with explicit merge semantics.

## DEC-009: Source-visible generation strategy and developer-safe tooling workflow

- Status: Proposed
- Date: 2026-03-30
- Owners: project
- Related docs: [ADR guide for developers](ADR-GUIDE.md), [Entity interface design](README.md), [DEC-009 brainstorm](../brainstorm/dec-009-source-visible-generation-strategy.md), [Annotation processing vs generated metadata rationale](../brainstorm/annotation-processing-vs-generated-metadata.md), [Roadmap](../roadmap/README.md)
- Supersedes: -
- Superseded by: -

### Context
Experience with annotation processing and bytecode-generation pipelines shows recurring issues: difficult debugging, weak flexibility during partial refactors, opaque generated outputs, and poor ergonomics when projects are temporarily in a broken syntax state. The project needs a generator model that keeps outputs understandable, reviewable, and manually maintainable when needed.

### Decision
Adopt a source-visible, parser-driven generation strategy with developer safety controls:
- Prefer JavaParser-based source analysis over annotation processing as the primary generation path.
- Avoid bytecode generation; generate Java source only.
- Generated boilerplate should be human-readable and checked into source control by default to improve visibility and reduce repeated build-time generation cost.
- Generation must support a freeze option so generated code can be taken over and maintained manually during serious issues.
- Generator is allowed to enhance existing files (in-file augmentation) instead of only creating separate files; for example, generated builders may be emitted inside the interface file.
- Invest in sidecar tooling workflows that support editor-aware operations such as undo-friendly changes.
- Sidecar generation may skip writes when source has syntax errors to avoid noisy or misleading generated output.

### Consequences
- Improves transparency and debuggability because generated artifacts are visible as normal Java source.
- Avoids hiding critical generation behavior inside compiler-attached pipelines that are harder to inspect and reason about.
- Supports editor-first and partial-refactor workflows more naturally than annotation-processing-driven generation.
- Preserves a path to reflection-light and runtime-friendly execution because source-known structure can be materialized ahead of time.
- Reduces CI/build overhead from always-on full regeneration when checked-in outputs are up to date.
- Gives teams an emergency fallback through freeze mode when generator defects block delivery.
- In-file augmentation increases ergonomics and discoverability but requires robust merge/idempotency safeguards.
- Sidecar tooling introduces additional engineering scope (incremental sync, conflict handling, and deterministic patching).
- Syntax-error skip behavior reduces churn/noise but requires clear user feedback so skipped generation is obvious.

### Out of scope
- Runtime code generation — this decision covers developer-invoked or build-time generation only; runtime reflection or dynamic class loading is not addressed.
- Bytecode manipulation — ASM, ByteBuddy, and similar tools are explicitly excluded; source-only is the invariant.
- IDE plugin development — sidecar tooling integrates with existing editor extension APIs; building a new IDE plugin is not required.
- Generator logic for specific entity types — what metadata, builders, or mappers are generated is addressed in DEC-004, DEC-005, and DEC-008; this decision covers tooling workflow only.
- VCS conflict resolution — freeze mode and checked-in outputs may produce merge conflicts; conflict resolution strategy is outside the scope of this decision.

### Acceptance criteria
- Primary generation MUST use source analysis and emit Java source, not bytecode.
- Generated outputs SHOULD be human-readable and suitable for source control.
- Freeze mode, syntax-error skip policy, and deterministic patching MUST be defined before acceptance.
