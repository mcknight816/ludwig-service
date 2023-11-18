package com.bluntsoftware.ludwig.conduit.domain;

import com.bluntsoftware.ludwig.conduit.config.nosql.MongoConnectionConfig;
import com.bluntsoftware.ludwig.conduit.schema.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class MongoSettings implements EntitySchema {
    String database;
    String collection;
    String userManaged;
    String allowFriends;
    String connection;

    public static JsonSchema getSchema() {
        String[] trueFalse = {"true","false"};
        Map<String, Property> props = new HashMap<>();
        props.put("database", StringProperty.builder().title("Database Name").build());
        props.put("collection",  StringProperty.builder().title("Collection Name").build());
        props.put("userManaged", EnumProperty.builder().enumeration(trueFalse).title("User Managed").build());
        props.put("allowFriends",  EnumProperty.builder().enumeration(trueFalse).title("Allow Friends").build());
        props.put("connection",  StringProperty.builder().title("Mongo Connection")
                        .meta("configClass", MongoConnectionConfig.class.getTypeName())
                        .format("configChooser").build());

        return JsonSchema.builder()
                .properties(props)
                .build();
    }
}
