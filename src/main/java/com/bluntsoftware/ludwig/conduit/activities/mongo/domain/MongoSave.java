package com.bluntsoftware.ludwig.conduit.activities.mongo.domain;

import com.bluntsoftware.ludwig.conduit.schema.EntitySchema;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.schema.StringProperty;
import lombok.*;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MongoSave implements EntitySchema {
    MongoSettings settings;
    Map<String,Object> payload;

    public static JsonSchema getSchema() {
        JsonSchema ret =  JsonSchema.builder().build();
        ret.getProperties().put("payload", StringProperty.builder().defaultValue("{}").format("json").build());
        ret.getProperties().put("settings", MongoSettings.getSchema());
        return ret;
    }
    public static void main(String[] args) {
        System.out.println(MongoSave.getSchema().getJson());
    }
}
