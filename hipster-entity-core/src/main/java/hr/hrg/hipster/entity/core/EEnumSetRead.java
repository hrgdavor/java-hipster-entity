package hr.hrg.hipster.entity.core;

public interface EEnumSetRead<E extends Enum<E>> {

    long getBits0();
    long getBits(int index);
    int getSegmentCount();

    boolean isEmpty();
    boolean has(int ordinal);

    boolean has(E key);
    boolean hasAny(EEnumSetRead<E> other);
    boolean hasAll(EEnumSetRead<E> other);

    int size();
    E get(int index);
    void forEach(ForEach<E> callback);
}
