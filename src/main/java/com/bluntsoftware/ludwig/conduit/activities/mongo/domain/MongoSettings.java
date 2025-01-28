package com.bluntsoftware.ludwig.conduit.activities.mongo.domain;

import com.bluntsoftware.ludwig.conduit.config.ai.domain.OpenAiConfig;
import com.bluntsoftware.ludwig.conduit.config.nosql.MongoConnectionConfig;

import com.bluntsoftware.ludwig.conduit.config.nosql.domain.MongoConnect;
import com.bluntsoftware.ludwig.conduit.utils.schema.*;
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
    String connection;
   // String allowFriends;

    public  JsonSchema getJsonSchema() {
        JsonSchema ret =  JsonSchema.builder()
                .title("settings")
                .build();

        String[] trueFalse = {"true","false"};
        Map<String, Property> props = new HashMap<>();
        props.put("database", StringProperty.builder().title("Database Name").build());
        props.put("collection",  StringProperty.builder().title("Collection Name").build());
        props.put("userManaged", EnumProperty.builder().enumeration(trueFalse).defaultValue("false").title("User Managed").build());
        //props.put("allowFriends",  EnumProperty.builder().enumeration(trueFalse).title("Allow Friends").build());
        ret.setProperties(props);
        ret.addConfigDomain("connection",MongoConnect.class);
        return ret;
    }
}
