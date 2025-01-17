package com.bluntsoftware.ludwig.conduit.config.nosql;


import com.bluntsoftware.ludwig.conduit.config.nosql.domain.CouchbaseConnection;
import com.bluntsoftware.ludwig.conduit.config.ActivityConfigImpl;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public class CouchbaseConnectionConfig extends ActivityConfigImpl<CouchbaseConnection> {

    @Override
    public JsonSchema getRecord() {
         return CouchbaseConnection.getSchema();
    }



    @Override
    public Map test() {
        return null;
    }
}
