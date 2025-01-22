package com.bluntsoftware.ludwig.conduit.config.nosql.domain;


import com.bluntsoftware.ludwig.conduit.utils.schema.*;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
@Data
@Builder
public class CouchbaseConnection implements EntitySchema {
    String server = "localhost";
    String port = "27017";
    String userName;
    String password;

    public static JsonSchema getSchema() {
        Map<String, Property> props = new HashMap<>();
        props.put("server", StringProperty.builder().title("Server Name").build());
        props.put("port", StringProperty.builder().title("Port").build());
        props.put("userName", StringProperty.builder().title("User Name").build());
        props.put("password", StringProperty.builder().title("Password").format(PropertyFormat.PASSWORD).build());
        return  JsonSchema.builder()
                .title("Connection")
                .properties(props)
                .build();
    }
}
