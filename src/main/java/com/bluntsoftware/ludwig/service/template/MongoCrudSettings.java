package com.bluntsoftware.ludwig.service.template;

import com.bluntsoftware.ludwig.conduit.activities.input.domain.PostInput;
import com.bluntsoftware.ludwig.conduit.activities.mongo.domain.MongoSettings;
import com.bluntsoftware.ludwig.conduit.schema.EntitySchema;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class MongoCrudSettings implements EntitySchema {
    MongoSettings settings;
    String payloadSchema;
    static JsonSchema getSchema() {
        JsonSchema ret =  JsonSchema.builder().title("MongoCrudFlow").build();
        ret.getProperties().put("settings", MongoSettings.getSchema());
        ret.getProperties().put("payloadSchema", PostInput.getSchema().properties.get("payloadSchema"));
        return ret;
    }
}
