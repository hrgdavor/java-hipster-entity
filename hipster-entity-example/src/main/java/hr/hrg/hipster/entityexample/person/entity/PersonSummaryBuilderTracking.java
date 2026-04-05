package hr.hrg.hipster.entityexample.person.entity;

import java.util.List;
import java.util.Map;

import hr.hrg.hipster.entity.api.ViewWriter;
import hr.hrg.hipster.entity.core.EEnumSetBuilder64;
import hr.hrg.hipster.entity.core.ViewChangeTracking;

// generated when @View(tracking=true) is used on the PersonSummary interface
public class PersonSummaryBuilderTracking implements PersonSummary, ViewWriter,ViewChangeTracking<PersonSummary_, EEnumSetBuilder64<PersonSummary_>> {
    // tracking variant
    final EEnumSetBuilder64<PersonSummary_> modifiedFields;

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

        
    // tracking variant
    public PersonSummaryBuilderTracking() {
        this.modifiedFields = new EEnumSetBuilder64<>(PersonSummary_.class);
    }

    // tracking variant
    PersonSummaryBuilderTracking(EEnumSetBuilder64<PersonSummary_> modifiedFields) {
        this.modifiedFields = modifiedFields;
    }

    // tracking variant
    public static class TrackingStrict extends PersonSummaryBuilderTracking {
        public TrackingStrict() {
            super(new EEnumSetBuilder64<>(PersonSummary_.class));
        }
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

    @Override
    public boolean isChanged() {return modifiedFields.size() > 0;}

    @Override
    public EEnumSetBuilder64<PersonSummary_> changes() {return modifiedFields;}

    PersonSummaryBuilderTracking id(Long value){ modifiedFields.addOrdinalChange(0, id, value); id = value; return this;}
    PersonSummaryBuilderTracking firstName(String value){ modifiedFields.addOrdinalChange(1, firstName, value); firstName = value; return this;}
    PersonSummaryBuilderTracking lastName(String value){ modifiedFields.addOrdinalChange(2, lastName, value); lastName = value; return this;}
    PersonSummaryBuilderTracking age(Integer value){ modifiedFields.addOrdinalChange(3, age, value); age = value; return this;}
    PersonSummaryBuilderTracking departmentName(String value){ modifiedFields.addOrdinalChange(4, departmentName, value); departmentName = value; return this;}
    PersonSummaryBuilderTracking metadata(Map<String, List<Long>> value){ modifiedFields.addOrdinalChange(5, metadata, value); metadata = value; return this;}        


    @Override
    public void set(int field, Object value) {
        switch (field) {
            case 0 -> { modifiedFields.addOrdinalChange(0, id, (Long)value); id = (Long) value;  }
            case 1 -> { modifiedFields.addOrdinalChange(1, firstName, (String)value); firstName = (String) value; }
            case 2 -> { modifiedFields.addOrdinalChange(2, lastName, (String)value); lastName = (String) value; }
            case 3 -> { modifiedFields.addOrdinalChange(3, age, (Integer)value); age = (Integer) value; }
            case 4 -> { modifiedFields.addOrdinalChange(4, departmentName, (String)value); departmentName = (String) value; }
            case 5 -> { modifiedFields.addOrdinalChange(5, metadata, (Map<String, List<Long>>)value); metadata = (Map<String, List<Long>>) value; }
        }
    }

    @Override
    public int set(String field, Object value) {
        switch (field) {
            case "id":             modifiedFields.addOrdinalChange(0, id, (Long)value); id = (Long) value; return 0;
            case "firstName":      modifiedFields.addOrdinalChange(1, firstName, (String)value); firstName = (String) value; return 1;
            case "lastName":       modifiedFields.addOrdinalChange(2, lastName, (String)value); lastName = (String) value; return 2;
            case "age":            modifiedFields.addOrdinalChange(3, age, (Integer)value); age = (Integer) value; return 3;
            case "departmentName": modifiedFields.addOrdinalChange(4, departmentName, (String)value); departmentName = (String) value; return 4;
            case "metadata":       modifiedFields.addOrdinalChange(5, metadata, (Map<String, List<Long>>)value); metadata = (Map<String, List<Long>>) value; return 5;
        }
        // unknown field, could throw or ignore, here we choose to ignore and return -1 to indicate no field was set
        return -1;
    }
}