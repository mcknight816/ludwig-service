package com.bluntsoftware.ludwig.conduit.config.nosql;


import com.bluntsoftware.ludwig.conduit.impl.ActivityConfigImpl;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public class CouchbaseConnectionConfig extends ActivityConfigImpl {

    @Override
    public JsonSchema getRecord() {
        JsonSchema connection = new JsonSchema("connection");
        connection.addString("server","localhost",null);
        connection.addString("port","27017",null);
        connection.addString("user",null,null);
        connection.addString("password",null,"password");
        return connection;
    }

    @Override
    public Map test() {
        return null;
    }
}
