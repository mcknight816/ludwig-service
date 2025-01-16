package com.bluntsoftware.ludwig.conduit.schema;

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
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }


}
