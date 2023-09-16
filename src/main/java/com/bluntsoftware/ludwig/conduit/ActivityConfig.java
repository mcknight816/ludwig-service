package com.bluntsoftware.ludwig.conduit;


import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;

public interface ActivityConfig {
    String getName();
    String getConfigClass();
    String getCategory();
    JsonSchema getSchema();
    String getPropertyName();
}
