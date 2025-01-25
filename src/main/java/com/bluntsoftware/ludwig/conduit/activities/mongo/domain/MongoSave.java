package com.bluntsoftware.ludwig.conduit.activities.mongo.domain;

import com.bluntsoftware.ludwig.conduit.utils.schema.EntitySchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.PropertyFormat;
import com.bluntsoftware.ludwig.conduit.utils.schema.StringProperty;
import lombok.*;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MongoSave implements EntitySchema {
    MongoSettings settings;
    Map<String,Object> payload;
    public  JsonSchema getJsonSchema() {
        JsonSchema ret =  JsonSchema.builder().title("Mongo Save").build();
        ret.getProperties().put("payload", StringProperty.builder().defaultValue("{}").format(PropertyFormat.JSON).build());
        ret.getProperties().put("settings", MongoSettings.builder().build().getJsonSchema());
        return ret;
    }
}
