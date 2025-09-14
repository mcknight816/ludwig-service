package com.bluntsoftware.ludwig.conduit.activities.python.domain;

import com.bluntsoftware.ludwig.conduit.utils.schema.EntitySchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PythonResponse implements EntitySchema {
    private boolean success;
    private int exitCode;
    private String text;      // stdout
    private String stderr;    // stderr
    private long durationMs;

    @Override
    public JsonSchema getJsonSchema() {
        JsonSchema ret =  JsonSchema.builder().title("Python Text Response").build();
        ret.addString("text",this.text);
        return ret;
    }
}

