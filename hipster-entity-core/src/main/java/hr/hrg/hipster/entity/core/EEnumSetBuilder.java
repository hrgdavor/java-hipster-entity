package hr.hrg.hipster.entity.core;

public interface EEnumSetBuilder<E extends Enum<E>> extends EEnumSetRead<E> {

    static <E extends Enum<E>> EEnumSetBuilder<E> create(Class<E> enumClass) {
        int size = enumClass.getEnumConstants().length;
        return size <= 64 ? new EEnumSetBuilder64<>(enumClass) : new EEnumSetBuilderLarge<>(enumClass);
    }

    boolean mark(int ordinal);
    boolean unmark(int ordinal);
    boolean mark(E value);
    boolean unmark(E value);

    default boolean set(int ordinal, boolean value) {
        return value ? mark(ordinal) : unmark(ordinal);
    }

    default boolean set(E key, boolean value) {
        return key != null && set(key.ordinal(), value);
    }

    void clear();

    EEnumSet<E> toImmutable();
}
