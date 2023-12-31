package com.bluntsoftware.ludwig.conduit.activities.mongo.domain;

import com.bluntsoftware.ludwig.conduit.schema.EntitySchema;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MongoFind implements EntitySchema {

    DBQuery query;
    MongoSettings settings;
    public static JsonSchema getSchema() {
        JsonSchema schema =  JsonSchema.builder().title("Mongo Find").build();
        schema.getProperties().put("query", DBQuery.getSchema() );
        schema.getProperties().put("settings", MongoSettings.getSchema());
        return schema;
    }
}
