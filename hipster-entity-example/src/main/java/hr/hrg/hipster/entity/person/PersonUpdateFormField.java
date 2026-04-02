package hr.hrg.hipster.entity.person;

import hr.hrg.hipster.entity.api.DefaultViewMeta;
import hr.hrg.hipster.entity.api.FieldDef;
import hr.hrg.hipster.entity.api.ViewMeta;
import hr.hrg.hipster.entity.core.ArrayBackedViewProxyFactory;
import hr.hrg.hipster.entity.core.EntityUpdateTrackingArray;

/**
 * Field definitions for the {@code PersonUpdateForm} write view.
 *
 * <p>Each constant name <strong>must</strong> match the accessor method name on
 * the form interface exactly — no mapping, same as Java records.
 * {@code enum.name()} is the field name, always. The ordinal is the positional index
 * into the backing array: {@code values[field.ordinal()]} holds the field value.</p>
 */
public enum PersonUpdateFormField implements FieldDef {    id(Long.class),
    firstName(String.class),
    lastName(String.class),
    email(String.class),
    phoneNumber(String.class);

    private final Class<?> javaType;

    PersonUpdateFormField(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public Class<?> javaType() {
        return javaType;
    }

    public static PersonUpdateFormField forName(String name) {
        if (name == null) {
            return null;
        }
        return switch (name) {
            case "id" -> id;
            case "firstName" -> firstName;
            case "lastName" -> lastName;
            case "email" -> email;
            case "phoneNumber" -> phoneNumber;
            default -> null;
        };
    }

    public static final ViewMeta<PersonUpdateForm, PersonUpdateFormField> META = new DefaultViewMeta<>(
            PersonUpdateForm.class,
            PersonUpdateFormField.class,
            PersonUpdateFormField::forName,
            values -> {
                EntityUpdateTrackingArray<Long, PersonUpdateForm, PersonUpdateFormField> updateArray =
                        EntityUpdateTrackingArray.create(PersonUpdateFormField.class, values);
                return ArrayBackedViewProxyFactory.createUpdatable(
                        PersonUpdateForm.class,
                        updateArray,
                        PersonUpdateFormField::forName);
            }
    );
}
