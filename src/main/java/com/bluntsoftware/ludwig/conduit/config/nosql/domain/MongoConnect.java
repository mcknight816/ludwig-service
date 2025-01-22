package com.bluntsoftware.ludwig.conduit.config.nosql.domain;


import com.bluntsoftware.ludwig.conduit.utils.schema.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class MongoConnect implements EntitySchema {
    String server = "localhost";
    String port = "27017";
    String userName;
    String password;
    String uri = "mongodb://localhost:27017";
    public static JsonSchema getSchema() {
        Map<String, Property> props = new HashMap<>();
        props.put("server", StringProperty.builder().title("Server Name").defaultValue("localhost").build());
        props.put("port", StringProperty.builder().title("Port").build());
        props.put("uri", StringProperty.builder().title("uri").defaultValue("mongodb://localhost:27017").build());
        props.put("userName", StringProperty.builder().title("User Name").build());
        props.put("password", StringProperty.builder().title("Password").format(PropertyFormat.PASSWORD).build());
        return  JsonSchema.builder().title("Connection")
                .properties(props)
                .build();
    }
}
