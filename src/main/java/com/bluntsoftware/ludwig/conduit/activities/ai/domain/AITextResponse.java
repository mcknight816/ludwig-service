package com.bluntsoftware.ludwig.conduit.activities.ai.domain;

import com.bluntsoftware.ludwig.conduit.utils.schema.EntitySchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AITextResponse implements EntitySchema {
    @Builder.Default
    String text = "ai response";

    @Override
    public JsonSchema getJsonSchema() {
        JsonSchema ret =  JsonSchema.builder().title("Ai Text Response").build();
        ret.addString("text",this.text);
        return ret;
    }
}
