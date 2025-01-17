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
    public StringProperty addString(String name, String defaultValue ){
            return addString(name,defaultValue,null);
    }

    public StringProperty addString(String name, String defaultValue,PropertyFormat format){
        return addString(null,name,defaultValue,format,false);
    }
    public StringProperty addString(String name, String defaultValue,boolean hidden ){
        return addString(null,name,defaultValue,null,hidden);
    }
    public StringProperty addString(String title,String name, String defaultValue,PropertyFormat format,boolean hidden) {
        return addString(title,name,defaultValue,format,hidden,null);
    }

    public StringProperty addConfig(ActivityConfig config){
        Map<String,String> meta = new HashMap<>();
        meta.put("configClass",config.getConfigClass());
        return addString(config.getName(),config.getPropertyName(),config.getName() + " Default",PropertyFormat.CONFIG_CHOOSER,false,meta);
    }
    @JsonIgnore
    public String getJson(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //  mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "{}";
    }
    public StringProperty addString(String title,String name, String defaultValue,PropertyFormat format,boolean hidden,Map<String,String> meta){
        StringProperty property = new StringProperty();
        property.setDefaultValue(defaultValue);
        if(title == null){
            property.setTitle(getTitle(name));
        }else{
            property.setTitle(title);
        }
         if(hidden){
             property.setType("hidden");
         }
        if(format != null){
            property.setFormat(format);
        }
        if(meta != null){
            property.setMeta(meta);
        }
        properties.put(name,property);
        return property;
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
