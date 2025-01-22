package com.bluntsoftware.ludwig.conduit.utils.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class NumberProperty implements Property {

    private String description;
    private String title;

    @Builder.Default
    private String type = "number";

    @Builder.Default
    private Double minimum = Double.MIN_VALUE;
    @Builder.Default
    private Double maximum = Double.MAX_VALUE;

    @Setter
    private Double defaultValue;

    @Singular("meta")
    private Map<String, String> meta = new HashMap<>();

    @JsonProperty("default")
    public Double getDefaultValue() {
        return defaultValue;
    }

    @Override
    public Object getValue() {
        return defaultValue;
    }
}

