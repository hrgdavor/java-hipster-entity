package hr.hrg.hipster.entity.core;

@SuppressWarnings("unchecked")
public final class EEnumSetBuilderLarge<E extends Enum<E>> implements EEnumSetBuilder<E> {

    private final Class<E> enumClass;
    private final E[] universe;
    private final long[] bits;
    private int size;

    public EEnumSetBuilderLarge(Class<E> enumClass) {
        this.enumClass = enumClass;
        this.universe = (E[]) enumClass.getEnumConstants();
        this.bits = new long[(universe.length / 64) + 1];
    }

    public EEnumSetBuilderLarge(Class<E> enumClass, long[] sourceBits, int size) {
        this.enumClass = enumClass;
        this.universe = (E[]) enumClass.getEnumConstants();
        this.bits = new long[(universe.length / 64) + 1];
        int limit = Math.min(this.bits.length, sourceBits.length);
        for (int i = 0; i < limit; i++) this.bits[i] = sourceBits[i];
        this.size = size;
    }

    @Override
    public long getBits0() {
        return bits[0];
    }

    @Override
    public long getBits(int index) {
        return index < bits.length ? bits[index] : 0L;
    }

    @Override
    public int getSegmentCount() {
        return bits.length;
    }

    @Override
    public boolean isEmpty() {
        for (long bit : bits) {
            if (bit != 0) return false;
        }
        return true;
    }

    @Override
    public boolean has(int ordinal) {
        if (ordinal < 0 || ordinal >= universe.length) return false;
        int index = ordinal / 64;
        return (bits[index] & (1L << (ordinal & 63))) != 0;
    }

    @Override
    public boolean has(E key) {
        return key != null && has(key.ordinal());
    }

    @Override
    public boolean hasAny(EEnumSetRead<E> other) {
        if (other instanceof EEnumSetBuilderLarge<?> bl) {
            long[] otherBits = bl.rawBits();
            for (int i = 0; i < bits.length; i++) {
                if ((bits[i] & otherBits[i]) != 0) return true;
            }
            return false;
        }
        if (other instanceof EEnumSetLarge<?> el) {
            long[] otherBits = el.bits;
            for (int i = 0; i < bits.length; i++) {
                if ((bits[i] & otherBits[i]) != 0) return true;
            }
            return false;
        }
        int otherSegments = other.getSegmentCount();
        if (otherSegments == bits.length) {
            for (int i = 0; i < bits.length; i++) {
                if ((bits[i] & other.getBits(i)) != 0) return true;
            }
            return false;
        }
        int maxSegments = Math.max(bits.length, otherSegments);
        for (int i = 0; i < maxSegments; i++) {
            long thisBits = i < bits.length ? bits[i] : 0L;
            if ((thisBits & other.getBits(i)) != 0) return true;
        }
        return false;
    }

    @Override
    public boolean hasAll(EEnumSetRead<E> other) {
        if (other instanceof EEnumSetBuilderLarge<?> bl) {
            long[] otherBits = bl.rawBits();
            for (int i = 0; i < bits.length; i++) {
                long ob = otherBits[i];
                if ((bits[i] & ob) != ob) return false;
            }
            return true;
        }
        if (other instanceof EEnumSetLarge<?> el) {
            long[] otherBits = el.bits;
            for (int i = 0; i < bits.length; i++) {
                long ob = otherBits[i];
                if ((bits[i] & ob) != ob) return false;
            }
            return true;
        }
        int otherSegments = other.getSegmentCount();
        if (otherSegments == bits.length) {
            for (int i = 0; i < bits.length; i++) {
                long ob = other.getBits(i);
                if ((bits[i] & ob) != ob) return false;
            }
            return true;
        }
        int maxSegments = Math.max(bits.length, otherSegments);
        for (int i = 0; i < maxSegments; i++) {
            long thisBits = i < bits.length ? bits[i] : 0L;
            long otherBits = other.getBits(i);
            if ((thisBits & otherBits) != otherBits) return false;
        }
        return true;
    }

    long[] rawBits() {
        return bits;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public E get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException("Index: " + index + ", size: " + size);
        int seen = 0;
        for (int segment = 0; segment < bits.length; segment++) {
            long remaining = bits[segment];
            while (remaining != 0) {
                int offset = Long.numberOfTrailingZeros(remaining);
                if (seen == index) return universe[(segment * 64) + offset];
                remaining &= (remaining - 1);
                seen++;
            }
        }
        throw new IllegalStateException("Internal bit count mismatch");
    }

    @Override
    public void forEach(ForEach<E> callback) {
        int idx = 0;
        for (int segment = 0; segment < bits.length; segment++) {
            long remaining = bits[segment];
            while (remaining != 0) {
                int offset = Long.numberOfTrailingZeros(remaining);
                callback.next(universe[(segment * 64) + offset], idx++);
                remaining &= (remaining - 1);
            }
        }
    }

    @Override
    public boolean mark(int ordinal) {
        if (ordinal < 0 || ordinal >= universe.length) return false;
        int segment = ordinal / 64;
        long mask = 1L << (ordinal & 63);
        if ((bits[segment] & mask) != 0) return false;
        bits[segment] |= mask;
        size++;
        return true;
    }

    @Override
    public boolean mark(E value) {
        return value != null && mark(value.ordinal());
    }

    @Override
    public boolean unmark(int ordinal) {
        if (ordinal < 0 || ordinal >= universe.length) return false;
        int segment = ordinal / 64;
        long mask = 1L << (ordinal & 63);
        if ((bits[segment] & mask) == 0) return false;
        bits[segment] &= ~mask;
        size--;
        return true;
    }

    @Override
    public boolean unmark(E value) {
        return value != null && unmark(value.ordinal());
    }

    @Override
    public void clear() {
        for (int i = 0; i < bits.length; i++) bits[i] = 0;
        size = 0;
    }

    @Override
    public EEnumSet<E> toImmutable() {
        if (size == 0) return new EEnumSetEmpty<>(enumClass);
        return new EEnumSetLarge<>(enumClass, bits, size);
    }
}
