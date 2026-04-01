package hr.hrg.hipster.entity.core;

@SuppressWarnings("unchecked")
public final class EEnumSetBuilder64<E extends Enum<E>> implements EEnumSetBuilder<E> {

    private final Class<E> enumClass;
    private final E[] universe;
    private long bits0;
    private int size;

    public EEnumSetBuilder64(Class<E> enumClass) {
        this.enumClass = enumClass;
        this.universe = (E[]) enumClass.getEnumConstants();
        if (universe.length > 64) {
            throw new IllegalArgumentException("EEnumSetBuilder64 requires enum with at most 64 values, got " + universe.length);
        }
    }

    public EEnumSetBuilder64(Class<E> enumClass, long bits0, int size) {
        this.enumClass = enumClass;
        this.universe = (E[]) enumClass.getEnumConstants();
        if (universe.length > 64) {
            throw new IllegalArgumentException("EEnumSetBuilder64 requires enum with at most 64 values, got " + universe.length);
        }
        this.bits0 = bits0;
        this.size = size;
    }

    @Override
    public long getBits0() {
        return bits0;
    }

    @Override
    public long getBits(int index) {
        return index == 0 ? bits0 : 0L;
    }

    @Override
    public int getSegmentCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return bits0 == 0;
    }

    @Override
    public boolean has(int ordinal) {
        if (ordinal < 0 || ordinal >= 64) return false;
        return (bits0 & (1L << ordinal)) != 0;
    }

    @Override
    public boolean has(E key) {
        return key != null && has(key.ordinal());
    }

    @Override
    public boolean hasAny(EEnumSetRead<E> other) {
        if (other instanceof EEnumSetBuilder64<?> b64) return (bits0 & b64.rawBits0()) != 0;
        if (other instanceof EEnumSet64<?> e64) return (bits0 & e64.bits0) != 0;
        return (bits0 & other.getBits0()) != 0;
    }

    @Override
    public boolean hasAll(EEnumSetRead<E> other) {
        if (other instanceof EEnumSetBuilder64<?> b64) {
            long ob = b64.rawBits0();
            return (bits0 & ob) == ob;
        }
        if (other instanceof EEnumSet64<?> e64) {
            long ob = e64.bits0;
            return (bits0 & ob) == ob;
        }
        long ob = other.getBits0();
        return (bits0 & ob) == ob;
    }

    long rawBits0() {
        return bits0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public E get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException("Index: " + index + ", size: " + size);
        long remaining = bits0;
        int seen = 0;
        while (remaining != 0) {
            int ordinal = Long.numberOfTrailingZeros(remaining);
            if (seen == index) return universe[ordinal];
            remaining &= (remaining - 1);
            seen++;
        }
        throw new IllegalStateException("Internal bit count mismatch");
    }

    @Override
    public void forEach(ForEach<E> callback) {
        long remaining = bits0;
        int idx = 0;
        while (remaining != 0) {
            int ordinal = Long.numberOfTrailingZeros(remaining);
            callback.next(universe[ordinal], idx++);
            remaining &= (remaining - 1);
        }
    }

    @Override
    public boolean mark(int ordinal) {
        if (ordinal < 0 || ordinal >= 64 || ordinal >= universe.length) return false;
        long mask = 1L << ordinal;
        if ((bits0 & mask) != 0) return false;
        bits0 |= mask;
        size++;
        return true;
    }

    @Override
    public boolean mark(E value) {
        return value != null && mark(value.ordinal());
    }

    @Override
    public boolean unmark(int ordinal) {
        if (ordinal < 0 || ordinal >= 64 || ordinal >= universe.length) return false;
        long mask = 1L << ordinal;
        if ((bits0 & mask) == 0) return false;
        bits0 &= ~mask;
        size--;
        return true;
    }

    @Override
    public boolean unmark(E value) {
        return value != null && unmark(value.ordinal());
    }

    @Override
    public void clear() {
        bits0 = 0;
        size = 0;
    }

    @Override
    public EEnumSet<E> toImmutable() {
        if (size == 0) return new EEnumSetEmpty<>(enumClass);
        return new EEnumSet64<>(enumClass, bits0, size);
    }
}
