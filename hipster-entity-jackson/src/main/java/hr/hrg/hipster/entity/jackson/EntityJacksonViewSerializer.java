package hr.hrg.hipster.entity.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import hr.hrg.hipster.entity.api.EntityBase;
import hr.hrg.hipster.entity.api.EntityReader;
import hr.hrg.hipster.entity.api.FieldDef;
import hr.hrg.hipster.entity.api.ViewMeta;

import java.io.IOException;

public final class EntityJacksonViewSerializer {

    public EntityJacksonViewSerializer() {
    }

    public <V extends EntityBase<?>, F extends Enum<F> & FieldDef> void serialize(ViewMeta<V, F> meta,
                                                                                 EntityReader<?, V, ?> entity,
                                                                                 JsonGenerator gen)
            throws IOException {
        gen.writeStartObject();

        F[] fields = meta.fieldValues();
        int fieldCount = fields.length;
        for (int i = 0; i < fieldCount; i++) {
            gen.writeObjectField(fields[i].name(), entity.get(i));
        }

        gen.writeEndObject();
    }

}
