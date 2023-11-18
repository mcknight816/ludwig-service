package com.bluntsoftware.ludwig.conduit.config.nosql;

import com.bluntsoftware.ludwig.conduit.config.nosql.domain.MongoConnection;
import com.bluntsoftware.ludwig.conduit.impl.ActivityConfigImpl;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class MongoConnectionConfig extends ActivityConfigImpl {

    @Override
    public JsonSchema getRecord() {
        return MongoConnection.builder().build().getRecord();
    }

    @Override
    public Map test() {
        return null;
    }
}
