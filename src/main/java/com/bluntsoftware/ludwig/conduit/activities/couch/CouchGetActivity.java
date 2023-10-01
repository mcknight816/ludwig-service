package com.bluntsoftware.ludwig.conduit.activities.couch;


import com.bluntsoftware.ludwig.conduit.config.nosql.CouchbaseConnectionConfig;
import com.bluntsoftware.ludwig.conduit.nosql.couch.CouchRepository;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex Mcknight on 2/27/2017.
 *
 */
@Service
public class CouchGetActivity extends CouchActivity {


    @Autowired
    public CouchGetActivity(CouchbaseConnectionConfig couchConnectionConfig, ActivityConfigRepository activityConfigRepository) {
        super(couchConnectionConfig,activityConfigRepository);
    }

    @Override
    public Map<String, Object> run(Map<String, Object> input)throws Exception {
        validateInput(input);
        CouchRepository mongoRepository = getRepository(input);
        String databaseName =  input.get("database").toString();
        String collectionName = input.get("collection").toString();
        String id = input.get("id").toString();
        return mongoRepository.getById(id, databaseName,collectionName);
    }

    @Override
    public JsonSchema getSchema() {
        JsonSchema schema =  super.getSchema();
        schema.addString("id","",null);
        return schema;
    }

    @Override
    public Map<String, Object> getOutput() {
        return new HashMap<>();
    }
}
