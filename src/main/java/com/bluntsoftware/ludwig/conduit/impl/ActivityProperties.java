package com.bluntsoftware.ludwig.conduit.impl;

import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityProperties {
    String icon;
    String category;
    String name;
    String activityClass;
    boolean fireAndForget;
    JsonSchema schema;
    Map<String,Object> input;
    Map<String,Object> output;
}
