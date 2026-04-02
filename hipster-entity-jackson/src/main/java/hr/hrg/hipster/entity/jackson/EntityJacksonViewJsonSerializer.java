package hr.hrg.hipster.entity.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import hr.hrg.hipster.entity.api.EntityBase;
import hr.hrg.hipster.entity.api.EntityReader;
import hr.hrg.hipster.entity.api.FieldDef;
import hr.hrg.hipster.entity.api.ViewMeta;

import java.io.IOException;

public final class EntityJacksonViewJsonSerializer<V extends EntityBase<?>, F extends Enum<F> & FieldDef>
        extends JsonSerializer<V> {

    private final ViewMeta<V, F> meta;

    private final EntityJacksonViewSerializer<V, F> serializer;

    public EntityJacksonViewJsonSerializer(ViewMeta<V, F> meta) {
        this.meta = meta;
        this.serializer = new EntityJacksonViewSerializer<>(meta);
    }

    @Override
    public void serialize(V value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (!(value instanceof EntityReader<?, ?, ?> reader)) {
            throw new IllegalStateException("Value is not EntityReader: " + value.getClass());
        }
        @SuppressWarnings("unchecked")
        EntityReader<?, V, ?> typedReader = (EntityReader<?, V, ?>) reader;
        serializer.serialize(typedReader, gen);
    }
}
