package com.bluntsoftware.ludwig.utils.converter.impl;


import com.bluntsoftware.ludwig.domain.Entity;
import com.bluntsoftware.ludwig.domain.Model;
import com.bluntsoftware.ludwig.domain.Variable;
import com.bluntsoftware.ludwig.utils.FieldType;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class SwaggerToModel extends ModelConverter {
  private static final String PROPERTIES = "properties";

  @Override
  public Model convert(Map<String, Object> data) {
    Map<String,Object> schemas = new HashMap<>();
    if(data.containsKey("openapi")){
      Map<String,Object> components = data.containsKey("components") ?  convertToMap(data.get("components")) : null;
      schemas = components != null && components.containsKey("schemas") ? convertToMap(components.get("schemas")): null;
    }else if(data.containsKey("swagger")){
      schemas = data.containsKey("definitions") ? convertToMap(data.get("definitions")): null;
    }
    return createFromObjectMap("untitled",schemas);
  }

  public Model createFromObjectMap(String name, Map<String, Object> entities){
    return  Model.builder()
      .name(name)
      .entities(handleEntities(entities))
      .build();
  }

  private List<Entity> handleEntities(Map<String, Object> entities) {
    return entities.entrySet()
      .stream()
      .filter(f -> !f.getKey().startsWith("Mono"))
      .filter(f -> !f.getKey().startsWith("Flux"))
      .map(this::handleEntity)
      .collect(Collectors.toList());
  }

  private Entity handleEntity(Map.Entry<String,Object> entry){

      Map<String,Object> entity =  entry.getValue() instanceof Map ? convertToMap(entry.getValue()) : null;
      Map<String,Object> properties = entity != null && entity.containsKey(PROPERTIES)
        && entity.get(PROPERTIES) instanceof Map ? convertToMap(entity.get(PROPERTIES)) : null;

      return Entity.builder()
        .name(entry.getKey())
        .variables( properties != null ? handleVariables(properties) : new ArrayList<>())
        .build();
  }

  private List<Variable> handleVariables(Map<String,Object> properties) {
    return properties.entrySet()
      .stream()
      .map(this::handleVariable)
      .collect(Collectors.toList());
  }

  private Variable handleVariable(Map.Entry<String,Object> property){
    boolean list = false;
    boolean primary = property.getKey().equalsIgnoreCase("id");
    String typeName = FieldType.STRING.getJavaType();
    if(property.getValue() instanceof Map){
      Map<String,Object> props = convertToMap(property.getValue());
      FieldType type = FieldType.bySchemaType(props.getOrDefault("type", "").toString());
      typeName = type != FieldType.NOTFOUND ? type.getJavaType() : typeName;

      if(type == FieldType.LIST){
        list = true;
      }
      //Swagger 2
      if(props.containsKey("items")){
        Map<String,Object> items =  convertToMap(props.get("items"));
        if(items.containsKey("$ref")){
          String ref = items.get("$ref").toString();
          typeName = ref.substring(ref.lastIndexOf("/") + 1);
        }else if(items.containsKey("type")){
          FieldType listType = FieldType.bySchemaType(items.getOrDefault("type", "").toString());
          typeName = listType != FieldType.NOTFOUND ? listType.getJavaType() : typeName;
        }
      }
      //Open Api 3
      if(props.containsKey("$ref")){
        String ref = props.get("$ref").toString();
        typeName = ref.substring(ref.lastIndexOf("/") + 1);
      }
    }

    return Variable.builder()
      .name(property.getKey())
      .primary(primary)
      .list(list)
      .notNull(false)
      .ignore(false)
      .type(typeName)
      .build();
  }
}
