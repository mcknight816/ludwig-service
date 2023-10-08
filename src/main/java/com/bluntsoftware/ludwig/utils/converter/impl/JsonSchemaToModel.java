package com.bluntsoftware.ludwig.utils.converter.impl;

import com.bluntsoftware.ludwig.domain.Entity;
import com.bluntsoftware.ludwig.domain.Model;
import com.bluntsoftware.ludwig.domain.Variable;
import com.bluntsoftware.ludwig.utils.FieldType;
import com.bluntsoftware.ludwig.utils.Inflector;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Slf4j
public class JsonSchemaToModel extends ModelConverter {

  private final String name;
  public JsonSchemaToModel(String name){
    this.name = name != null ? name : "Untitled";
  }

  @Override
  public Model convert(Map<String, Object> data) {
    Map<String, Object> properties = convertToMap(data.get("properties"));
    String entityName = this.name;
    if(data.containsKey("title")){
      entityName = data.get("title").toString();
    }
    Model model =  Model.builder().name(this.name).entities(buildEntities(entityName,properties)).build();
    log.info(writeAsString(model));
    return model;
  }

  public  List<Entity> buildEntities(String name, Map<String, Object> properties) {
    List<Entity> entities = new ArrayList<>();
    Entity entity = Entity.builder().name(name).build();
    List<Variable> variables = new ArrayList<>();
    for(Map.Entry<String,Object> entry:properties.entrySet()){
      Map<String, Object> property = convertToMap(entry.getValue());
      String fieldName = entry.getKey();
      String type = property.get("type").toString();
      if(type.equalsIgnoreCase("array")){
        String listType = handleList(fieldName,convertToMap(property.get("items")),entities);
        variables.add(Variable.builder().primary(false).list(true).name(fieldName).type(listType).build());
      }else if(type.equalsIgnoreCase("object")){
        entities.addAll(buildEntities(capitalize(fieldName),convertToMap(property.get("properties"))));
        variables.add(Variable.builder().name(fieldName).type(capitalize(entry.getKey())).build());
      }else{
        boolean primary = fieldName.equalsIgnoreCase("id") || fieldName.equalsIgnoreCase("_id") ;
        variables.add(Variable.builder()
          .primary(primary)
          .name(fieldName)
          .type(FieldType.bySchemaType(type).getJavaType())
          .notNull(false)
          .ignore(false)
          .list(false)
          .build());
      }
    }
    entity.setVariables(variables);
    entities.add(entity);
    return entities;
  }

  public String handleList(String fieldName,Map<String,Object> items,List<Entity> entities) {
    String type = items.get("type").toString();
      if(type.equalsIgnoreCase("array")){
        type = handleList(fieldName, convertToMap(items.get("items")),entities);
      }else if( type.equalsIgnoreCase("object")){
        type =  capitalize(singular(fieldName));
        entities.addAll(buildEntities(type,convertToMap(items.get("properties"))));
      }else{
        type = FieldType.bySchemaType(type).getJavaType();
      }
    return type;
  }

  public static String capitalize(String str) {
    return Inflector.getInstance().capitalize(str);
  }

  public static String singular(String str) {
    return Inflector.getInstance().singularize(str);
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
