package hr.hrg.hipster.entityexample.person.entity;

import hr.hrg.hipster.entity.api.View;
import hr.hrg.hipster.entity.api.BooleanOption;

@View(read = BooleanOption.TRUE, write = BooleanOption.FALSE)
public interface PersonDto extends PersonSummary {
}
