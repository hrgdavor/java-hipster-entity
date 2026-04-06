package hr.hrg.hipster.entityexample.person.entity;

import hr.hrg.hipster.entity.api.View;
import hr.hrg.hipster.entity.api.WriteOption;

@View(write=WriteOption.INTERFACE)
public interface PersonUpdateForm extends PersonCreateForm, Person {
}
