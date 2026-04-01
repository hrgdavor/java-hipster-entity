package hr.hrg.hipster.entity.person;

import hr.hrg.hipster.entity.api.View;
import hr.hrg.hipster.entity.api.BooleanOption;

@View(read = BooleanOption.FALSE, write = BooleanOption.TRUE)
public interface PersonCreateForm {
    String firstName();
    String lastName();
    String email();
    String phoneNumber();
}
