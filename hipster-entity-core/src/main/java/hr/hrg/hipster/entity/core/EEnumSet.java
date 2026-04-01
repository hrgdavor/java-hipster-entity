package hr.hrg.hipster.entity.core;

public interface EEnumSet<E extends Enum<E>> extends EEnumSetRead<E> {
	EEnumSetBuilder<E> toBuilder();
}
