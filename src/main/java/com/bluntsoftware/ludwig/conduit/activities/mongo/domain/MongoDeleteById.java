package com.bluntsoftware.ludwig.conduit.activities.mongo.domain;

import com.bluntsoftware.ludwig.conduit.schema.EntitySchema;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.schema.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MongoDeleteById implements EntitySchema {
    String id;
    MongoSettings settings;
    public static JsonSchema getSchema() {
        JsonSchema ret =  JsonSchema.builder().title("Mongo Delete By Id").build();
        ret.getProperties().put("id", StringProperty.builder().build());
        ret.getProperties().put("settings", MongoSettings.getSchema());
        return ret;
    }
}