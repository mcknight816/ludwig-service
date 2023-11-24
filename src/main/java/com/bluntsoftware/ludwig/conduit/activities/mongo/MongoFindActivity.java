package com.bluntsoftware.ludwig.conduit.activities.mongo;


import com.bluntsoftware.ludwig.conduit.activities.mongo.domain.*;
import com.bluntsoftware.ludwig.conduit.config.nosql.MongoConnectionConfig;
import com.bluntsoftware.ludwig.conduit.nosql.NoSqlResult;
import com.bluntsoftware.ludwig.conduit.nosql.mongo.MongoRepository;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.utils.SecurityUtils;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by Alex Mcknight on 2/27/2017.
 */
@Service
public class MongoFindActivity extends MongoActivity {

    @Autowired
    public MongoFindActivity(MongoConnectionConfig mongoConnectionConfig, ActivityConfigRepository activityConfigRepository) {
        super(mongoConnectionConfig,activityConfigRepository);
    }

    @Override
    public Map<String,Object> run(Map<String, Object> input) throws Exception {
        //validateInput(input);
        MongoFind mongoFind = convertValue(input,MongoFind.class);
        MongoSettings mongoSettings = mongoFind.getSettings();
        MongoRepository mongoRepository = getRepository(mongoSettings.getConnection());
        DBQuery query = mongoFind.getQuery();
        String filterByFields = query.getFilter();

        if(!SecurityUtils.isAdmin() && mongoSettings.getUserManaged() != null) {
            boolean userManaged = mongoSettings.getUserManaged().equalsIgnoreCase("true");
            boolean allowFriends = mongoSettings.getAllowFriends() != null && mongoSettings.getAllowFriends().equalsIgnoreCase("true");
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

        String rows = query.getRows() != null ? query.getRows() : "20";

        String page = query.getPage() != null ? query.getPage() : "1";

        String sidx = query.getSidx() != null ? query.getSidx() : null;

        String sord = query.getSord() != null ? query.getSord() : null;


        NoSqlResult ret = mongoRepository.findAll( mongoSettings.getDatabase(),mongoSettings.getCollection(),filterByFields,null,sidx,sord,rows,page );
        ObjectMapper mapper = new ObjectMapper();

        return  (Map<String, Object>) mapper.convertValue(ret,Map.class);

    }

    @Override
    public JsonSchema getSchema() {
        return MongoFind.getSchema();
    }


}
