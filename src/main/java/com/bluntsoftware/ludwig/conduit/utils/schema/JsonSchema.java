package com.bluntsoftware.ludwig.conduit.utils.schema;

import com.bluntsoftware.ludwig.conduit.config.ActivityConfig;
import com.bluntsoftware.ludwig.conduit.config.ActivityConfigImpl;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
/**
 * Created by Alex Mcknight on 1/25/2017.
 */
@Slf4j
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class JsonSchema implements Property {

    @Builder.Default
    private String type = "object";
    @Builder.Default
    private String title = "untitled";
    @Builder.Default
    private Boolean hidden = false;
    @Builder.Default
    public Map<String,Property> properties = new LinkedHashMap<>();

    public JsonSchema(String title) {
        this.title = title;
        properties = new LinkedHashMap<>();
    }

    public JsonSchema(String title, boolean hidden) {
        this.title = title;
        this.hidden = hidden;
        properties = new LinkedHashMap<>();
    }

    public StringProperty addString(String name){
        return addString(name,StringProperty.builder().defaultValue(null).build());
    }

    public StringProperty addString(String name, String defaultValue ){
        return addString(name,StringProperty.builder().defaultValue(defaultValue).build());
    }

    public StringProperty addString(String name, String defaultValue, PropertyFormat format) {
        return addString(name,StringProperty.builder()
                .defaultValue(defaultValue)
                .type(PropertyType.STRING.getValue())
                .format(format)
                .build());
    }

    public StringProperty addString(String name, String defaultValue, boolean hidden ) {
        return addString(name,StringProperty.builder()
                .defaultValue(defaultValue)
                .type(hidden ? PropertyType.HIDDEN.getValue() : PropertyType.STRING.getValue())
                .build());
    }

    public StringProperty addString(String title,String name, String defaultValue,PropertyFormat format,boolean hidden) {
        return addString(name,StringProperty.builder()
                .title(title)
                .defaultValue(defaultValue)
                .type(hidden ? PropertyType.HIDDEN.getValue() : PropertyType.STRING.getValue())
                .format(format)
                .build());
    }

    public StringProperty addString(String name, StringProperty property) {

        if(property.getTitle() == null){
            property.setTitle(getTitle(name));
        }

        if(property.getType() == null){
            property.setType(PropertyType.STRING.getValue());
        }

        properties.put(name,property);
        return property;
    }

    public BooleanProperty addBoolean(String name, Boolean defaultValue) {
        return addBoolean(name,BooleanProperty.builder()
                .defaultValue(defaultValue)
                .build());
    }

    public BooleanProperty addBoolean(String name, BooleanProperty property) {
        if(property.getTitle() == null){
            property.setTitle(getTitle(name));
        }

        if(property.getType() == null){
            property.setType(PropertyType.BOOLEAN.getValue());
        }

        properties.put(name,property);
        return property;
    }

    public NumberProperty addNumber(String name, double defaultValue) {
        return addNumber(name,NumberProperty.builder()
                .defaultValue(defaultValue)
                .build());
    }
    public NumberProperty addNumber(String name, NumberProperty property) {
        if(property.getTitle() == null){
            property.setTitle(getTitle(name));
        }

        if(property.getType() == null){
            property.setType(PropertyType.NUMBER.getValue());
        }

        properties.put(name,property);
        return property;
    }

    public StringProperty addConfigDomain(String name,Class<? extends EntitySchema> config){
        Map<String,String> meta = new HashMap<>();
        meta.put("configClass",config.getName());
        StringProperty stringProperty =  StringProperty.builder()
                .title(config.getName())
                .defaultValue(config.getName() + " Default")
                .format(PropertyFormat.CONFIG_CHOOSER)
                .type(PropertyType.STRING.getValue())
                .meta(meta)
                .build();
        properties.put(name,stringProperty);
        return stringProperty;
    }

    public StringProperty addConfig(String activityConfigClassName){
        ActivityConfig<?> config = ActivityConfigImpl.getByConfigClass(activityConfigClassName);
        return addConfig(config);
    }

    public StringProperty addConfig(ActivityConfig<?> config){
        Map<String,String> meta = new HashMap<>();
        meta.put("configClass",config.getConfigClass());
        return addString(config.getName(),StringProperty.builder()
                .title(config.getPropertyName())
                .defaultValue(config.getName() + " Default")
                .format(PropertyFormat.CONFIG_CHOOSER)
                .type(PropertyType.STRING.getValue())
                .meta(meta)
                .build());
    }

    @JsonIgnore
    public String getJson(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //  mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return "{}";
    }

    public String getTitle(String name) {
        String[] sections = name.split("_");
        String title = "";
        for(String section:sections){
           title +=  section.substring(0, 1).toUpperCase() + section.substring(1) + " ";
        }
        return title;
    }

    public EnumProperty addEnum(String title,String name, List<String> enumeration, String defaultValue){
        EnumProperty property = new EnumProperty();
        property.setDefaultValue(defaultValue);
        property.set(enumeration);
        if(title == null){
            property.setTitle(getTitle(name));
        }else{
            property.setTitle(title);
        }
        properties.put(name,property);
        return property;
    }

    public EnumProperty addEnum(String name, List<String> enumeration, String defaultValue){
         return addEnum(null,name,enumeration,defaultValue);
    }

    public JsonSchema addRecord(String name, JsonSchema recordProperty){
        properties.put(name,recordProperty);
        return recordProperty;
    }

    @JsonIgnore
    @Override
    public Map<String,Object> getValue() {
        Map<String,Object> ret = new HashMap<>();
        for(String key:properties.keySet()){
            ret.put(key,properties.get(key).getValue());
        }
        return ret;
    }
    public <T> T convertValue(Class<T> toValueType) throws IllegalArgumentException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.convertValue(getValue(),toValueType);
    }




    public interface StringPropertyFilter {
        Boolean hasProperty(StringProperty property);
    }
    @JsonIgnore
    StringPropertyFilter secretFilter = (property) -> (property.getFormat() != null
            && PropertyFormat.PASSWORD.equals(property.getFormat()));
    @JsonIgnore
    public Map<String,StringProperty> getStringPropertyPaths(){
        return getStringProperties(this,null,null);
    }
    @JsonIgnore
    public Map<String,StringProperty> getSecretStringProperties(){
        return getStringPropertyPaths(secretFilter);
    }
    @JsonIgnore
    public Map<String,StringProperty> getStringPropertyPaths(StringPropertyFilter filter){
        return getStringProperties(this,null,filter);
    }
    @JsonIgnore
    Map<String,StringProperty> getStringProperties(JsonSchema record, String path, StringPropertyFilter filter){
        Map<String,StringProperty> ret = new HashMap<>();
        if(path != null){
            path += ".";
        }else{
            path = "";
        }
        for(String key:record.getProperties().keySet()){
            Property property = record.getProperties().get(key);
            if(property instanceof StringProperty){
                StringProperty stringProperty = (StringProperty)property;
                if(filter == null){
                    ret.put(path + key ,stringProperty);
                }else if(filter.hasProperty(stringProperty)){
                    ret.put(path + key ,stringProperty);
                }
            }else if(property instanceof JsonSchema){
                Map<String,StringProperty> paths = getStringProperties((JsonSchema)property,path + key,filter);
                ret.putAll(paths);
            }
        }
        return ret;
    }

}
