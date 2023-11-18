package com.bluntsoftware.ludwig.conduit.domain;

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
        JsonSchema schema =  JsonSchema.builder().build();
        schema.getProperties().put("query", DBQuery.getSchema() );
        schema.getProperties().put("settings", MongoSettings.getSchema());
        return schema;
    }
}
