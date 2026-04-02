package hr.hrg.hipster.entity.core;

import hr.hrg.hipster.entity.api.EntityBase;

/**
 * Update-tracking array view for enums with ≤ 64 values.
 * The {@code changes} field is typed as the concrete {@link EEnumSetBuilder64} so
 * all {@link #mark}/{@link #clear} calls are statically dispatched by the JVM.
 */
public final class EntityUpdateTrackingArray64<ID, T extends EntityBase<ID>, F extends Enum<F>>
        extends EntityUpdateTrackingArray<ID, T, F> {

    private final EEnumSetBuilder64<F> changes;

    EntityUpdateTrackingArray64(Class<F> enumClass, Object[] values) {
        super(enumClass, values);
        this.changes = new EEnumSetBuilder64<>(enumClass);
    }

    @Override
    public boolean mark(int ordinal) {
        return changes.addOrdinal(ordinal);
    }

    @Override
    public boolean unmark(int ordinal) {
        return changes.removeOrdinal(ordinal);
    }

    @Override
    public void clear() {
        changes.clear();
    }

    @Override
    public EEnumSet<F> changesSnapshot() {
        return changes.toImmutable();
    }

    @Override
    public EEnumSetBuilder<F> getChanges() {
        return changes;
    }

    /** Direct access to the concrete builder — no interface overhead. */
    public EEnumSetBuilder64<F> getChanges64() {
        return changes;
    }
}
