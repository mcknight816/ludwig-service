package com.bluntsoftware.ludwig.conduit.activities.mongo;


import com.bluntsoftware.ludwig.conduit.config.nosql.MongoConnectionConfig;
import com.bluntsoftware.ludwig.conduit.activities.mongo.domain.MongoGetById;
import com.bluntsoftware.ludwig.conduit.nosql.mongo.MongoRepository;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
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
        validateInput(input);
        MongoRepository mongoRepository = getRepository(input.get("connection").toString());
        String databaseName =  input.get("database").toString();
        String collectionName = input.get("collection").toString();
        String id = input.get("id").toString();
        return mongoRepository.getById(id, databaseName,collectionName);
    }

    @Override
    public JsonSchema getSchema() {
        return MongoGetById.getSchema();
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
