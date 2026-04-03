package hr.hrg.hipster.entity.api;

public interface EntityReader<ID, T extends EntityBase<ID>, F extends Enum<F>> extends EntityBase<ID>{
    Object get(F field);
    Object get(int fieldOrdinal);
}
