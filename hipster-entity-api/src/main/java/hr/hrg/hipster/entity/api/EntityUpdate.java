package hr.hrg.hipster.entity.api;

public interface EntityUpdate<ID, E extends EntityBase<ID>, F extends Enum<F>> extends EntityReader<ID, E, F> {
    Object set(F field, Object value);
    Object set(int fieldOrdinal, Object value);
}
