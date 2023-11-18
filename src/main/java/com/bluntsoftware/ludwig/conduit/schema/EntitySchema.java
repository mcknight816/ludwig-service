package com.bluntsoftware.ludwig.conduit.schema;

public interface EntitySchema {

    static JsonSchema getSchema() {
        return JsonSchema.builder().build();
    }
}
