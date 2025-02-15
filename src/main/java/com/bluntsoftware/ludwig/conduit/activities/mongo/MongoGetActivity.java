package com.bluntsoftware.ludwig.conduit.activities.mongo;

import com.bluntsoftware.ludwig.conduit.activities.mongo.domain.MongoSettings;
import com.bluntsoftware.ludwig.conduit.activities.mongo.domain.MongoById;
import com.bluntsoftware.ludwig.conduit.service.nosql.mongo.MongoRepository;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;


/**
 * MongoGetActivity is a service class that extends MongoActivity and provides functionality
 * to retrieve a document from a MongoDB collection using its unique identifier (ID).
 * It interacts with the MongoRepository to perform the database operation and handles
 * the necessary configurations and schema validation.
 *
 * This activity is designed to be executed with an input map containing the necessary
 * details, such as the ID of the document, and settings that include connection, database,
 * and collection information. The result of the operation is a map representation of the
 * retrieved document.
 *
 * Methods:
 *
 * - Constructor:
 *   This class requires an ActivityConfigRepository instance to be provided via its constructor
 *   to initialize the parent MongoActivity and enable configuration management.
 *
 * - run(Map<String, Object> input):
 *   Takes an input map, extracts the ID and MongoDB settings, and retrieves a document from
 *   the specified collection in the database. It uses the MongoById class for input deserialization
 *   and the MongoRepository for data retrieval. Throws exceptions in case of errors.
 *
 * - getJsonSchema():
 *   Returns the JSON schema definition of the expected input for this activity. This schema
 *   is based on the MongoById class and specifies the structural constraints required for the input.
 *
 * - getOutput():
 *   Returns an empty map that acts as a placeholder, as this activity's result is directly returned
 *   by the run method. The method can be overridden for extended functionality if needed.
 *
 * Dependencies:
 * - ActivityConfigRepository: For accessing configuration details.
 * - MongoRepository: For the actual interaction with MongoDB.
 * - MongoSettings: For specifying MongoDB connection, database, and collection details.
 * - MongoById: For encapsulating the ID and settings of the requested document.
 * - JsonSchema: For generating the input schema.
 */
@Service
public class MongoGetActivity extends MongoActivity {

    @Autowired
    public MongoGetActivity( ActivityConfigRepository activityConfigRepository) {
        super(activityConfigRepository);
    }
    @Override
    public Map<String, Object> run(Map<String, Object> input)throws Exception {
        MongoById byId = convertValue(input, MongoById.class);
        MongoSettings settings  = byId.getSettings();
        MongoRepository mongoRepository = getRepository(settings.getConnection());
        return mongoRepository.getById(byId.getId(), settings.getDatabase(),settings.getCollection());
    }

    @Override
    public JsonSchema getJsonSchema() {
        return MongoById.builder().build().getJsonSchema();
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
