package hr.hrg.hipster.entity.person;

import hr.hrg.hipster.entity.api.EntityUpdate;
import hr.hrg.hipster.entity.api.FieldKind;
import hr.hrg.hipster.entity.api.FieldSource;

import java.util.List;
import java.util.Map;

public interface PersonSummary extends PersonEntity {
    String firstName();
    String lastName();

    @FieldSource(kind = FieldKind.DERIVED, expression = "YEAR(NOW()) - YEAR(birthDate)")
    Integer age();

    @FieldSource(kind = FieldKind.JOINED, relation = "department.name")
    String departmentName();

    Map<String, List<Long>> metadata();

    public record Record(Long id, String firstName, String lastName, Integer age, String departmentName, Map<String, List<Long>> metadata) implements PersonSummary {}

    public class Mutable implements PersonSummary, EntityUpdate<Long, PersonSummary, PersonSummaryField> {
        Long id;
        String firstName;
        String lastName;
        Integer age;
        String departmentName;
        Map<String, List<Long>> metadata;

        public Long id(){return id;}
        public String firstName(){return firstName;}
        public String lastName(){return lastName;}
        public Integer age(){return age;}
        public String departmentName(){return departmentName;}
        public Map<String, List<Long>> metadata(){return metadata;}

        public Object get(PersonSummaryField field){
            return switch (field) {
                case id -> id;
                case firstName -> firstName;
                case lastName -> lastName;
                case age -> age;
                case departmentName -> departmentName;
                case metadata -> metadata;
            };
        }
        public Object get(int field){
            return switch (field) {
                case 0 -> id;
                case 1 -> firstName;
                case 2 -> lastName;
                case 3 -> age;
                case 4 -> departmentName;
                case 5 -> metadata;
                default -> null;
            };
        }
        public Object set(PersonSummaryField field, Object value){
            switch (field) {
                case id -> id = (Long) value;
                case firstName -> firstName = (String) value;
                case lastName -> lastName = (String) value;
                case age -> age = (Integer) value;
                case departmentName -> departmentName = (String) value;
                case metadata -> metadata = (Map<String, List<Long>>) value;
            }
            return this;
        }

        public Object set(int field, Object value){
            switch (field) {
                case 0 -> id = (Long) value;
                case 1 -> firstName = (String) value;
                case 2 -> lastName = (String) value;
                case 3 -> age = (Integer) value;
                case 4 -> departmentName = (String) value;
                case 5 -> metadata = (Map<String, List<Long>>) value;
            }
            return this;
        }
    }
}
