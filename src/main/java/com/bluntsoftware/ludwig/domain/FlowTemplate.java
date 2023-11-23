package com.bluntsoftware.ludwig.domain;


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
public class FlowTemplate {
    String name;
    String type;
    JsonSchema schema;
    Map<String,Object> context;
}
