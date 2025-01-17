package com.bluntsoftware.ludwig.conduit.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonSchemaTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void getJson() {
        List<String> gender = new ArrayList<String>();
        gender.add("Male");
        gender.add("Female");
        JsonSchema form = new JsonSchema("UserDetails");
        JsonSchema user = new JsonSchema("User");
        user.addString("first_name","Alex");
        user.addString("last_name","Mcknight");
        user.addString("age","51");
        user.addEnum("gender",gender,"Male");
        user.addString("color","blue");
        user.addString("json","{}",PropertyFormat.JSON);
        form.addRecord("user",user);

        JsonSchema address = new JsonSchema("Address");
        address.addString("address1","816 Stonybrook");
        address.addString("address2","");
        address.addString("city","W. Norriton");
        address.addString("state","PA");
        address.addString("zip","19403");

        form.addRecord("address",address);

        System.out.println(form.getJson());
     //   System.out.println(form.getValue());

        Map<String,StringProperty> paths = form.getSecretStringProperties();

        JsonPath path = new JsonPath( form.getValue());
        for(String prop_path : paths.keySet()){
            Object val = path.getValue(prop_path);
            if(val != null){
                //  System.out.println(val.toString());
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        try {
            String jsonInString = mapper.writeValueAsString(form);
            //  System.out.println(jsonInString);
        //    System.out.println(mapper.writeValueAsString(form.getValue()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}