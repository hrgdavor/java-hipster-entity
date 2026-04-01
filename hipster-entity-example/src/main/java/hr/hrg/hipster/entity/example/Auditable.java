package hr.hrg.hipster.entity.example;

import hr.hrg.hipster.entity.api.EntityBase;

import java.time.Instant;

public interface Auditable<ID> extends EntityBase<ID> {
    Instant createdAt();
    Instant updatedAt();
}
