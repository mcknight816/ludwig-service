package com.bluntsoftware.ludwig.conduit.schema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PropertyType {
    HIDDEN("hidden"),
    STRING("string"),
    NUMBER("number"),
    BOOLEAN("boolean");

    private final String value;

    // Constructor
    PropertyType(String value) {
        this.value = value;
    }

    // Getter for the property
    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @JsonCreator
    public static PropertyType fromValue(String value) {
        for (PropertyType format : PropertyType.values()) {
            if (format.value.equals(value)) {
                return format;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
