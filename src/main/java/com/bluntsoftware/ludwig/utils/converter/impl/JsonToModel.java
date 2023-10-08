package com.bluntsoftware.ludwig.utils.converter.impl;


import com.bluntsoftware.ludwig.domain.Entity;
import com.bluntsoftware.ludwig.domain.Model;
import com.bluntsoftware.ludwig.domain.Variable;
import com.bluntsoftware.ludwig.utils.Inflector;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class JsonToModel extends ModelConverter{

  private final String name;
  public JsonToModel(String name){
    this.name = name != null ? name : "Untitled";
  }

  @Override
  public Model convert(Map<String, Object> data) {
    return createFromObjectMap(this.name,data);
  }

  public Model createFromObjectJson(String name, String json) throws JsonProcessingException {
        return createFromObjectMap(name,convertToMap(new ObjectMapper().readValue(json,Map.class)));
  }

  public Model createFromObjectMap(String name, Map<String, Object> map){
      Model model =  Model.builder().name(name).entities(buildEntities(name,map)).build();
      log.info(writeAsString(model));
      return model;

  }

  public static List<Entity> buildFromMap(Map<String, Object> map){
    List<Entity> entities = new ArrayList<>();
    for(Map.Entry<String,Object> entry:map.entrySet()){
      Object value = entry.getValue();
      String fieldName = entry.getKey();
      if (value instanceof Map) {
        entities.addAll(buildEntities(capitalize(fieldName), (Map<String, Object>) value));
      }
    }
    return entities;
  }

  public static List<Entity> buildEntities(String name,Map<String, Object> map) {
      List<Entity> entities = new ArrayList<>();
      Entity entity = Entity.builder().name(name).build();
      List<Variable> variables = new ArrayList<>();
      for(Map.Entry<String,Object> entry:map.entrySet()){
          Object value = entry.getValue();
          String fieldName = removeSpaces(entry.getKey());
          if (value instanceof List) {
              String type = handleList(fieldName,(List<Object>)value,entities);
              variables.add(Variable.builder().primary(false).list(true).name(fieldName).type(type).build());
              log.info("found List for key {} : {}", fieldName ,writeAsString(value));
          } else if (value instanceof Map) {
              entities.addAll(buildEntities(capitalize(fieldName),(Map<String,Object>)value));
              variables.add(Variable.builder().name(fieldName).type(capitalize(removeSpaces(entry.getKey()))).build());
              log.info("found Map {} : {}",fieldName, writeAsString(value));
          } else{
              Boolean idKey = false;
              if(fieldName.equalsIgnoreCase("id")){
                idKey = true;
              }
              if(value != null){
                variables.add(Variable.builder().primary(idKey).name(fieldName).type(value.getClass().getSimpleName()).build());
                log.info("found {} {} : {}", value.getClass().getName() ,fieldName,value);
              }
          }
      }
      entity.setVariables(variables);
      entities.add(entity);
      return entities;
  }

  public static String handleList(String fieldName,List<Object> list,List<Entity> entities) {
      String type = "String";
      if(list != null && !list.isEmpty()){
        Object item = list.get(0);
        if(item instanceof List ){
          type = handleList(fieldName,(List<Object>)item,entities);
        }else if( item instanceof Map){
          type =  capitalize(singular(fieldName));
          entities.addAll(buildEntities(type,(Map<String,Object>)item));
        }else{
          type =  item.getClass().getSimpleName();
        }
      }
      return type;
  }

    public static String capitalize(String str) {
        return Inflector.getInstance().capitalize(str);
    }

    public static String singular(String str) {
        return Inflector.getInstance().singularize(str);
    }

    public static String removeSpaces(String str) {
        return str.replace(" ","");
    }

    public static String writeAsString(Object obj){
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch(JsonProcessingException jsonProcessingException){
            log.error(jsonProcessingException.getMessage());
        }
        return "";
    }

}
