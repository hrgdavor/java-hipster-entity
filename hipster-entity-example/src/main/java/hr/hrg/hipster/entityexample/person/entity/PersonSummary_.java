package hr.hrg.hipster.entityexample.person.entity;

import java.lang.reflect.Type;

import hr.hrg.hipster.entity.api.DefaultViewMeta;
import hr.hrg.hipster.entity.api.FieldDef;
import hr.hrg.hipster.entity.api.FieldNameMapper;
import hr.hrg.hipster.entity.api.TypeUtils;
import hr.hrg.hipster.entity.api.ViewMeta;
import hr.hrg.hipster.entity.core.ArrayBackedViewProxyFactory;
import hr.hrg.hipster.entity.core.EntityReadArray;

public enum PersonSummary_ implements FieldDef {

    id(java.lang.Long.class),
    firstName(java.lang.String.class),
    lastName(java.lang.String.class),
    age(java.lang.Integer.class),
    departmentName(java.lang.String.class),
    metadata(TypeUtils.parameterizedType(java.util.Map.class, java.lang.String.class, TypeUtils.parameterizedType(java.util.List.class, java.lang.Long.class)));

    private final Type propertyType;

    private PersonSummary_(Type propertyType) {
        this.propertyType = propertyType;
    }

    @Override
    public Type javaType() {
        return propertyType;
    }

    public static PersonSummary_ forName(String name) {
        if (name == null)
            return null;
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

    public static final ViewMeta<PersonSummary, PersonSummary_> META = new DefaultViewMeta<PersonSummary, PersonSummary_>(
            PersonSummary.class,
            PersonSummary_.class,
            PersonSummary_::forName,
            values -> {
                EntityReadArray<PersonSummary, PersonSummary_> readArray =
                        new EntityReadArray<>(PersonSummary_.class, values);

                return ArrayBackedViewProxyFactory.createRead(
                        PersonSummary.class,
                        readArray,
                        PersonSummary_::forName);
            }
    );

}
