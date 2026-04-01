# EnumSet Implementation Path and JMH Evidence

This document is an implementation guide for the custom enum-bitset structures used for change tracking in proxy and builder workflows.

For the decision rationale, tradeoffs, and measured evidence, see [DEC-014](../brainstorm/dec-014-enumset-concrete-dispatch-strategy.md).

## Implementation scope

The implementation provides three layers of enum-set behavior for update tracking:

- `EEnumSet64` and `EEnumSetLarge`: immutable read snapshots
- `EEnumSetBuilder64` and `EEnumSetBuilderLarge`: mutable accumulators for mark/unmark/clear
- `EntityUpdateTrackingArray64` and `EntityUpdateTrackingArrayLarge`: concrete backing for tracking arrays in proxies

## Implementation approach

The design intentionally separates immutable and mutable responsibilities:

- immutable `EEnumSet*` classes expose read semantics and snapshot representation
- mutable `EEnumSetBuilder*` classes expose `mark`, `unmark`, and `clear`
- tracking arrays hold concrete builder variants to reduce polymorphic dispatch on hot paths

Two storage variants are used:

- 64-value enums: single `long` (`EEnumSetBuilder64`)
- larger enums: segmented `long[]` (`EEnumSetBuilderLarge`)

This keeps fast paths small and predictable while preserving support for larger enums.

## Why JMH is used

The optimization goal is practical runtime behavior, not theoretical micro-optimizations.
JMH is used to validate that the chosen path performs well under representative operations.

Benchmark focus:

- `mark` and `unmark` throughput
- `clear` throughput
- snapshot creation throughput
- concrete dispatch vs interface or abstract dispatch
- 64-field and 96-field cases

Relevant benchmark classes:

- `hipster-entity-core/src/test/java/hr/hrg/hipster/entity/core/EEnumSetJmhBenchmark.java`
- `hipster-entity-core/src/test/java/hr/hrg/hipster/entity/core/EEnumSetTrackingJmhBenchmark.java`

Runner:

- `scripts/run-jmh.js`

## Inlining-oriented benchmark profile

The default runner profile is tuned to allow JIT inlining stabilization:

- forks: `3`
- warmup iterations: `6`
- measurement iterations: `8`
- warmup time: `2s`
- measurement time: `2s`

Recommended command:

```bash
bun run scripts/run-jmh.js --include ".*EEnumSetTrackingJmhBenchmark.*"
```

Short smoke runs are still useful for quick checks, but they should not be treated as decision-grade evidence.

## Current evidence summary

Directional results from the tracking suite show:

- `markUnmark*` paths generally benefit from concrete dispatch
- `clear*` paths are mixed by variant and width (64 vs 96)
- snapshot paths are largely dominated by allocation and conversion cost

Interpretation:

- The custom EnumSet path is a good fit for update-tracking mutation hot paths.
- Dispatch specialization helps most where work per call is small and frequent.
- Snapshot cost should be treated as a separate optimization concern from dispatch.

## Decision guidance

Use this implementation path when:

- tracking updates frequently in proxy or builder flows
- enum width is known and benefits from specialized 64 or segmented code paths
- reproducible JMH runs confirm expected behavior in your target JVM profile

Reassess when:

- snapshot creation dominates total cost
- production workload patterns diverge from benchmark assumptions
- a simpler baseline provides equivalent end-to-end performance with lower maintenance cost