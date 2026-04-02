package hr.hrg.hipster.entity.api;

import java.util.Objects;
import java.util.function.Function;

/**
 * Simple concrete {@link ViewMeta} implementation that is parametrized through
 * constructor arguments. This avoids per-view boilerplate classes in most cases.
 *
 * @param <V> view interface type
 * @param <F> field enum type (must implement {@link FieldDef})
 */
public final class DefaultViewMeta<V, F extends Enum<F> & FieldDef> implements ViewMeta<V, F> {

    private final Class<V> viewType;
    private final Class<F> fieldType;
    private final F[] fieldValues;
    private final int fieldCount;
    private final Function<String, F> forName;
    private final Function<Object[], V> creator;

    public DefaultViewMeta(
            Class<V> viewType,
            Class<F> fieldType,
            Function<String, F> forName,
            Function<Object[], V> creator
    ) {
        this.viewType = Objects.requireNonNull(viewType, "viewType");
        this.fieldType = Objects.requireNonNull(fieldType, "fieldType");
        this.fieldValues = fieldType.getEnumConstants();
        this.fieldCount = this.fieldValues.length;
        this.forName = Objects.requireNonNull(forName, "forName");
        this.creator = Objects.requireNonNull(creator, "creator");
    }

    @Override
    public Class<V> viewType() {
        return viewType;
    }

    @Override
    public Class<F> fieldType() {
        return fieldType;
    }

    @Override
    public int fieldCount() {
        return fieldCount;
    }

    @Override
    public F[] fieldValues() {
        return fieldValues;
    }

    @Override
    public String fieldNameAt(int ordinal) {
        return fieldValues[ordinal].name();
    }

    @Override
    public Class<?> fieldTypeAt(int ordinal) {
        return fieldValues[ordinal].javaType();
    }

    @Override
    public F forName(String name) {
        return forName.apply(name);
    }

    @Override
    public V create(Object[] values) {
        return creator.apply(values);
    }
}
