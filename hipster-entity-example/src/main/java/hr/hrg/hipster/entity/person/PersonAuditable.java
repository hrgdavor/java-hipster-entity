package hr.hrg.hipster.entity.person;

import hr.hrg.hipster.entity.api.BooleanOption;
import hr.hrg.hipster.entity.api.View;
import hr.hrg.hipster.entity.example.Auditable;

@View(read = BooleanOption.TRUE, write = BooleanOption.FALSE)
public interface PersonAuditable extends PersonEntity, Auditable<Long> {
}
