package com.bluntsoftware.ludwig.conduit.activities.mongo;


import com.bluntsoftware.ludwig.conduit.config.nosql.MongoConnectionConfig;
import com.bluntsoftware.ludwig.conduit.activities.mongo.domain.MongoSettings;
import com.bluntsoftware.ludwig.conduit.impl.ActivityImpl;
import com.bluntsoftware.ludwig.conduit.nosql.mongo.MongoConnection;
import com.bluntsoftware.ludwig.conduit.nosql.mongo.MongoRepository;
import com.bluntsoftware.ludwig.conduit.schema.EntitySchema;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex Mcknight on 2/14/2017.
 *
 */
public abstract class MongoActivity extends ActivityImpl  {

    private final MongoConnectionConfig mongoConnectionConfig;

    private final ObjectMapper mapper;

    private final Map<Map<String,Object>, MongoRepository> repos = new HashMap<>();

    public MongoActivity(MongoConnectionConfig mongoConnectionConfig , ActivityConfigRepository activityConfigRepository) {
        super(activityConfigRepository);
        this.mongoConnectionConfig = mongoConnectionConfig;
        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public <T> T convertValue(Map<String,Object> fromValue, Class<T> toValueType) throws IllegalArgumentException {
        return mapper.convertValue(fromValue,toValueType);
    }
    @Override
    public JsonSchema getSchema() {
         return MongoSettings.getSchema();
    }

    MongoRepository getRepository(String connectionName){
        Map<String, Object>  connection = this.getExternalConfigByName(connectionName,MongoConnectionConfig.class);
        if(connection == null){
            connection = mongoConnectionConfig.getDefaults();
        }
      //  Map<String,Object> connection = (Map<String,Object>)config.get("Connection");
        if(connection != null){
            MongoRepository repo = this.repos.get(connection);
            if(repo != null){
                return repo;
            }

            String strPort = connection.get("port").toString();
            String server = connection.get("server").toString();
            if(strPort != null && !strPort.equalsIgnoreCase("")) {
                Integer port = Integer.parseInt(strPort);
                repo =  new MongoRepository(new MongoConnection(server,port));
            }else{
                repo = new MongoRepository(new MongoConnection(server,null));
            }
            this.repos.put(connection,repo);
            return repo;
        }
        return null;
    }
    public static void main(String[] args) {

    }

    @Override
    public String getIcon() {
        return "fa-leaf";
    }

    MongoCollection<Document> getCollection(){
          Map<String,Object>  in = getInput();
          MongoRepository repository = getRepository(in.get("connection").toString());
          if(repository != null){
              Object dbObj = in.get("database");
              Object collectionObj = in.get("collection");
              if(dbObj != null && collectionObj != null){
                  return repository.getCollection(dbObj.toString(),collectionObj.toString());
              }
          }
          return null;
    }

    void validateInput(MongoSettings settings) throws Exception{

        MongoRepository repository = getRepository(settings.getConnection());
        if(repository != null){
            Object dbObj = settings.getDatabase();
            Object collectionObj = settings.getCollection();
            if(dbObj== null || dbObj.toString().equalsIgnoreCase("")){
                throw new Exception("Invalid Database Name");
            }
            if(collectionObj == null || collectionObj.toString().equalsIgnoreCase("")){
                throw new Exception("Invalid Collection Name");
            }
        }else{
             throw new Exception("Invalid Repository Connection Settings");
        }
    }

}
