package hr.hrg.hipster.entityexample.person.entity;

import hr.hrg.hipster.entity.api.BooleanOption;
import hr.hrg.hipster.entity.api.View;
import hr.hrg.hipster.entityexample.example.Auditable;

@View(read = BooleanOption.TRUE, write = BooleanOption.FALSE)
public interface PersonAuditable extends Person, Auditable<Long> {
}
