package com.bluntsoftware.ludwig.conduit.activities.couch;

import com.bluntsoftware.ludwig.conduit.config.nosql.CouchbaseConnectionConfig;
import com.bluntsoftware.ludwig.conduit.activities.ActivityImpl;
import com.bluntsoftware.ludwig.conduit.config.nosql.domain.CouchbaseConnection;
import com.bluntsoftware.ludwig.conduit.service.nosql.couch.CouchConnection;
import com.bluntsoftware.ludwig.conduit.service.nosql.couch.CouchRepository;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import com.couchbase.client.java.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by Alex Mcknight on 2/14/2017.
 *
 */
public abstract class CouchActivity extends ActivityImpl {

    private final CouchbaseConnectionConfig couchConnectionConfig;

    private final Map<CouchbaseConnection, CouchRepository> repos = new HashMap<>();

    public CouchActivity(CouchbaseConnectionConfig couchConnectionConfig, ActivityConfigRepository activityConfigRepository ) {
        super(activityConfigRepository);
        this.couchConnectionConfig = couchConnectionConfig;
    }

    @Override
    public JsonSchema getJsonSchema() {
        JsonSchema schema = JsonSchema.builder().title("Couch Properties").build();
        schema.addString("database","test",null);
        schema.addString("collection","",null);
        List<String> truefalse = new ArrayList<String>();
        truefalse.add("true");
        truefalse.add("false");
        schema.addEnum("User Managed","userManaged",truefalse,"false");
        schema.addEnum("Allow Friends","allowFriends",truefalse,"false");
        schema.addConfig(couchConnectionConfig);
        return schema;
    }

    CouchRepository getRepository(Map<String, Object> input){
        CouchbaseConnection  connection = this.getExternalConfigByName(input.get(couchConnectionConfig.getPropertyName()), CouchbaseConnection.class);

        CouchRepository repo = this.repos.get(connection);
        if(repo != null){
            return repo;
        }

        String strPort = connection.getPort();
        String server = connection.getServer();
        String userName = connection.getUserName();
        String password = connection.getPassword();
        if(strPort != null && !strPort.equalsIgnoreCase("")) {
            int port = Integer.parseInt(strPort);
            repo = new CouchRepository(new CouchConnection(server,port,userName,password));
        }else{
            repo = new CouchRepository(new CouchConnection(server,null));
        }
        this.repos.put(connection,repo);
        return repo;
    }

    public static void main(String[] args) {

    }

    @Override
    public String getIcon() {
        return "fa-pause";
    }

    Collection getCollection(){
          Map<String,Object> in = getInput();
        CouchRepository repository = getRepository(in);
          if(repository != null){
              Object dbObj = in.get("database");
              Object collectionObj = in.get("collection");
              if(dbObj != null && collectionObj != null){
                  return repository.getCreateCollection(dbObj.toString(),collectionObj.toString());
              }
          }
          return null;
    }

    void validateInput(Map<String, Object> in) throws Exception{

       CouchRepository repository = getRepository(in);
        if(repository != null){
            Object dbObj = in.get("database");
            Object collectionObj = in.get("collection");
            if(dbObj== null || dbObj.toString().equalsIgnoreCase("")){
                throw new Exception("Invalid Database Name");
            }
            /*if(collectionObj == null || collectionObj.toString().equalsIgnoreCase("")){
                throw new Exception("Invalid Collection Name");
            }*/
        }else{
             throw new Exception("Invalid Repository Connection Settings");
        }
    }

}
