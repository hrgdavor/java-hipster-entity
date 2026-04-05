package hr.hrg.hipster.entityexample.person.entity;

import hr.hrg.hipster.entity.api.FieldKind;
import hr.hrg.hipster.entity.api.FieldSource;

import java.util.List;
import java.util.Map;

public interface PersonSummary extends Person {

    @FieldSource(kind = FieldKind.DERIVED, expression = "YEAR(NOW()) - YEAR(birthDate)")
    Integer age();

    @FieldSource(kind = FieldKind.JOINED, relation = "department.name")
    String departmentName();

    Map<String, List<Long>> metadata();

    // generated boilerplate, for better dev experience

    public record Record(
        Long id, 
        String firstName, 
        String lastName, 
        Integer age, 
        String departmentName, 
        Map<String, List<Long>> metadata
    ) implements PersonSummary {}
}
