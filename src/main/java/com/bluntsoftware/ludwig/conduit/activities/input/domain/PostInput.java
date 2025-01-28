package com.bluntsoftware.ludwig.conduit.activities.input.domain;

import com.bluntsoftware.ludwig.conduit.config.model.PayloadSchemaConfig;
import com.bluntsoftware.ludwig.conduit.config.model.domain.PayloadSchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.EntitySchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.PropertyFormat;
import com.bluntsoftware.ludwig.conduit.utils.schema.StringProperty;
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
    public JsonSchema getJsonSchema() {
        JsonSchema ret =  JsonSchema.builder().title("Post").build();
        ret.getProperties().put("payload", JsonSchema.builder().hidden(true).build());
        ret.getProperties().put("settings", InputSettings.builder().build().getJsonSchema());
        ret.getProperties().put("payloadSchema",  StringProperty.builder().title("Payload Schema")
                .meta("configClass", PayloadSchema.class.getTypeName())
                .format(PropertyFormat.CONFIG_CHOOSER).build());
        return ret;
    }
}
