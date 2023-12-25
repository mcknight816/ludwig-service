package com.bluntsoftware.ludwig.conduit.activities.input.domain;

import com.bluntsoftware.ludwig.conduit.config.model.PayloadSchemaConfig;
import com.bluntsoftware.ludwig.conduit.schema.EntitySchema;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.schema.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostInput implements EntitySchema {
    Map<String,Object> payload;
    InputSettings settings;
    String payloadSchema;
    public static JsonSchema getSchema() {
        JsonSchema ret =  JsonSchema.builder().title("Post").build();
        ret.getProperties().put("payload", JsonSchema.builder().hidden(true).build());
        ret.getProperties().put("settings", InputSettings.getSchema());
        ret.getProperties().put("payloadSchema",  StringProperty.builder().title("Payload Schema")
                .meta("configClass", PayloadSchemaConfig.class.getTypeName())
                .format("configChooser").build());
        return ret;
    }
}
