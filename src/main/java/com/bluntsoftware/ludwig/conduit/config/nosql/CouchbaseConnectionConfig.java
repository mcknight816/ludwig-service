package com.bluntsoftware.ludwig.conduit.config.nosql;


import com.bluntsoftware.ludwig.conduit.config.ConfigTestResult;
import com.bluntsoftware.ludwig.conduit.config.nosql.domain.CouchbaseConnection;
import com.bluntsoftware.ludwig.conduit.config.ActivityConfigImpl;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CouchbaseConnectionConfig extends ActivityConfigImpl<CouchbaseConnection> {

    @Override
    public JsonSchema getRecord() {
         return CouchbaseConnection.getSchema();
    }



    @Override
    public ConfigTestResult test(Map<String, Object> connection) {
        return ConfigTestResult.builder()
                .error(true)
                .message(String.format("TODO: Implement Test for Params %s", connection))
                .build();
    }
}
