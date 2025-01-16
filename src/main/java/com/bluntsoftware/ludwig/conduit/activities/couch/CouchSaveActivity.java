package com.bluntsoftware.ludwig.conduit.activities.couch;


import com.bluntsoftware.ludwig.conduit.config.nosql.CouchbaseConnectionConfig;
import com.bluntsoftware.ludwig.conduit.nosql.couch.CouchRepository;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.schema.PropertyFormat;
import com.bluntsoftware.ludwig.conduit.utils.SecurityUtils;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex Mcknight on 2/27/2017.
 */
@Service
public class CouchSaveActivity extends CouchActivity {
    private Logger log = LoggerFactory.getLogger(CouchSaveActivity.class);

    @Autowired
    public CouchSaveActivity(CouchbaseConnectionConfig couchConnectionConfig, ActivityConfigRepository activityConfigRepository) {
        super(couchConnectionConfig,activityConfigRepository);
    }

    @Override
    public JsonSchema getSchema() {
        JsonSchema schema =  super.getSchema();
        schema.addString("payload","{}", PropertyFormat.JSON);
        return schema;
    }

    @Override
    public Map<String, Object> getInput() {
        return getSchema().getValue();
    }

    @Override
    public Map<String, Object> run(Map<String, Object> input)throws Exception {
        validateInput(input);
        CouchRepository mongoRepository = getRepository(input);
        String databaseName =  input.get("database").toString();
        String collectionName = input.get("collection").toString();
        Map<String,Object> msg = new HashMap<>();
        Object payload = input.get("payload");
        log.info("processing");
        if(payload instanceof Map){
            msg = (Map<String,Object>)payload;
        }else if(payload instanceof String){
            try{
                ObjectMapper mapper = new ObjectMapper();
                msg = mapper.readValue(payload.toString(), Map.class);
            }catch (Exception e){
                msg.put("msg",payload);
            }
        }
        if(input.containsKey("userManaged") && input.get("userManaged") != null){
            boolean userManaged = input.get("userManaged").toString().equalsIgnoreCase("true");
            if(userManaged){
                try{
                    Map<String,Object> userInfo = SecurityUtils.getUserInfo();
                    String login = SecurityUtils.getCurrentLogin();
                    StringBuilder roles = new StringBuilder();
                    for(String authority:SecurityUtils.getRoles()){
                        roles.append(authority).append(" ");
                    }
                    //if admin is editing a users record then keep the original owner (user_info)
                    if(msg.get("_id") != null && roles.toString().contains("ROLE_ADMIN")){
                        Document doc  = mongoRepository.getById( msg.get("_id").toString(),databaseName,collectionName);
                        if(doc != null && doc.containsKey("user_info")){
                            Map<String,Object> docUserInfo = (Map<String,Object>)doc.get("user_info");
                            if(docUserInfo != null && docUserInfo.containsKey("login")){
                                String docLogin = docUserInfo.get("login").toString();
                                if(!docLogin.equalsIgnoreCase(login)){
                                    userInfo.putAll(docUserInfo);
                                }
                            }
                        }
                    }
                    msg.put("user_info",userInfo);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return mongoRepository.save( databaseName,collectionName,msg,true);
    }

    @Override
    public Map<String, Object> getOutput() {
        return new HashMap<>();
    }
}
