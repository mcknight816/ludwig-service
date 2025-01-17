package com.bluntsoftware.ludwig.conduit.activities.mongo;


import com.bluntsoftware.ludwig.conduit.config.nosql.MongoConnectionConfig;
import com.bluntsoftware.ludwig.conduit.activities.mongo.domain.MongoSettings;
import com.bluntsoftware.ludwig.conduit.activities.ActivityImpl;
import com.bluntsoftware.ludwig.conduit.nosql.mongo.MongoConnection;
import com.bluntsoftware.ludwig.conduit.nosql.mongo.MongoRepository;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import com.bluntsoftware.ludwig.exception.AppException;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex Mcknight on 2/14/2017.
 *
 */
@Slf4j
public abstract class MongoActivity extends ActivityImpl  {

    private final ObjectMapper mapper;
    private final Map<Map<String,Object>, MongoRepository> repos = new HashMap<>();

    public MongoActivity(ActivityConfigRepository activityConfigRepository) {
        super(activityConfigRepository);
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
        if(this.getActivityConfigRepository() == null){
            log.error("Activity Config Repository Not Found");
        }

        Map<String, Object>  connection = this.getExternalConfigByName(connectionName,MongoConnectionConfig.class);

        if(connection == null){
            log.error("No connection found for name {}", connectionName);
            throw new AppException("connection " + connectionName + " not found");
        }
      //  Map<String,Object> connection = (Map<String,Object>)config.get("Connection");
        if(connection != null){
            MongoRepository repo = this.repos.get(connection);
            if(repo != null){
                return repo;
            }

            if(connection.containsKey("uri")){
                repo = new MongoRepository(new MongoConnection(connection.get("uri").toString()));
            }else{
                String strPort = connection.get("port").toString();
                String server = connection.get("server").toString();
                if(strPort != null && !strPort.equalsIgnoreCase("")) {
                    Integer port = Integer.parseInt(strPort);
                    repo =  new MongoRepository(new MongoConnection(server,port));
                }else{
                    repo = new MongoRepository(new MongoConnection(server,null));
                }
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
