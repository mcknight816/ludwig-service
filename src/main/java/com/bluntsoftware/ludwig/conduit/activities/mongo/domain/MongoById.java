package com.bluntsoftware.ludwig.conduit.activities.mongo.domain;

import com.bluntsoftware.ludwig.conduit.utils.schema.EntitySchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MongoById implements EntitySchema {
    String id;
    MongoSettings settings;
    public  JsonSchema getSchema() {
        JsonSchema ret =  JsonSchema.builder().title("Mongo Get By Id").build();
        ret.getProperties().put("id", StringProperty.builder().title("Id").defaultValue("").build());
        ret.getProperties().put("settings", MongoSettings.builder().build().getSchema());
        return ret;
    }
}
