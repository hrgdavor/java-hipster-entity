package hr.hrg.hipster.entity.person;

import hr.hrg.hipster.entity.api.View;
import hr.hrg.hipster.entity.api.BooleanOption;

@View(read = BooleanOption.TRUE, write = BooleanOption.FALSE)
public interface PersonDto extends PersonSummary {
}
