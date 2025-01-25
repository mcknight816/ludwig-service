package com.bluntsoftware.ludwig.conduit.activities.couch;


import com.bluntsoftware.ludwig.conduit.config.nosql.CouchbaseConnectionConfig;
import com.bluntsoftware.ludwig.conduit.service.nosql.couch.CouchRepository;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by Alex Mcknight on 2/27/2017.
 *
 */
@Service
public class CouchDeleteActivity extends CouchActivity {

    @Autowired
    public CouchDeleteActivity(CouchbaseConnectionConfig couchConnectionConfig, ActivityConfigRepository activityConfigRepository) {
        super(couchConnectionConfig,activityConfigRepository);
    }

    @Override
    public Map<String, Object> run(Map<String, Object> input) throws Exception{
        validateInput(input);
        CouchRepository mongoRepository = getRepository(input);
        String databaseName =  input.get("database").toString();
        String collectionName = input.get("collection").toString();
        String id =input.get("id").toString();
        return mongoRepository.remove( databaseName,collectionName,id);
    }
    @Override
    public JsonSchema getJsonSchema() {
        JsonSchema schema =  super.getJsonSchema();
        schema.addString("id","",null);
        return schema;
    }
}
