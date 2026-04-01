package hr.hrg.hipster.entity.person;

import hr.hrg.hipster.entity.core.EEnumSet;

public interface PersonUpdatableView extends PersonUpdateForm {

    PersonUpdatableView firstName(String value);
    PersonUpdatableView lastName(String value);
    PersonUpdatableView email(String value);
    PersonUpdatableView phoneNumber(String value);

    Object get(PersonUpdateFormField field);
    boolean set(PersonUpdateFormField field, Object value);

    EEnumSet<PersonUpdateFormField> changes();
    void clearChanges();
}
