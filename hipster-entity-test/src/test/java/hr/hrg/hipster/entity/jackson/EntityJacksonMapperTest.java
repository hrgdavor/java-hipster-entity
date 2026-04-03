package hr.hrg.hipster.entity.jackson;

import com.fasterxml.jackson.core.JsonParser;
import hr.hrg.hipster.entity.person.PersonSummary;
import hr.hrg.hipster.entity.person.PersonSummaryField;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntityJacksonMapperTest {

    private static PersonSummary personSummary(Long id, String firstName, String lastName,
                                               Integer age, String departmentName) {
        Object[] values = new Object[PersonSummaryField.values().length];
        values[PersonSummaryField.id.ordinal()]             = id;
        values[PersonSummaryField.firstName.ordinal()]      = firstName;
        values[PersonSummaryField.lastName.ordinal()]       = lastName;
        values[PersonSummaryField.age.ordinal()]            = age;
        values[PersonSummaryField.departmentName.ordinal()] = departmentName;
        values[PersonSummaryField.metadata.ordinal()]       = null;
        return PersonSummaryField.META.create(values);
    }

    @Test
    void serializeAndDeserializePersonSummaryView() throws Exception {
        PersonSummary source = personSummary(42L, "Alice", "Smith", 34, "Engineering");

        java.io.StringWriter writer = new java.io.StringWriter();
        EntityJacksonMapper.toJson(PersonSummaryField.META,
                (hr.hrg.hipster.entity.api.EntityReader<?, PersonSummary, ?>) source,
                writer);
        String json = writer.toString();

        try (JsonParser parser = new com.fasterxml.jackson.databind.ObjectMapper().createParser(json)) {
            PersonSummary deserialized = EntityJacksonMapper.fromJson(PersonSummaryField.META, parser);
            assertEquals(source.firstName(),      deserialized.firstName());
            assertEquals(source.lastName(),       deserialized.lastName());
            assertEquals(source.age(),            deserialized.age());
            assertEquals(source.departmentName(), deserialized.departmentName());
            assertEquals(source.metadata(),       deserialized.metadata());
        }
    }

    @Test
    void serializeAndDeserializeViewHelpers() throws Exception {
        PersonSummary source = personSummary(42L, "Alice", "Smith", 34, "Engineering");

        java.io.StringWriter writer = new java.io.StringWriter();
        EntityJacksonMapper.toJson(PersonSummaryField.META,
                (hr.hrg.hipster.entity.api.EntityReader<?, PersonSummary, ?>) source,
                writer);
        String json = writer.toString();

        try (JsonParser parser = new com.fasterxml.jackson.databind.ObjectMapper().createParser(json)) {
            PersonSummary deserialized = EntityJacksonMapper.fromJson(PersonSummaryField.META, parser);
            assertEquals(source.firstName(),      deserialized.firstName());
            assertEquals(source.lastName(),       deserialized.lastName());
            assertEquals(source.age(),            deserialized.age());
            assertEquals(source.departmentName(), deserialized.departmentName());
            assertEquals(source.metadata(),       deserialized.metadata());
        }
    }

    @Test
    void registerViewModuleIntoObjectMapper() throws Exception {
        PersonSummary source = personSummary(42L, "Alice", "Smith", 34, "Engineering");

        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        EntityJacksonMapper.registerModule(mapper, PersonSummaryField.META);

        String json = mapper.writeValueAsString(source);
        PersonSummary deserialized = mapper.readValue(json, PersonSummary.class);

        assertEquals(source.firstName(),      deserialized.firstName());
        assertEquals(source.lastName(),       deserialized.lastName());
        assertEquals(source.age(),            deserialized.age());
        assertEquals(source.departmentName(), deserialized.departmentName());
        assertEquals(source.metadata(),       deserialized.metadata());
    }

    @Test
    void deserializeThroughBoilerplateDeserializer() throws Exception {
        PersonSummary source = personSummary(42L, "Alice", "Smith", 34, "Engineering");

        java.io.StringWriter writer = new java.io.StringWriter();
        EntityJacksonMapper.toJson(PersonSummaryField.META,
                (hr.hrg.hipster.entity.api.EntityReader<?, PersonSummary, ?>) source,
                writer);
        String json = writer.toString();

        try (JsonParser parser = new com.fasterxml.jackson.databind.ObjectMapper().createParser(json)) {
            PersonSummary deserialized = new PersonSummaryBoilerplateDeserializer().deserialize(parser);
            assertEquals(source.firstName(),      deserialized.firstName());
            assertEquals(source.lastName(),       deserialized.lastName());
            assertEquals(source.age(),            deserialized.age());
            assertEquals(source.departmentName(), deserialized.departmentName());
            assertEquals(source.metadata(),       deserialized.metadata());
        }
    }
}
