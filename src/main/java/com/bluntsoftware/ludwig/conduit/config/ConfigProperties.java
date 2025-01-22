package com.bluntsoftware.ludwig.conduit.config;

import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigProperties {
    String name;
    String configClass;
    String category;
    JsonSchema schema;
}
