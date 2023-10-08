package com.bluntsoftware.ludwig.utils.converter.impl;

import com.bluntsoftware.ludwig.domain.Entity;
import com.bluntsoftware.ludwig.domain.Model;
import com.bluntsoftware.ludwig.domain.Variable;
import com.bluntsoftware.ludwig.utils.FieldType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ModelToJsonSchema {

    Map<String,Entity> objectTypes = new HashMap<>();

    List<String> listEntities(Model model){
       return model.getEntities().stream().map(Entity::getName).collect(Collectors.toList());
    }

    Map<String,Object> convert(Model model,String entityName){
        model.getEntities().forEach(e-> objectTypes.put(e.getName(),e));
        return this.handleEntity(objectTypes.get(entityName));
    }
    Map<String,Object> handleEntity(Entity entity){
        Map<String,Object> ret = new HashMap<>();
        ret.put("title",entity.getName());
        ret.put("type","object");
        ret.put("properties",handleVariables(entity.getVariables()));
        return ret;
    }
    Map<String,Object> handleArray(Variable variable){
        Map<String,Object> array = new HashMap<>();
        array.put("type","array");
        array.put("items","");
        return null;
    }

    Map<String,Object> handleVariables(List<Variable> variables){
        Map<String,Object> ret = new HashMap<>();
        variables.forEach( v -> ret.put(v.getName(),handleVariable(v)));
        return ret;
    }
    Map<String,Object> handleVariable(Variable variable) {
        if(Boolean.TRUE.equals(variable.getList())){
            Map<String,Object> list = new HashMap<>();
            list.put("type","array");
            variable.setList(Boolean.FALSE);
            list.put("items",handleVariable(variable));
            return list;
        } else if(objectTypes.containsKey(variable.getType())){
            return handleEntity(objectTypes.get(variable.getType()));
        } else {
            FieldType type = FieldType.byJavaType(variable.getType());
            Map<String,Object> var = new HashMap<>();
            var.put("type",type.getSchemaType());
            return var;
        }
    }
    public static void main(String[] args) {
        Model model = Model.builder()
                .name("Business")
                .entity(Entity.builder().name("Contact")
                        .variable(Variable.builder().type(FieldType.STRING.getJavaType()).name("firstName").notNull(true).build())
                        .variable(Variable.builder().type(FieldType.STRING.getJavaType()).name("lastname").notNull(true).build())
                        .variable(Variable.builder().type(FieldType.STRING.getJavaType()).name("phone").notNull(true).build())
                        .variable(Variable.builder().type(FieldType.STRING.getJavaType()).name("email").build())
                        .variable(Variable.builder().type(FieldType.INT.getJavaType()).name("age").build())
                        .build())
                .entity(Entity.builder().name("Customer")
                        .variable(Variable.builder().type(FieldType.STRING.getJavaType()).name("id").primary(true).build())
                        .variable(Variable.builder().type(FieldType.STRING.getJavaType()).name("name").notNull(true).build())
                        .variable(Variable.builder().type(FieldType.BOOL.getJavaType()).name("enabled").build())
                        .variable(Variable.builder().type("Contact").name("contacts").list(true).build())
                        .build())
                .build();
        ModelToJsonSchema modelToJsonSchema = new ModelToJsonSchema();
        ObjectMapper mapper = new ObjectMapper();
        try {
            log.info(mapper.writeValueAsString(modelToJsonSchema.convert(model,"Customer")));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
