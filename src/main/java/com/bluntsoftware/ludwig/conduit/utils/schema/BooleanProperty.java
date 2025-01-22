package com.bluntsoftware.ludwig.conduit.utils.schema;

import com.couchbase.client.core.deps.com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BooleanProperty implements Property {

    private String description;
    private String title;

    @Builder.Default
    private String type = "boolean";

    @Setter
    private Boolean defaultValue;

    @Singular("meta")
    private Map<String, String> meta = new HashMap<>();

    @JsonProperty("default")
    public Boolean getDefaultValue() {
        return defaultValue;
    }

    @Override
    public Object getValue() {
        return defaultValue;
    }
}
