package com.bluntsoftware.ludwig.conduit.utils.schema;

public interface EntitySchema {

    static JsonSchema getSchema() {
        return JsonSchema.builder().build();
    }
}
