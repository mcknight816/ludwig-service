package com.bluntsoftware.ludwig.conduit.config.sql;


import com.bluntsoftware.ludwig.conduit.config.ConfigTestResult;
import com.bluntsoftware.ludwig.conduit.config.sql.domain.SQLConnectionConfig;
import com.bluntsoftware.ludwig.conduit.config.ActivityConfigImpl;
import org.springframework.stereotype.Service;

@Service
public class SQLConfigActivity extends ActivityConfigImpl<SQLConnectionConfig> {

    @Override
    public ConfigTestResult testConfig(SQLConnectionConfig config) {
        return ConfigTestResult.builder()
                .error(true)
                .message(String.format("TODO: Implement Test for Params %s", config))
                .build();
    }
}
