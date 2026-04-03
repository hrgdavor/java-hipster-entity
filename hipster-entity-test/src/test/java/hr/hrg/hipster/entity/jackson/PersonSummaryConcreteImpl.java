package hr.hrg.hipster.entity.jackson;

import hr.hrg.hipster.entity.api.EntityReader;
import hr.hrg.hipster.entity.person.PersonSummary;
import hr.hrg.hipster.entity.person.PersonSummaryField;

import java.util.List;
import java.util.Map;

/**
 * Concrete array-backed implementation of {@link PersonSummary} — no JDK Dynamic Proxy,
 * no {@link java.lang.reflect.InvocationHandler} overhead.
 *
 * <p>Each accessor performs a single array read and an unchecked cast.
 * All method dispatches are directly JIT-compiled and inlineable, unlike proxy invocations
 * which always go through {@link java.lang.reflect.InvocationHandler#invoke}.</p>
 *
 * <p>Used in JMH benchmarks to measure deserialization throughput with and without
 * the proxy overhead, establishing the theoretical ceiling for array-backed views.
 * Compare against {@link PersonSummaryField#META} (proxy-backed) to quantify the
 * proxy call overhead.</p>
 */
@SuppressWarnings("unchecked")
final class PersonSummaryConcreteImpl
        implements PersonSummary, EntityReader<Long, PersonSummary, PersonSummaryField> {

    private final Object[] values;

    PersonSummaryConcreteImpl(Object[] values) {
        this.values = values;
    }

    @Override public Long id()               { return (Long)    values[PersonSummaryField.id.ordinal()]; }
    @Override public String firstName()      { return (String)  values[PersonSummaryField.firstName.ordinal()]; }
    @Override public String lastName()       { return (String)  values[PersonSummaryField.lastName.ordinal()]; }
    @Override public Integer age()           { return (Integer) values[PersonSummaryField.age.ordinal()]; }
    @Override public String departmentName() { return (String)  values[PersonSummaryField.departmentName.ordinal()]; }

    @Override
    public Map<String, List<Long>> metadata() {
        return (Map<String, List<Long>>) values[PersonSummaryField.metadata.ordinal()];
    }

    @Override public Object get(PersonSummaryField field) { return values[field.ordinal()]; }
    @Override public Object get(int fieldOrdinal)         { return values[fieldOrdinal]; }
}
