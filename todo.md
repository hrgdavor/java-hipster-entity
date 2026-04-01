# EEnumSet Hardening Task List

This document tracks concrete fixes for `EEnumSet64` and `EEnumSetLarge`.

Goal:
- resolve correctness bugs first
- align behavior between 64-bit and large variants
- keep changes reviewable and executable one by one

## Execution order

Apply tasks in numeric order. Tasks 1-6 are correctness-critical and should be completed before performance refactors.

## Numbered tasks

1. Fix off-by-one limit in `EEnumSet64`
- Issue: capacity guard rejects the 64th enum value.
- Location: `EEnumSet64` varargs constructor (`if(size >= 63)`).
- Proposed fix:
  - change guard to allow ordinals `0..63` (64 values total)
  - fail only when input enum has ordinal outside supported range
- Acceptance check:
  - adding enum with ordinal 63 succeeds
  - adding ordinal 64+ fails fast with clear error

2. Add ordinal upper-bound validation in `EEnumSet64.has(int)`
- Issue: ordinal >= 64 is shifted and aliased due to Java shift masking.
- Location: `EEnumSet64.has(int)`.
- Proposed fix:
  - return `false` when `ordinal < 0 || ordinal >= 64`
- Acceptance check:
  - `has(64)` and `has(128)` return `false`
  - no false positives from masked shifts

3. Fix `EEnumSet64.plus(EEnumSet...)` lost updates
- Issue: `out.plus(en)` result is ignored, so additions are dropped.
- Location: `EEnumSet64.plus(EEnumSet...)`.
- Proposed fix:
  - reassign return value (`out = out.plus(en)`), or mutate via internal helper
  - verify behavior when merging multiple sets
- Acceptance check:
  - union with non-empty input returns all expected members

4. Assign `enumClass` in list constructor of `EEnumSet64`
- Issue: list constructor does not assign `enumClass`, but `makeNew` depends on it.
- Location: `EEnumSet64(Class<E>, List<E>)`.
- Proposed fix:
  - set `this.enumClass = enumClass` in that constructor
- Acceptance check:
  - chained `plus/minus` calls from list-based instances do not fail

5. Assign `enumClass` in list constructor of `EEnumSetLarge`
- Issue: same missing assignment pattern as 64-bit variant.
- Location: `EEnumSetLarge(Class<E>, List<E>)`.
- Proposed fix:
  - set `this.enumClass = enumClass` in that constructor
- Acceptance check:
  - chained `plus/minus` calls from list-based instances remain stable

6. Align `has(E)` null handling between variants
- Issue: `EEnumSet64.has(E)` is null-safe, `EEnumSetLarge.has(E)` is not.
- Location: `EEnumSetLarge.has(E)`.
- Proposed fix:
  - mirror 64-bit null-safe behavior (`return key != null && has(key.ordinal())`)
- Acceptance check:
  - `has(null)` returns `false` in both variants

7. Fix selected-element iteration/storage semantics in `EEnumSet64`
- Issue: methods use `universe[0..size)` as selected members, but constructors do not maintain such projection.
- Impacted methods: `get`, `toString`, `plus(E)`, `minus(E)`, list-based combine methods.
- Proposed fix options (choose one and keep consistent):
  - Option A (recommended): treat bitmask as single source of truth and enumerate set bits when needed
  - Option B: maintain explicit selected-members array/list in insertion or ordinal order
- Acceptance check:
  - `get(i)` and `toString()` reflect actual set membership, not enum prefix

8. Unify callback index semantics in `forEach`
- Issue: index argument meaning differs by variant (ordinal vs positional index).
- Location: `EEnumSet64.forEach`, `EEnumSetLarge.forEach`.
- Proposed fix:
  - define contract: callback index is ordinal
  - update implementations to pass ordinal consistently
- Acceptance check:
  - callers relying on ordinal behave identically across variants

9. Add behavior parity tests for 64 and large implementations
- Issue: no protection against variant drift.
- Proposed fix:
  - create shared test cases executed against both implementations
  - include membership, union, minus, hasAll/hasAny, forEach, and null handling
- Acceptance check:
  - both implementations pass identical expected outcomes for same logical operations

10. Add edge-case tests for boundaries and regressions
- Scope:
  - empty set behavior
  - max ordinal (63) in 64-bit variant
  - out-of-range ordinals
  - duplicate add/remove idempotency
  - merge of many small sets
- Acceptance check:
  - all known bugs from tasks 1-8 are covered by tests

11. Optional optimization pass (after correctness lock)
- Goal: reduce allocations in `plus/minus` operations.
- Proposed fix candidates:
  - segment-wise operations for union/intersection paths
  - avoid temporary `List<E>` where not required
- Acceptance check:
  - benchmark shows measurable improvement without behavioral changes

12. [DONE] Static dispatch for `EntityUpdateTrackingArray` hot path
- Background: The original structural split (steps 1-7) is effectively done via the existing
  design. Specifically:
  - `setTrue` is now `private` in both `EEnumSet64` and `EEnumSetLarge` — package coupling gone.
  - No `plus`/`minus` set-algebra exists in `EEnumSet64` or `EEnumSetLarge` — immutable role is pure read.
  - `EEnumSetEmpty` and `EEnumSetAll` are both already `final`.
  - `EEnumSetBuilder` interface already provides `mark(int)`, `unmark(int)`, `mark(E)`, `unmark(E)`,
    `clear()`, and `toImmutable()` — this is the `EEnumSetMutable` role.
  - `EEnumSetBuilder64` and `EEnumSetBuilderLarge` are both `final` — devirtualization is possible
    when held by concrete type.
  - The abstract-base split was bypassed: immutable (`EEnumSet64`/`EEnumSetLarge`) and mutable
    (`EEnumSetBuilder64`/`EEnumSetBuilderLarge`) are separate final classes in separate hierarchies.
- Remaining gap: `EntityUpdateTrackingArray.changes` is typed as `EEnumSetBuilder<F>` (interface).
  All `mark`/`clear` calls on it are therefore virtual-dispatched.
- Implementation:
  - `EntityUpdateTrackingArray` is now an `abstract` base class with a static `create()` factory.
  - `final EntityUpdateTrackingArray64` holds `EEnumSetBuilder64<F> changes` (concrete field).
  - `final EntityUpdateTrackingArrayLarge` holds `EEnumSetBuilderLarge<F> changes` (concrete field).
  - All `mark(int)`, `unmark(int)`, `clear()` calls inside the concrete classes are statically
    dispatched directly to the concrete builder — no interface indirection.
  - `getChanges64()` / `getChangesLarge()` provide concrete-typed access for generated code that
    knows its variant, eliminating interface dispatch at the call site too.
  - `getChanges()` returns `EEnumSetBuilder<F>` (interface) for generic/polymorphic callers.
  - Public `changesSnapshot()` returns an immutable `EEnumSet<F>` snapshot after each flush cycle.
- Acceptance check:
  - all existing correctness tests pass unchanged
  - `mark`/`clear` calls in concrete subclasses are statically bound (verify with javap -p -c)

## Review checklist per task

For each task before merge:
- add/adjust tests first or in same change
- keep diff minimal and isolated to one task
- run full core module tests
- update this document status if desired
