package com.bluntsoftware.ludwig.conduit.activities.mongo;


import com.bluntsoftware.ludwig.conduit.config.nosql.MongoConnectionConfig;
import com.bluntsoftware.ludwig.conduit.activities.mongo.domain.MongoById;
import com.bluntsoftware.ludwig.conduit.nosql.mongo.MongoRepository;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by Alex Mcknight on 2/27/2017.
 *
 */
@Service
public class MongoDeleteActivity  extends MongoActivity {

    @Autowired
    public MongoDeleteActivity(MongoConnectionConfig mongoConnectionConfig, ActivityConfigRepository activityConfigRepository) {
        super(mongoConnectionConfig,activityConfigRepository);
    }

    @Override
    public Map<String, Object> run(Map<String, Object> input) throws Exception{
        validateInput(input);
        MongoRepository mongoRepository = getRepository(input.get("connection").toString());
        String databaseName =  input.get("database").toString();
        String collectionName = input.get("collection").toString();
        String id =input.get("id").toString();
        return mongoRepository.remove( databaseName,collectionName,id);
    }
    @Override
    public JsonSchema getSchema() {
        return MongoById.getSchema();
    }
}
