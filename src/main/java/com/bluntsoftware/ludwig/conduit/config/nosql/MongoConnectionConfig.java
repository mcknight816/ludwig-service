package com.bluntsoftware.ludwig.conduit.config.nosql;

import com.bluntsoftware.ludwig.conduit.config.ConfigTestResult;
import com.bluntsoftware.ludwig.conduit.config.ActivityConfigImpl;
import com.bluntsoftware.ludwig.conduit.config.nosql.domain.MongoConnect;
import com.bluntsoftware.ludwig.conduit.service.nosql.mongo.MongoConnection;
import com.bluntsoftware.ludwig.conduit.service.nosql.mongo.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import org.bson.Document;

/**
 * MongoConnectionConfig is a configuration class that extends ActivityConfigImpl for handling
 * MongoDB-specific connection configurations. This class provides functionalities to validate
 * and test connectivity with a MongoDB instance using the provided configuration details.
 *
 * The primary responsibility of this class is to implement the `testConfig` method which executes
 * a connection test against a MongoDB server to ensure the configuration is valid.
 *
 * Key functionalities include:
 * - Creating and initializing a MongoRepository instance using provided connection details.
 * - Performing a test operation by saving a sample document in a test database and collection.
 * - Validating the successful completion of the test operation and returning a ConfigTestResult
 *   indicating success or failure. On failure, it provides a detailed error message.
 */
@Service
public class MongoConnectionConfig extends ActivityConfigImpl<MongoConnect> {

    @Override
    public ConfigTestResult testConfig(MongoConnect config) {
        ConfigTestResult error = ConfigTestResult.builder()
                .error(true)
                .message(String.format("failed Test for Params %s", config))
                .build();

        MongoRepository repo = new MongoRepository(new MongoConnection(config.getUri()));
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
