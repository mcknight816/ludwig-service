package com.bluntsoftware.ludwig.conduit.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex Mcknight on 1/25/2017.
 *
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class StringProperty implements Property {

    private String description;
    private int minLength;
    @Setter
    private String defaultValue;
    private PropertyFormat format;
    private String title;
    @Builder.Default
    private String type = "string";

    @Singular("meta")
    private  Map<String,String> meta = new HashMap<>();

    @JsonProperty("default")
    public String getDefaultValue() {
        return defaultValue;
    }


    @Override
    public Object getValue() {
        return defaultValue;
    }
}
