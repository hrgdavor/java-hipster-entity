package hr.hrg.hipster.entity.person;

import hr.hrg.hipster.entity.api.DefaultViewMeta;
import hr.hrg.hipster.entity.api.FieldDef;
import hr.hrg.hipster.entity.api.FieldNameMapper;
import hr.hrg.hipster.entity.api.ViewMeta;
import hr.hrg.hipster.entity.core.ArrayBackedViewProxyFactory;
import hr.hrg.hipster.entity.core.EntityReadArray;

import java.util.Map;

/**
 * Field definitions for the {@code PersonSummary} read view.
 *
 * <p>Each constant name <strong>must</strong> match the accessor method name on
 * {@link PersonSummary} exactly — no mapping, same as Java records.
 * {@code enum.name()} is the field name, always. The ordinal is the positional index
 * into the backing array: {@code values[field.ordinal()]} holds the field value.</p>
 */
public enum PersonSummaryField implements FieldDef {
    id(Long.class),
    firstName(String.class),
    lastName(String.class),
    age(Integer.class),
    departmentName(String.class),
    metadata(Map.class);

    private final Class<?> javaType;

    PersonSummaryField(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public Class<?> javaType() {
        return javaType;
    }

    public static PersonSummaryField forName(String name) {
        if (name == null) {
            return null;
        }
        return switch (name) {
            case "id" -> id;
            case "firstName" -> firstName;
            case "lastName" -> lastName;
            case "age" -> age;
            case "departmentName" -> departmentName;
            case "metadata" -> metadata;
            default -> null;
        };
    }

    private static final FieldNameMapper<PersonSummaryField> NAME_MAPPER = PersonSummaryField::forName;

    public static final ViewMeta<PersonSummary, PersonSummaryField> META = new DefaultViewMeta<>(
        PersonSummary.class,
        PersonSummaryField.class,
        NAME_MAPPER,
        values -> {
            EntityReadArray<Long, PersonSummary, PersonSummaryField> readArray =
                    new EntityReadArray<>(PersonSummaryField.class, values);
            return ArrayBackedViewProxyFactory.createRead(
                    PersonSummary.class,
                    readArray,
                    NAME_MAPPER);
        }
    );
}
