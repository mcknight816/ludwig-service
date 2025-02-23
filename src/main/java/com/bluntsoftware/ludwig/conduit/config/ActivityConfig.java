package com.bluntsoftware.ludwig.conduit.config;

import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import java.util.Map;

public interface ActivityConfig<T> {
    String getName();
    String getConfigClass();
    String getCategory();
    JsonSchema getJsonSchema();
    String getPropertyName();
    T getConfig(Map<String,Object> config);
    T getDefaultConfig();
    ConfigTestResult test(Map<String,Object> config);
}
