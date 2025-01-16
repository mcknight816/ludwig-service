package com.bluntsoftware.ludwig.conduit.schema;


import com.bluntsoftware.ludwig.conduit.config.ActivityConfig;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Alex Mcknight on 1/25/2017.
 *
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class JsonSchema implements Property {

    @Builder.Default
    private String type = "object";
    private String title = "untitled";
    @Builder.Default
    private Boolean hidden = false;
    @Builder.Default
    private Map<String, Property> properties = new LinkedHashMap<>();

    public JsonSchema(String title) {
        this.title = title;
        this.properties = new LinkedHashMap<>();
    }

    public JsonSchema(String title, boolean hidden) {
        this.title = title;
        this.hidden = hidden;
        this.properties = new LinkedHashMap<>();
    }

    // Add String property methods
    public StringProperty addString(String name, String defaultValue) {
        return addString(null, name, defaultValue, null, false);
    }

    public StringProperty addString(String name, String defaultValue, PropertyFormat format) {
        return addString(null, name, defaultValue, format, false);
    }

    public StringProperty addString(String name, String defaultValue, boolean hidden) {
        return addString(null, name, defaultValue, null, hidden);
    }

    public StringProperty addString(String title, String name, String defaultValue, PropertyFormat format, boolean hidden) {
        return addString(title, name, defaultValue, format, hidden, null);
    }

    public StringProperty addConfig(ActivityConfig config) {
        Map<String, String> meta = new HashMap<>();
        meta.put("configClass", config.getConfigClass());
        return addString(config.getName(), config.getPropertyName(), config.getName() + " Default", PropertyFormat.CONFIG_CHOOSER, false, meta);
    }

    public StringProperty addString(String title, String name, String defaultValue, PropertyFormat format, boolean hidden, Map<String, String> meta) {
        StringProperty property = new StringProperty();
        property.setDefaultValue(defaultValue);
        property.setTitle(title != null ? title : getTitle(name));
        property.setType(hidden ? "hidden" : "string");
        property.setFormat(format);
        property.setMeta(meta);

        properties.put(name, property);
        return property;
    }

    // Add Enum property methods
    public EnumProperty addEnum(String title, String name, List<String> enumeration, String defaultValue) {
        EnumProperty property = new EnumProperty();
        property.setDefaultValue(defaultValue);
        property.set(enumeration);
        property.setTitle(title != null ? title : getTitle(name));

        properties.put(name, property);
        return property;
    }

    public EnumProperty addEnum(String name, List<String> enumeration, String defaultValue) {
        return addEnum(null, name, enumeration, defaultValue);
    }

    // Add record
    public JsonSchema addRecord(String name, JsonSchema recordProperty) {
        properties.put(name, recordProperty);
        return recordProperty;
    }

    // JSON Utilities
    @JsonIgnore
    public String getJson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }

    public <T> T convertValue(Class<T> toValueType) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.convertValue(getValue(), toValueType);
    }

    // Get values
    @JsonIgnore
    @Override
    public Map<String, Object> getValue() {
        Map<String, Object> ret = new HashMap<>();
        properties.forEach((key, property) -> ret.put(key, property.getValue()));
        return ret;
    }

    // String property utilities
    @JsonIgnore
    public Map<String, StringProperty> getStringPropertyPaths() {
        return getStringProperties(this, null, null);
    }

    @JsonIgnore
    public Map<String, StringProperty> getSecretStringProperties() {
        return getStringPropertyPaths(property -> PropertyFormat.PASSWORD.equals(property.getFormat()));
    }

    @JsonIgnore
    public Map<String, StringProperty> getStringPropertyPaths(StringPropertyFilter filter) {
        return getStringProperties(this, null, filter);
    }

    @JsonIgnore
    private Map<String, StringProperty> getStringProperties(JsonSchema record, String path, StringPropertyFilter filter) {
        Map<String, StringProperty> ret = new HashMap<>();
        String fullPath = (path != null ? path + "." : "");

        record.getProperties().forEach((key, property) -> {
            String currentPath = fullPath + key;

            if (property instanceof StringProperty) {
                StringProperty stringProperty = (StringProperty) property;
                if (filter == null || filter.hasProperty(stringProperty)) {
                    ret.put(currentPath, stringProperty);
                }
            } else if (property instanceof JsonSchema) {
                JsonSchema subSchema = (JsonSchema) property;
                ret.putAll(getStringProperties(subSchema, currentPath, filter));
            }
        });

        return ret;
    }

    // Title generator
    public String getTitle(String name) {
        return Arrays.stream(name.split("_"))
                .map(section -> section.substring(0, 1).toUpperCase() + section.substring(1))
                .collect(Collectors.joining(" "));
    }

    // Filter interface
    public interface StringPropertyFilter {
        Boolean hasProperty(StringProperty property);
    }
}

