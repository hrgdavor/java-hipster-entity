package hr.hrg.hipster.entity.core;

public final class EEnumSetEmpty<E extends Enum<E>> implements EEnumSet<E> {

    private final Class<E> enumClass;

    public EEnumSetEmpty(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

	@Override
	public long getBits0() { return 0; }

	@Override
	public long getBits(int index) { return 0;	}

	@Override
	public boolean isEmpty() {	return true;	}

	@Override
	public boolean has(int ordinal) { return false;	}

	@Override
	public boolean has(E key) { return false; }

	@Override
	public boolean hasAny(EEnumSetRead<E> other) {	return false; }

	@Override
	public boolean hasAll(EEnumSetRead<E> other) { return false; }

	@Override
	public int size() {	return 0; }

	@Override
	public E get(int index) {return null;}
	
	@Override
	public String toString() {
		return "[]";
	}

	@Override
	public void forEach(ForEach<E> callback) {
		// do nothing, this set is empty
	}

	@Override
	public EEnumSetBuilder<E> toBuilder() {
		return EEnumSetBuilder.create(enumClass);
	}

	@Override
	public int getSegmentCount() {
		return 1;
	}
}
