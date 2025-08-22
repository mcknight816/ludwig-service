package com.bluntsoftware.ludwig.conduit.activities;

import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;

import java.util.List;
import java.util.Map;

public interface Activity {
    Map<String,Object> getInput();
    Map<String,Object> getOutput();
    String getName();
    String getActivityClass();
    String getCategory();
    String getIcon();
    Boolean fireAndForget();
    ActivityProperties getActivityProperties();
    JsonSchema getJsonSchema();
    List<String> getKeywords();
}
