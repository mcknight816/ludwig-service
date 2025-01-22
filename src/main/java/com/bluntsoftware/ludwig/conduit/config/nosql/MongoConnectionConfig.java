package com.bluntsoftware.ludwig.conduit.config.nosql;

import com.bluntsoftware.ludwig.conduit.config.ConfigTestResult;
import com.bluntsoftware.ludwig.conduit.config.ActivityConfigImpl;
import com.bluntsoftware.ludwig.conduit.config.nosql.domain.MongoConnect;
import com.bluntsoftware.ludwig.conduit.service.nosql.mongo.MongoConnection;
import com.bluntsoftware.ludwig.conduit.service.nosql.mongo.MongoRepository;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import org.bson.Document;
@Service
public class MongoConnectionConfig extends ActivityConfigImpl<MongoConnect> {

    @Override
    public JsonSchema getRecord() {
        return MongoConnect.getSchema();
    }
    @Override
    public ConfigTestResult test(Map<String, Object> connection) {
        MongoConnect mongoConnection = this.getConfig(connection);
        ConfigTestResult error = ConfigTestResult.builder()
                .error(true)
                .message(String.format("failed Test for Params %s", connection))
                .build();

        MongoRepository repo = new MongoRepository(new MongoConnection(mongoConnection.getUri()));
        Map<String,Object> data = new HashMap<>();
        data.put("name","test");
        data.put("description","test for mongo connection config");
        try {
            Document doc = repo.save("test-db", "test-collection", data, false);
            if (doc != null && doc.containsKey("_id")) {
                return ConfigTestResult.builder()
                        .success(true)
                        .message(String.format("Successful connection Test for db %s", "test-db"))
                        .build();
            }
        } catch (Exception e) {
            error.setHint(e.getMessage());
        }

        return error;
    }
}
