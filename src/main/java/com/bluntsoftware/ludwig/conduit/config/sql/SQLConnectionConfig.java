package com.bluntsoftware.ludwig.conduit.config.sql;


import com.bluntsoftware.ludwig.conduit.config.ConfigTestResult;
import com.bluntsoftware.ludwig.conduit.config.sql.domain.SQLConnection;
import com.bluntsoftware.ludwig.conduit.config.ActivityConfigImpl;
import org.springframework.stereotype.Service;

@Service
public class SQLConnectionConfig extends ActivityConfigImpl<SQLConnection> {

    @Override
    public ConfigTestResult testConfig(SQLConnection config) {
        return ConfigTestResult.builder()
                .error(true)
                .message(String.format("TODO: Implement Test for Params %s", config))
                .build();
    }
}
