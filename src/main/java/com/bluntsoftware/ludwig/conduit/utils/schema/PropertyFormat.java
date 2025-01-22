package com.bluntsoftware.ludwig.conduit.utils.schema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PropertyFormat {

    FOLDER_CHOOSER("folderChooser"),
    CONFIG_CHOOSER("configChooser"),
    IMAGE_CHOOSER("imageChooser"),
    ROLE_CHOOSER("roleChooser"),
    PASSWORD("password"),
    JAVASCRIPT("javascript"),
    JSON("json"),
    HTML("html"),
    FILE("file"),
    XML("xml"),
    SQL("sql");

    private final String value;

    // Constructor
    PropertyFormat(String value) {
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
    public static PropertyFormat fromValue(String value) {
        for (PropertyFormat format : PropertyFormat.values()) {
            if (format.value.equals(value)) {
                return format;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
