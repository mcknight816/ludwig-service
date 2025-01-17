package com.bluntsoftware.ludwig.conduit.config.queue;


import com.bluntsoftware.ludwig.conduit.config.ActivityConfigImpl;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.schema.PropertyFormat;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class QConnectionConfig extends ActivityConfigImpl {


    @Override
    public JsonSchema getRecord() {
        JsonSchema connection = new JsonSchema("connection");

        List<String> binder = new ArrayList<String>();
        binder.add("Active MQ");
        binder.add("IBM MQ");

        connection.addEnum("binder",binder,"Active MQ");
        connection.addString("host","localhost",null);
        connection.addString("port","61616",null);
        connection.addString("username","admin",null);
        connection.addString("password","admin", PropertyFormat.PASSWORD);

        connection.addString("IBMMQ-Manager","q-manager","QM1",null,false);
        connection.addString("IBMMQ-Channel","channel","DEV.APP.SVRCONN",null,false);
        return connection;
    }



    @Override
    public Map test() {
        return null;
    }
}
