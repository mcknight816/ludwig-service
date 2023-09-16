package com.bluntsoftware.ludwig.conduit.schema;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JsonSchema extends RecordProperty {

    public interface StringPropertyFilter {
        Boolean hasProperty(StringProperty property);
    }
    StringPropertyFilter secretFilter = (property) -> (property.getFormat() != null
            && property.getFormat().equalsIgnoreCase("password"));

    public JsonSchema(String title) {
        super(title);
    }
    @JsonIgnore
    public String getJson(){
         ObjectMapper mapper = new ObjectMapper();
         mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
         try {
             return mapper.writeValueAsString(this);
         } catch (JsonProcessingException e) {
             e.printStackTrace();
         }
         return "{}";
     }
    public Map<String,StringProperty> getStringPropertyPaths(){
        return getStringProperties(this,null,null);
    }
    public Map<String,StringProperty> getSecretStringProperties(){
        return getStringPropertyPaths(secretFilter);
    }

    public Map<String,StringProperty> getStringPropertyPaths(StringPropertyFilter filter){
        return getStringProperties(this,null,filter);
     }

     Map<String,StringProperty> getStringProperties(RecordProperty record,String path,StringPropertyFilter filter){
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
             }else if(property instanceof RecordProperty){
                 Map<String,StringProperty> paths = getStringProperties((RecordProperty)property,path + key,filter);
                 ret.putAll(paths);
             }
         }
        return ret;
     }


    public static void main(String[] args) {
        List<String> gender = new ArrayList<String>();
        gender.add("Male");
        gender.add("Female");

        JsonSchema form = new JsonSchema("User");
        form.addString("first_name","Alex");
        form.addString("last_name","Mcknight");
        form.addString("age","51");
        form.addEnum("gender",gender,"Male");
        form.addString("color","blue","color");

        RecordProperty address = new RecordProperty("Address");
        address.addString("address1","816 Stonybrook");
        address.addString("address2","");
        address.addString("city","W. Norriton");
        address.addString("state","PA");
        address.addString("zip","19403");

        form.addRecord("address",address);


        Map<String,StringProperty> paths = form.getSecretStringProperties();

        JsonPath path = new JsonPath( form.getValue());
        for(String prop_path : paths.keySet()){
            Object val = path.getValue(prop_path);
            if(val != null){
                System.out.println(val.toString());
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            String jsonInString = mapper.writeValueAsString(form);
            System.out.println(jsonInString);
            System.out.println(mapper.writeValueAsString(form.getValue()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
