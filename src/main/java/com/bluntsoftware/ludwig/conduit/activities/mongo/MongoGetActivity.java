package com.bluntsoftware.ludwig.conduit.activities.mongo;

import com.bluntsoftware.ludwig.conduit.activities.mongo.domain.MongoSettings;
import com.bluntsoftware.ludwig.conduit.config.nosql.MongoConnectionConfig;
import com.bluntsoftware.ludwig.conduit.activities.mongo.domain.ById;
import com.bluntsoftware.ludwig.conduit.nosql.mongo.MongoRepository;
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
public class MongoGetActivity extends MongoActivity {

    @Autowired
    public MongoGetActivity(MongoConnectionConfig mongoConnectionConfig, ActivityConfigRepository activityConfigRepository) {
        super(mongoConnectionConfig,activityConfigRepository);
    }
    @Override
    public Map<String, Object> run(Map<String, Object> input)throws Exception {
        ById byId = convertValue(input, ById.class);
        MongoSettings settings  = byId.getSettings();
        MongoRepository mongoRepository = getRepository(settings.getConnection());
        return mongoRepository.getById(byId.getId(), settings.getDatabase(),settings.getCollection());
    }

    @Override
    public JsonSchema getSchema() {
        return ById.getSchema();
    }

    @Override
    public Map<String, Object> getOutput() {
        return new HashMap<>();
       /* MongoCollection<Document> col = getCollection();
        if(col == null){
            return new HashMap<>();
        }
        BasicDBObject sort = BasicDBObject.parse("{$natural:-1}");
        return col.find().sort(sort).limit(1).first(); */
    }
}
