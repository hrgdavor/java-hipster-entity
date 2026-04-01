package hr.hrg.hipster.entity.core;

import java.io.StringWriter;
import java.util.List;

@SuppressWarnings("unchecked")
public final class EEnumSet64<E extends Enum<E>> implements EEnumSet<E>{

	protected long bits0;
	protected int size;
	protected E[] universe;
    private Class<E> enumClass;

    protected EEnumSet64(Class<E> enumClass, E ...values){
		universe = (E[])enumClass.getEnumConstants();
        this.enumClass = enumClass;
		ensureSupportedUniverseSize();
        for(E en:values) {
			int ordinal = en.ordinal();
			if(!has(ordinal)) {
				size++;
				setTrue(ordinal);
			}
		}
	}

	/**
	 * Internal fast-path constructor used when all immutable state is already known.
	 *
	 * <p>Intended for package-local builder conversions to avoid rebuilding bits from
	 * enumerated values. Callers must provide a consistent pair where {@code size}
	 * matches the number of set bits in {@code bits0}.</p>
	 */
	EEnumSet64(Class<E> enumClass, long bits0, int size){
		this.enumClass = enumClass;
		universe = (E[]) enumClass.getEnumConstants();
		ensureSupportedUniverseSize();
		this.bits0 = bits0;
		this.size = size;
	}
	
	protected EEnumSet64(Class<E> enumClass,List<E> values){
		this.enumClass = enumClass;
		universe = (E[])enumClass.getEnumConstants();
		ensureSupportedUniverseSize();
		for(E en:values) {
			int ordinal = en.ordinal();
			if(!has(ordinal)) {
				size++;
				setTrue(ordinal);
			}
		}
	}

	private void ensureSupportedUniverseSize() {
		if (universe.length > 64) {
			throw new IllegalArgumentException("EEnumSet64 requires enum with at most 64 values, got " + universe.length);
		}
	}
	
	private void setTrue(int ordinal) {
		this.bits0 |= (1L<<ordinal);		
	}
		
	void setAll(boolean value) {
		if (value) {
			bits0 = -1L;// -1 is all bits true (111111....11111)
		} else {
			bits0 = 0;
		}
	}

	@Override public long getBits0()  { return bits0; }
	@Override public long getBits(int index)  { return  index == 0 ? bits0 : 0L; }

	@Override
	public boolean isEmpty() {
		return bits0 == 0;
	}
	
	@Override
	public boolean has(int ordinal){
		if(ordinal < 0 || ordinal >= 64) return false;
		return (bits0 & (1L << ordinal)) != 0;		
	}
	
	@Override
	public boolean has(E key) {
		return key == null ? false: has(key.ordinal());
	}
		
	@Override
	public boolean hasAll(EEnumSetRead<E> other) {
		if(other instanceof EEnumSet64<?> e64) {
			long ob = e64.bits0;
			return (bits0 & ob) == ob;
		}
		if(other instanceof EEnumSetBuilder64<?> b64) {
			long ob = b64.rawBits0();
			return (bits0 & ob) == ob;
		}
		long otherBits0 = other.getBits0();
		return (bits0 & otherBits0) == otherBits0;
	}

	@Override
	public boolean hasAny(EEnumSetRead<E> other) {
		if(other instanceof EEnumSet64<?> e64) return (bits0 & e64.bits0) != 0;
		if(other instanceof EEnumSetBuilder64<?> b64) return (bits0 & b64.rawBits0()) != 0;
		return (bits0 & other.getBits0()) != 0;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public E get(int index) {
		return universe[index];
	}

	@Override
	public String toString() {
		StringWriter sw = new StringWriter();
		sw.append('[');
		for(int i=0; i<size; i++) {
			if(i>0) sw.append(',');
			sw.append(universe[i].toString());
		}
		sw.append(']');
		return sw.toString();
	}
	
	@Override
	public void forEach(ForEach<E> callback) {
		int idx = 0;
		for(int i=0; i<universe.length; i++){
			if(has(i)) callback.next(universe[i],idx++);
		}
	}

	@Override
	public EEnumSetBuilder<E> toBuilder() {
		return new EEnumSetBuilder64<>(enumClass, bits0, size);
	}
	
	@Override
	public int getSegmentCount() {
		return 1;
	}
}
