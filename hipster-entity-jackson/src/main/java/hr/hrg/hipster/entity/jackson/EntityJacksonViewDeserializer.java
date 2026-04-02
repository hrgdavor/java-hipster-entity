package hr.hrg.hipster.entity.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import hr.hrg.hipster.entity.api.EntityBase;
import hr.hrg.hipster.entity.api.FieldDef;
import hr.hrg.hipster.entity.api.ViewMeta;

import java.io.IOException;

public final class EntityJacksonViewDeserializer {

    public EntityJacksonViewDeserializer() {
    }

    public <V extends EntityBase<?>, F extends Enum<F> & FieldDef> V deserialize(ViewMeta<V, F> meta,
                                                                               JsonParser p)
            throws IOException {
        Object[] values = new Object[meta.fieldCount()];

        JsonToken token = p.currentToken();
        if (token == null) {
            token = p.nextToken();
        }
        if (token != JsonToken.START_OBJECT) {
            throw new IllegalArgumentException("Expected JSON object");
        }

        while (p.nextToken() != JsonToken.END_OBJECT) {
            String name = p.currentName();
            p.nextToken();
            F field = meta.forName(name);
            if (field != null) {
                values[field.ordinal()] = p.readValueAs(field.javaType());
            } else {
                p.skipChildren();
            }
        }

        return meta.create(values);
    }
}
