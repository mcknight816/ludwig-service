package com.bluntsoftware.ludwig.conduit.config.queue.domain;

import com.bluntsoftware.ludwig.conduit.utils.schema.EntitySchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.PropertyFormat;

public class ActiveMQConfig implements EntitySchema {
    @Override
    public JsonSchema getJsonSchema() {
        JsonSchema connection = new JsonSchema("Active MQ Connection");
        connection.addString("host","localhost",null);
        connection.addString("port","61616",null);
        connection.addString("username","admin",null);
        connection.addString("password","admin", PropertyFormat.PASSWORD);
        return connection;
    }
}
