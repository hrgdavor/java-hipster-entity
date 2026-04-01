package hr.hrg.hipster.entity.core;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unchecked")
public final class EEnumSetLarge<E extends Enum<E>> implements EEnumSet<E>{

	protected long[] bits;
	protected int size;
	protected E[] universe;
    private Class<E> enumClass;

    protected EEnumSetLarge(Class<E> enumClass, E ...values){
        this.enumClass = enumClass;

		universe = (E[])enumClass.getEnumConstants();
		bits = new long[(universe.length / 64)+1];
		
		for(E en:values) {
			int ordinal = en.ordinal();
			if(!has(ordinal)) {
				universe[size] = en;
				size++;
				setTrue(ordinal);
			}
		}
	}


	protected EEnumSetLarge(Class<E> enumClass, List<E> values){
		this.enumClass = enumClass;

		universe = (E[])enumClass.getEnumConstants();
		bits = new long[(universe.length / 64)+1];

		for(E en:values) {
			int ordinal = en.ordinal();
			if(!has(ordinal)) {
				universe[size] = en;
				size++;
				setTrue(ordinal);
			}
		}
	}

	/**
	 * Internal fast-path constructor used when immutable segments are already prepared.
	 *
	 * <p>Intended for package-local builder conversions to avoid reconstructing from
	 * values. A defensive copy of {@code bits} is created so the immutable snapshot
	 * cannot be affected by later builder mutations. Callers must provide a
	 * {@code size} consistent with set bits.</p>
	 */
	EEnumSetLarge(Class<E> enumClass, long[] bits, int size){
		this.enumClass = enumClass;
		universe = (E[]) enumClass.getEnumConstants();
		this.bits = Arrays.copyOf(bits, (universe.length / 64) + 1);
		this.size = size;
	}
	
	private void setTrue(int ordinal) {
		this.bits[ordinal/64] |= (1L<< (ordinal & 63));		
	}
		
	void setAll(boolean value) {
		if (value) {
			for(int i=0; i<bits.length; i++) bits[i] = -1L; // -1 is all bits true (111111....11111)
		} else {
			for(int i=0; i<bits.length; i++) bits[i] = 0;
		}
	}

	@Override public long getBits0()  { return bits[0]; }
	@Override public long getBits(int index)  { return  index < bits.length ? bits[index] : 0L; }

	@Override
	public boolean isEmpty() {
		for(int i=0; i<bits.length; i++) {
			if(bits[i] != 0) return false;
		}
		
		return true;
	}
	
	@Override
	public boolean has(int ordinal){
		if(ordinal < 0) return false;
		int index = ordinal / 64;
		if(index >= bits.length) return false;
		return (bits[index]  & (1L << (ordinal & 63))) != 0;		
	}

	@Override
	public boolean has(E key) {
		return key != null && has(key.ordinal());
	}

	@Override
	public boolean hasAll(EEnumSetRead<E> other) {
		if(other instanceof EEnumSetLarge<?> el) {
			long[] otherBits = el.bits;
			for(int i=0; i<bits.length; i++) {
				long ob = otherBits[i];
				if((bits[i] & ob) != ob) return false;
			}
			return true;
		}
		if(other instanceof EEnumSetBuilderLarge<?> bl) {
			long[] otherBits = bl.rawBits();
			for(int i=0; i<bits.length; i++) {
				long ob = otherBits[i];
				if((bits[i] & ob) != ob) return false;
			}
			return true;
		}
		int otherSegments = other.getSegmentCount();
		if(otherSegments == bits.length) {
			for(int i=0; i<bits.length; i++) {
				long ob = other.getBits(i);
				if((bits[i] & ob) != ob) return false;
			}
			return true;
		}
		
		int maxSegments = Math.max(bits.length, otherSegments);
		for(int i=0; i<maxSegments; i++) {
			long thisBits = i < bits.length ? bits[i] : 0L;
			long otherBits = other.getBits(i);
			if((thisBits & otherBits) != otherBits) return false;
		}
		
		return true;		
	}

	@Override
	public boolean hasAny(EEnumSetRead<E> other) {
		if(other instanceof EEnumSetLarge<?> el) {
			long[] otherBits = el.bits;
			for(int i=0; i<bits.length; i++) {
				if((bits[i] & otherBits[i]) != 0) return true;
			}
			return false;
		}
		if(other instanceof EEnumSetBuilderLarge<?> bl) {
			long[] otherBits = bl.rawBits();
			for(int i=0; i<bits.length; i++) {
				if((bits[i] & otherBits[i]) != 0) return true;
			}
			return false;
		}
		int otherSegments = other.getSegmentCount();
		if(otherSegments == bits.length) {
			for(int i=0; i<bits.length; i++) {
				if((bits[i] & other.getBits(i)) != 0) return true;
			}
			return false;
		}

		int maxSegments = Math.max(bits.length, otherSegments);
		for(int i=0; i<maxSegments; i++) {
			long otherBits = other.getBits(i);
			long thisBits = i < bits.length ? bits[i] : 0L;
			if((thisBits & otherBits) != 0) return true;
		}
		
		return false;
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
		for(int i=0; i<size; i++) callback.next(universe[i],i);
	}

	@Override
	public EEnumSetBuilder<E> toBuilder() {
		return new EEnumSetBuilderLarge<>(enumClass, bits, size);
	}
	
	@Override
	public int getSegmentCount() {
		return bits.length;
	}
}
