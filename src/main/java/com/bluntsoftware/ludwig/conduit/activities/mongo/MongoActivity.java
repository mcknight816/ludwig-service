package com.bluntsoftware.ludwig.conduit.activities.mongo;

import com.bluntsoftware.ludwig.conduit.activities.mongo.domain.MongoSettings;
import com.bluntsoftware.ludwig.conduit.activities.ActivityImpl;
import com.bluntsoftware.ludwig.conduit.config.nosql.domain.MongoConnect;
import com.bluntsoftware.ludwig.conduit.service.nosql.mongo.MongoConnection;
import com.bluntsoftware.ludwig.conduit.service.nosql.mongo.MongoRepository;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
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
 */
@Slf4j
public abstract class MongoActivity extends ActivityImpl  {

    private final ObjectMapper mapper;
    private final Map<MongoConnect, MongoRepository> repos = new HashMap<>();

    public MongoActivity(ActivityConfigRepository activityConfigRepository) {
        super(activityConfigRepository);
        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public JsonSchema getJsonSchema() {
         return MongoSettings.builder().build().getJsonSchema();
    }

    MongoRepository getRepository(String connectionName){
        if(this.getActivityConfigRepository() == null){
            log.error("Activity Config Repository Not Found");
        }

        MongoConnect  connection = this.getExternalConfigByName(connectionName, MongoConnect.class);

        if(connection == null){
            log.error("No connection found for name {}", connectionName);
            throw new AppException("connection " + connectionName + " not found");
        }

        MongoRepository repo = this.repos.get(connection);
        if(repo != null){
            return repo;
        }

        if(connection.getUri() != null){
            repo = new MongoRepository(new MongoConnection(connection.getUri()));
        }else{
            String strPort = connection.getPort();
            String server = connection.getServer();
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
