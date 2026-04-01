package hr.hrg.hipster.entity.core;

import hr.hrg.hipster.entity.api.EntityBase;
import hr.hrg.hipster.entity.api.EntityReader;
import hr.hrg.hipster.entity.api.EntityUpdate;

import java.util.Objects;

/**
 * Base for update-tracking entity array views.
 * Use {@link #create} to obtain the correct concrete variant for the enum size.
 * Concrete subclasses hold their builder as a concrete type so the JVM can
 * statically dispatch (and inline) {@link #mark}/{@link #clear} on the hot path.
 */
public abstract class EntityUpdateTrackingArray<ID, T extends EntityBase<ID>, F extends Enum<F>> implements EntityUpdate<ID, T, F> {

    protected final Class<F> enumClass;
    protected final Object[] values;
    protected final ID id;

    @SuppressWarnings("unchecked")
    protected EntityUpdateTrackingArray(Class<F> enumClass, Object[] values) {
        if (enumClass.getEnumConstants().length != values.length) {
            throw new IllegalArgumentException("View field count and data provided lengths do not match");
        }
        this.id = (ID) values[0];
        this.enumClass = enumClass;
        this.values = values;
    }

    /** Factory: picks {@link EntityUpdateTrackingArray64} or {@link EntityUpdateTrackingArrayLarge}. */
    public static <ID, T extends EntityBase<ID>, F extends Enum<F>>
    EntityUpdateTrackingArray<ID, T, F> create(Class<F> enumClass, Object... values) {
        return enumClass.getEnumConstants().length <= 64
                ? new EntityUpdateTrackingArray64<>(enumClass, values)
                : new EntityUpdateTrackingArrayLarge<>(enumClass, values);
    }

    /** Mark field ordinal as changed. Returns {@code true} if the bit was newly set. */
    public abstract boolean mark(int ordinal);

    /** Unmark field ordinal. Returns {@code true} if the bit was cleared. */
    public abstract boolean unmark(int ordinal);

    /** Reset all change bits (call after flush). */
    public abstract void clear();

    /** Snapshot of currently marked fields as an immutable set. */
    public abstract EEnumSet<F> changesSnapshot();

    /** Access the underlying builder (interface-typed for generic callers). */
    public abstract EEnumSetBuilder<F> getChanges();

    @Override
    public ID id() {
        return id;
    }

    @Override
    public Object get(F field) {
        return values[field.ordinal()];
    }

    @Override
    public Object get(int fieldOrdinal) {
        return values[fieldOrdinal];
    }

    @Override
    public Object set(F field, Object value) {
        return set(field.ordinal(), value);
    }

    @Override
    public Object set(int fieldOrdinal, Object value) {
        if (fieldOrdinal < 0 || fieldOrdinal >= values.length) {
            throw new IndexOutOfBoundsException("Field ordinal out of bounds: " + fieldOrdinal);
        }
        if (fieldOrdinal == 0) {
            throw new UnsupportedOperationException("ID field is immutable in EntityUpdateTrackingArray");
        }

        Object previous = values[fieldOrdinal];
        if (Objects.equals(previous, value)) {
            return previous;
        }

        values[fieldOrdinal] = value;
        mark(fieldOrdinal);
        return previous;
    }
}
