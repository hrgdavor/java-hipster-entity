package hr.hrg.hipster.entity.core;

public final class EEnumSetAll<E extends Enum<E>> implements EEnumSet<E> {
	private static final String FAKE = "This is a fake full set that can not be listed. You must override and implement version that has all elements.";
	private int segmentCount;

	public EEnumSetAll(int segmentCount) {
		this.segmentCount = segmentCount;
	}
	
	//= -1L; // -1 is all bits true (111111....11111)
	
	@Override
	public long getBits0() { return -1L; }

	@Override
	public long getBits(int index) { return -1L;	}

	@Override
	public boolean isEmpty() {	return false;	}

	@Override
	public boolean has(int ordinal) { return ordinal > -1;	}

	@Override
	public boolean has(E key) {	return true; }
	
	@Override
	public boolean hasAny(EEnumSetRead<E> other) {	return true; }

	@Override
	public boolean hasAll(EEnumSetRead<E> other) { return true; }

	@Override
	public int size() {	throw new UnsupportedOperationException(FAKE); }

	@Override
	public E get(int index) {throw new UnsupportedOperationException(FAKE);}
	
	@Override
	public String toString() {
		return "[*ALL*]";
	}

	@Override
	public void forEach(ForEach<E> callback) {
		throw new UnsupportedOperationException(FAKE);
	}

	@Override
	public EEnumSetBuilder<E> toBuilder() {
		throw new UnsupportedOperationException(FAKE);
	}
		
	@Override
	public int getSegmentCount() {
		return segmentCount;
	}
}
