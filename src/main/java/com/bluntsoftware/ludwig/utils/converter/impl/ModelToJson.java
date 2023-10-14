package com.bluntsoftware.ludwig.utils.converter.impl;

import com.bluntsoftware.ludwig.domain.Entity;
import com.bluntsoftware.ludwig.domain.Model;
import com.bluntsoftware.ludwig.domain.Variable;
import com.bluntsoftware.ludwig.utils.FieldType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
public class ModelToJson {

    Map<String,Entity> objectTypes = new HashMap<>();

    public Map<String,Object> convert(Model model, String entityName){
        model.getEntities().forEach(e-> objectTypes.put(e.getName(),e));
        return this.handleEntity(objectTypes.get(entityName));
    }
    Map<String,Object> handleEntity(Entity entity){
        return handleVariables(entity.getVariables());
    }

    Map<String,Object> handleVariables(List<Variable> variables){
        Map<String,Object> ret = new HashMap<>();
        variables.forEach( v -> ret.put(v.getName(),handleVariable(v)));
        return ret;
    }
    Object handleVariable(Variable variable) {
        if(Boolean.TRUE.equals(variable.getList())){
            List<Object> list = new ArrayList<>();
            variable.setList(Boolean.FALSE);
            list.add(handleVariable(variable));
            return list;
        } else if(objectTypes.containsKey(variable.getType())){
            return handleEntity(objectTypes.get(variable.getType()));
        } else {
            FieldType type = FieldType.byJavaType(variable.getType());
            if(type.getSchemaType().equalsIgnoreCase("boolean")){
                return Boolean.FALSE;
            }else if(type.getSchemaType().equalsIgnoreCase("integer")){
                return Integer.parseInt("1");
            }else if(type.getSchemaType().equalsIgnoreCase("number")){
                return BigDecimal.valueOf(1L);
            }else if(type.getSchemaType().equalsIgnoreCase("double")){
                return Double.valueOf("1.1");
            }
            return "";
        }
    }

    public static void main(String[] args) {
        Model model = Model.builder()
                .name("Business")
                .entity(Entity.builder().name("Contact")
                        .variable(Variable.builder().type(FieldType.STRING.getJavaType()).name("firstName").notNull(true).build())
                        .variable(Variable.builder().type(FieldType.STRING.getJavaType()).name("lastName").notNull(true).build())
                        .variable(Variable.builder().type(FieldType.STRING.getJavaType()).name("phone").notNull(true).build())
                        .variable(Variable.builder().type(FieldType.STRING.getJavaType()).name("email").build())
                        .variable(Variable.builder().type(FieldType.INT.getJavaType()).name("age").build())
                        .build())
                .entity(Entity.builder().name("Customer")
                        .variable(Variable.builder().type(FieldType.STRING.getJavaType()).name("id").primary(true).build())
                        .variable(Variable.builder().type(FieldType.STRING.getJavaType()).name("companyName").notNull(true).build())
                        .variable(Variable.builder().type(FieldType.BOOL.getJavaType()).name("enabled").build())
                        .variable(Variable.builder().type("Contact").name("contacts").list(true).build())
                        .variable(Variable.builder().type("Contact").name("contact").build())
                        .build())
                .build();
        ModelToJson modelToJson  = new ModelToJson();
        ObjectMapper mapper = new ObjectMapper();
        try {
            log.info(mapper.writeValueAsString(modelToJson.convert(model,"Customer")));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
