package com.bluntsoftware.ludwig.conduit.activities.mongo;


import com.bluntsoftware.ludwig.conduit.activities.mongo.domain.MongoById;

import com.bluntsoftware.ludwig.conduit.activities.mongo.domain.MongoSettings;
import com.bluntsoftware.ludwig.conduit.service.nosql.mongo.MongoRepository;
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
public class MongoDeleteActivity  extends MongoActivity {

    @Autowired
    public MongoDeleteActivity(ActivityConfigRepository activityConfigRepository) {
        super(activityConfigRepository);
    }

    @Override
    public Map<String, Object> run(Map<String, Object> input) throws Exception{
        MongoById byId = convertValue(input, MongoById.class);
        MongoSettings settings = byId.getSettings();
        //validateInput(input);
        MongoRepository mongoRepository = getRepository(settings.getConnection());
        return mongoRepository.remove( settings.getDatabase(),settings.getCollection(),byId.getId());
    }
    @Override
    public JsonSchema getSchema() {
        return MongoById.builder().build().getJsonSchema();
    }
}
