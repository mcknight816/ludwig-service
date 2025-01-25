package com.bluntsoftware.ludwig.conduit.config.nosql;


import com.bluntsoftware.ludwig.conduit.config.ConfigTestResult;
import com.bluntsoftware.ludwig.conduit.config.nosql.domain.CouchbaseConnection;
import com.bluntsoftware.ludwig.conduit.config.ActivityConfigImpl;
import org.springframework.stereotype.Service;

@Service
public class CouchbaseConnectionConfig extends ActivityConfigImpl<CouchbaseConnection> {
    @Override
    public ConfigTestResult testConfig(CouchbaseConnection config) {
        return ConfigTestResult.builder()
                .error(true)
                .message(String.format("TODO: Implement Test for Params %s", config))
                .build();
    }
}
