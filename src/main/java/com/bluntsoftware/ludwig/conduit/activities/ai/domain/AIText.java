package com.bluntsoftware.ludwig.conduit.activities.ai.domain;


import com.bluntsoftware.ludwig.conduit.config.ai.domain.OpenAiConfig;
import com.bluntsoftware.ludwig.conduit.utils.schema.EntitySchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AIText implements EntitySchema {
    String text;
    String config;

    @Override
    public JsonSchema getJsonSchema() {
        JsonSchema ret =  JsonSchema.builder().title("Ai Text").build();
        ret.addString("text");
        ret.addConfigDomain("config",OpenAiConfig.class);
        return ret;
    }
}
