package com.bluntsoftware.ludwig.conduit.config.sql;


import com.bluntsoftware.ludwig.conduit.config.ConfigTestResult;
import com.bluntsoftware.ludwig.conduit.config.sql.domain.SQLConnection;
import com.bluntsoftware.ludwig.conduit.config.ActivityConfigImpl;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SQLConnectionConfig extends ActivityConfigImpl<SQLConnection> {

    @Override
    public JsonSchema getRecord() {
       return SQLConnection.getSchema();
    }



    @Override
    public ConfigTestResult test(Map<String, Object> connection) {
        return ConfigTestResult.builder()
                .error(true)
                .message(String.format("TODO: Implement Test for Params %s", connection))
                .build();
    }
}
