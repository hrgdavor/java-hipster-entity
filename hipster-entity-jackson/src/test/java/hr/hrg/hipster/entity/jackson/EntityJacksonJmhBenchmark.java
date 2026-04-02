package hr.hrg.hipster.entity.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import hr.hrg.hipster.entity.api.EntityReader;
import hr.hrg.hipster.entity.person.PersonSummary;
import hr.hrg.hipster.entity.person.PersonSummaryField;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class EntityJacksonJmhBenchmark {

    @State(Scope.Thread)
    public static class BenchmarkState {
        ObjectMapper defaultMapper;
        ObjectMapper moduleMapper;
        PersonSummary view;
        EntityReader<?, PersonSummary, PersonSummaryField> viewReader;
        PersonDto baselineBean;
        String jsonFromView;
        String jsonFromBean;
        EntityJacksonViewSerializer<PersonSummary, PersonSummaryField> entityViewSerializer;
        PersonSummaryGeneratedSerializer personSummarySerializer;

        @Setup
        public void setup() throws IOException {
            defaultMapper = new ObjectMapper();
            moduleMapper = new ObjectMapper();
            EntityJacksonMapper.registerModule(moduleMapper, PersonSummaryField.META);

            Object[] values = new Object[PersonSummaryField.values().length];
            values[PersonSummaryField.id.ordinal()] = 42L;
            values[PersonSummaryField.firstName.ordinal()] = "Alice";
            values[PersonSummaryField.lastName.ordinal()] = "Smith";
            values[PersonSummaryField.age.ordinal()] = 34;
            values[PersonSummaryField.departmentName.ordinal()] = "Engineering";
            values[PersonSummaryField.metadata.ordinal()] = null;
            view = PersonSummaryField.META.create(values);

            baselineBean = new PersonDto(42L, "Alice", "Smith", 34, "Engineering", null);

            StringWriter w = new StringWriter();
            viewReader = (EntityReader<?, PersonSummary, PersonSummaryField>) (EntityReader) view;
            EntityJacksonMapper.toJson(PersonSummaryField.META, viewReader, w);
            jsonFromView = w.toString();

            jsonFromBean = defaultMapper.writeValueAsString(baselineBean);
            entityViewSerializer = new EntityJacksonViewSerializer<>(PersonSummaryField.META);
            personSummarySerializer = new PersonSummaryGeneratedSerializer();
        }
    }

    @Benchmark
    public String serializeViewThroughEntityJackson(BenchmarkState state) throws IOException {
        StringWriter w = new StringWriter();
        EntityJacksonMapper.toJson(PersonSummaryField.META, state.viewReader, w);
        return w.toString();
    }

    @Benchmark
    public String serializeViewThroughEntityJacksonViewSerializer(BenchmarkState state) throws IOException {
        StringWriter w = new StringWriter();
        JsonGenerator gen = state.defaultMapper.getFactory().createGenerator(w);
        state.entityViewSerializer.serialize(state.viewReader, gen);
        gen.close();
        return w.toString();
    }

    @Benchmark
    public String serializeViewThroughPersonSummaryGeneratedSerializer(BenchmarkState state) throws IOException {
        StringWriter w = new StringWriter();
        JsonGenerator gen = state.defaultMapper.getFactory().createGenerator(w);
        state.personSummarySerializer.serialize(state.viewReader, gen);
        gen.close();
        return w.toString();
    }

    @Benchmark
    public String serializePojoThroughDefaultJackson(BenchmarkState state) throws IOException {
        return state.defaultMapper.writeValueAsString(state.baselineBean);
    }

    @Benchmark
    public PersonSummary deserializeViewThroughEntityJackson(BenchmarkState state) throws IOException {
        JsonParser parser = state.moduleMapper.createParser(state.jsonFromView);
        return EntityJacksonMapper.fromJson(PersonSummaryField.META, parser);
    }

    @Benchmark
    public PersonDto deserializePojoThroughDefaultJackson(BenchmarkState state) throws IOException {
        return state.defaultMapper.readValue(state.jsonFromBean, PersonDto.class);
    }

    public static final class PersonSummaryGeneratedSerializer {
        public void serialize(EntityReader<?, PersonSummary, PersonSummaryField> src, JsonGenerator gen) throws IOException {
            gen.writeStartObject();

            gen.writeFieldName("id");
            Object id = src.get(PersonSummaryField.id.ordinal());
            if (id == null) {
                gen.writeNull();
            } else {
                gen.writeNumber((Long) id);
            }

            gen.writeFieldName("firstName");
            Object firstName = src.get(PersonSummaryField.firstName.ordinal());
            if (firstName == null) {
                gen.writeNull();
            } else {
                gen.writeString((String) firstName);
            }

            gen.writeFieldName("lastName");
            Object lastName = src.get(PersonSummaryField.lastName.ordinal());
            if (lastName == null) {
                gen.writeNull();
            } else {
                gen.writeString((String) lastName);
            }

            gen.writeFieldName("age");
            Object age = src.get(PersonSummaryField.age.ordinal());
            if (age == null) {
                gen.writeNull();
            } else {
                gen.writeNumber((Integer) age);
            }

            gen.writeFieldName("departmentName");
            Object departmentName = src.get(PersonSummaryField.departmentName.ordinal());
            if (departmentName == null) {
                gen.writeNull();
            } else {
                gen.writeString((String) departmentName);
            }

            gen.writeFieldName("metadata");
            Object metadata = src.get(PersonSummaryField.metadata.ordinal());
            if (metadata == null) {
                gen.writeNull();
            } else {
                gen.writeObject(metadata);
            }

            gen.writeEndObject();
        }
    }

    public static record PersonDto(
            Long id,
            String firstName,
            String lastName,
            Integer age,
            String departmentName,
            Object metadata
    ) {
    }
}

