package com.bluntsoftware.ludwig.conduit.activities.mongo;


import com.bluntsoftware.ludwig.conduit.activities.mongo.domain.*;
import com.bluntsoftware.ludwig.conduit.service.nosql.NoSqlResult;
import com.bluntsoftware.ludwig.conduit.service.nosql.mongo.MongoRepository;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.utils.SecurityUtils;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * MongoFindActivity is a concrete implementation of the MongoActivity class.
 * This class serves as an activity to perform find operations on a MongoDB collection.
 * It handles query parameters, security checks, and MongoDB connection interactions to fetch data
 * based on the input configuration.
 *
 * Key features include:
 * - Parsing input parameters for MongoDB queries.
 * - Validating and handling user-managed access and security constraints.
 * - Fetching data from the specified MongoDB collection using the MongoRepository.
 * - Generating results in a structured format.
 *
 * Constructor:
 * - Requires an ActivityConfigRepository to initialize the activity.
 *
 * Methods:
 * - run(Map<String, Object> input): Executes the find activity based on the provided input map.
 *   Retrieves data from the specified MongoDB collection and returns the results as a map.
 * - getJsonSchema(): Provides the JSON schema definition for the MongoFind activity.
 */
@Service
public class MongoFindActivity extends MongoActivity {


    public MongoFindActivity( ActivityConfigRepository activityConfigRepository) {
        super(activityConfigRepository);
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
          //  boolean allowFriends = mongoSettings.getAllowFriends() != null && mongoSettings.getAllowFriends().equalsIgnoreCase("true");
            if (userManaged) {
                String login = "anonymous";
                try{
                    if(filterByFields == null || filterByFields.equalsIgnoreCase("")){
                        filterByFields = "{}";
                    }
                    BasicDBObject q = BasicDBObject.parse(filterByFields);
                    login = SecurityUtils.getCurrentLogin();
                   // if(allowFriends){
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
                   // }else{
                        q.put("user_info.login",login);
                    //}
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
    public JsonSchema getJsonSchema() {
        return MongoFind.builder().build().getJsonSchema();
    }


}
