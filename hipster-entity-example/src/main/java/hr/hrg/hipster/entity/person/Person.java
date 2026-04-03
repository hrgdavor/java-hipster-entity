package hr.hrg.hipster.entity.person;

import hr.hrg.hipster.entity.api.EntityBase;
import hr.hrg.hipster.entity.api.Identifiable;

public interface Person extends EntityBase<Long>, Identifiable<Long> {
    String firstName();
    String lastName();

    public record Record(Long id, String firstName, String lastName) implements Person {}
}
