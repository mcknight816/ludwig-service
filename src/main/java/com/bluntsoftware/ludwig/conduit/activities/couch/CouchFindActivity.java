package com.bluntsoftware.ludwig.conduit.activities.couch;


import com.bluntsoftware.ludwig.conduit.config.nosql.CouchbaseConnectionConfig;
import com.bluntsoftware.ludwig.conduit.service.nosql.NoSqlResult;
import com.bluntsoftware.ludwig.conduit.service.nosql.couch.CouchRepository;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.PropertyFormat;
import com.bluntsoftware.ludwig.conduit.utils.SecurityUtils;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex Mcknight on 2/27/2017.
 */
@Service
public class CouchFindActivity extends CouchActivity {

    @Autowired
    public CouchFindActivity(CouchbaseConnectionConfig couchConnectionConfig, ActivityConfigRepository activityConfigRepository) {
        super(couchConnectionConfig,activityConfigRepository);
    }

    @Override
    public Map<String,Object> run(Map<String, Object> input) throws Exception {
        validateInput(input);
        CouchRepository mongoRepository = getRepository(input);
        String databaseName =  input.get("database").toString();
        String collectionName = input.get("collection").toString();

        Map<String,Object> query =((Map<String,Object>)input.get("query"));
        String filterByFields = "{}";
        Object qry =  query.get("filter");
        if(qry != null){
            filterByFields = qry.toString();
        }

        if(!SecurityUtils.isAdmin() && input.containsKey("userManaged") && input.get("userManaged") != null) {
            Boolean userManaged = input.get("userManaged").toString().equalsIgnoreCase("true");
            Boolean allowFriends = input.containsKey("allowFriends") && input.get("allowFriends").toString().equalsIgnoreCase("true");
            if (userManaged) {
                String login = "anonymous";
                try{
                    if(filterByFields == null || filterByFields.equalsIgnoreCase("")){
                        filterByFields = "{}";
                    }
                    BasicDBObject q = BasicDBObject.parse(filterByFields);
                    login = SecurityUtils.getCurrentLogin();
                    if(allowFriends){
                        /*
                        User user = accountService.getByLogin(login);
                        List<Friend> friends = user.getFriends();
                        BasicDBList or = new BasicDBList();
                        DBObject clause = new BasicDBObject("user_info.login",login);
                        or.add(clause);
                        for(Friend friend:friends){
                            if(friend.getState() == Friend.STATE.FRIEND){
                                clause = new BasicDBObject("user_info.login", friend.getLogin());
                                or.add(clause);
                            }
                        }
                        q.put("$or", or);
                        */
                        q.put("user_info.login",login);
                    }else{
                        q.put("user_info.login",login);
                    }
                    filterByFields = q.toJson();
                }catch(Exception e){

                }
            }
        }

        String rows = "20";
        if(query.containsKey("rows") && query.get("rows") != null){
            rows = query.get("rows").toString();
        }

        String page = "1";
        if(query.containsKey("page") && query.get("page") != null){
            page = query.get("page").toString();
        }
        String sidx = null;
        if(query.containsKey("sidx") && query.get("sidx") != null){
            sidx = query.get("sidx").toString();
        }
        String sord = null;
        if(query.containsKey("sord") && query.get("sord") != null){
            sord = query.get("sord").toString();
        }
        NoSqlResult ret = mongoRepository.findAll( databaseName,collectionName,filterByFields,null,sidx,sord,rows,page );
        ObjectMapper mapper = new ObjectMapper();

        return  (Map<String,Object>) mapper.convertValue(ret,Map.class);
    }

    @Override
    public JsonSchema getSchema() {
        JsonSchema schema =  super.getSchema();
        List<String> sord = new ArrayList<>();
        sord.add("ASC");
        sord.add("DESC");

        JsonSchema query = new JsonSchema("Query");
        query.addString("page","1");
        query.addString("rows","20",null);
        query.addEnum("Sort Order","sord",sord,"ASC");
        query.addString("Sort Index","sidx","_id",null,false);
        query.addString("filter","", PropertyFormat.JSON);
        schema.addRecord("query",query);
        return schema;
    }

}
