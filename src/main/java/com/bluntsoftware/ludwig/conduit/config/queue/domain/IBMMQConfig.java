package com.bluntsoftware.ludwig.conduit.config.queue.domain;

import com.bluntsoftware.ludwig.conduit.utils.schema.EntitySchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.PropertyFormat;

public class IBMMQConfig implements EntitySchema {
    @Override
    public JsonSchema getJsonSchema() {
        JsonSchema connection = new JsonSchema("IBM MQ Connection");
        connection.addString("host","localhost",null);
        connection.addString("port","61616",null);
        connection.addString("username","admin",null);
        connection.addString("password","admin", PropertyFormat.PASSWORD);
        connection.addString("IBMMQ-Manager","q-manager","QM1",null,false);
        connection.addString("IBMMQ-Channel","channel","DEV.APP.SVRCONN",null,false);
        return connection;
    }
}
