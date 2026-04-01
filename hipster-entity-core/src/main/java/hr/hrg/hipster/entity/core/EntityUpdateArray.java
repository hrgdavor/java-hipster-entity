package hr.hrg.hipster.entity.core;

import hr.hrg.hipster.entity.api.EntityBase;
import hr.hrg.hipster.entity.api.EntityReader;

public final class EntityUpdateArray<ID, T extends EntityBase<ID>, F extends Enum<F>> implements EntityReader<ID,T, F> {

    private final Class<F> enumClass;
    private final Object[] values;
    private final ID id;

    public EntityUpdateArray(Class<F> enumClass, Object ...values) {
        if(enumClass.getEnumConstants().length != values.length) {
            throw new IllegalArgumentException("View field count and data provided lengths do not match");
        }
        this.id = (ID)values[0];
        this.enumClass = enumClass;
        this.values = values;
    }

    @Override
    public ID id() {
        return id;
    }

    @Override
    public Object get(F field) {
        return values[field.ordinal()];
    }

    @Override
    public Object get(int fieldOrdinal) {
        return values[fieldOrdinal];
    }
}
