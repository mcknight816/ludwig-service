package com.bluntsoftware.ludwig.conduit.activities.ai.domain;


import com.bluntsoftware.ludwig.conduit.config.ai.domain.OpenAiConfig;
import com.bluntsoftware.ludwig.conduit.utils.schema.EntitySchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.PropertyFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AITextRequest implements EntitySchema {

    String text;
    String config;
    @Builder.Default
    String instructions = "Respond in a sarcastic tone";
    String knowledgeBase;
    String user;
    @Override
    public JsonSchema getJsonSchema() {
        JsonSchema ret =  JsonSchema.builder().title("Ai Text Request").build();
        ret.addString("text");
        ret.addString("user");
        ret.addString("instructions",this.instructions);
        ret.addConfigDomain("config",OpenAiConfig.class);
        ret.addString("knowledgeBase","", PropertyFormat.KNOWLEDGE_BASE_CHOOSER);
        return ret;
    }
}
