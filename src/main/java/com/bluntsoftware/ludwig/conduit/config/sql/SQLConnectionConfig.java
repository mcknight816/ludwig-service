package com.bluntsoftware.ludwig.conduit.config.sql;


import com.bluntsoftware.ludwig.conduit.config.sql.domain.SQLConnection;
import com.bluntsoftware.ludwig.conduit.impl.ActivityConfigImpl;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Service
public class SQLConnectionConfig extends ActivityConfigImpl<SQLConnection> {

    @Override
    public JsonSchema getRecord() {
       return SQLConnection.getSchema();
    }

    @Override
    public SQLConnection getConfig() {
        return SQLConnection.builder().build();
    }

    @Override
    public Map test() {
        return null;
    }
}
