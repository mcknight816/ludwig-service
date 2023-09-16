package com.bluntsoftware.ludwig.conduit.schema;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Alex Mcknight on 1/25/2017.
 *
 */
public class StringProperty implements Property{

    private String description;
    private int minLength;
    private String defaultValue;
    private String format;
    private String title;
    private String type = "string";
    private  Map<String,String> meta = new HashMap<>();

    public  Map<String, String> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, String> meta) {
        this.meta = meta;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }
    @JsonProperty("default")
    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    @Override
    public Object getValue() {
        return defaultValue;
    }
}
