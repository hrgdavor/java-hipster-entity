package hr.hrg.hipster.entityexample.person.entity;

import hr.hrg.hipster.entity.api.View;
import hr.hrg.hipster.entity.api.BooleanOption;

@View(read = BooleanOption.FALSE, write = BooleanOption.TRUE)
public interface PersonUpdateForm extends PersonCreateForm, Person {
}
