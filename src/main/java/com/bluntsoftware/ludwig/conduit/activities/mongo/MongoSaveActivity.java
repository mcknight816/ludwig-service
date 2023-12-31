package com.bluntsoftware.ludwig.conduit.activities.mongo;


import com.bluntsoftware.ludwig.conduit.Activity;
import com.bluntsoftware.ludwig.conduit.activities.mongo.domain.MongoSave;
import com.bluntsoftware.ludwig.conduit.activities.mongo.domain.MongoSettings;
import com.bluntsoftware.ludwig.conduit.config.nosql.MongoConnectionConfig;
import com.bluntsoftware.ludwig.conduit.impl.ActivityProperties;
import com.bluntsoftware.ludwig.conduit.nosql.mongo.MongoRepository;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.utils.SecurityUtils;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ConcurrentModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex Mcknight on 2/27/2017.
 */
@Slf4j
@Service
public class MongoSaveActivity extends MongoActivity {

    @Autowired
    public MongoSaveActivity(MongoConnectionConfig mongoConnectionConfig, ActivityConfigRepository activityConfigRepository) {
        super(mongoConnectionConfig,activityConfigRepository);
    }

    @Override
    public JsonSchema getSchema() {
        return MongoSave.getSchema();
    }

    public static Activity get(){
        return getByClassName(MongoSaveActivity.class.getName());
    }

    @Override
    public Map<String, Object> run(Map<String, Object> input) throws Exception {
        MongoSave mongoSave = convertValue(input,MongoSave.class);
        MongoSettings mongoSettings = mongoSave.getSettings();
        MongoRepository mongoRepository = getRepository(mongoSettings.getConnection());
        Map<String, Object> payload = mongoSave.getPayload();
        if (payload == null) throw new RuntimeException("null payload for mongo save");
        if (mongoSettings.getUserManaged() != null && mongoSettings.getUserManaged().equalsIgnoreCase("true")) {
            try {
                Map<String, Object> userInfo = SecurityUtils.getUserInfo();
                String login = SecurityUtils.getCurrentLogin();
                StringBuilder roles = new StringBuilder();
                for (String authority : SecurityUtils.getRoles()) {
                    roles.append(authority).append(" ");
                }
                //if admin is editing a users record then keep the original owner (user_info)
                if (payload.get("_id") != null && roles.toString().contains("ROLE_ADMIN")) {
                    Document doc = mongoRepository.getById(payload.get("_id").toString(), mongoSettings.getDatabase(), mongoSettings.getCollection());
                    if (doc != null && doc.containsKey("user_info")) {
                        Map<String, Object> docUserInfo = (Map<String, Object>) doc.get("user_info");
                        if (docUserInfo != null && docUserInfo.containsKey("login")) {
                            String docLogin = docUserInfo.get("login").toString();
                            if (!docLogin.equalsIgnoreCase(login)) {
                                userInfo.putAll(docUserInfo);
                            }
                        }
                    }
                }
                payload.put("user_info", userInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return mongoRepository.save(mongoSettings.getDatabase(), mongoSettings.getCollection(), payload, true);
    }

    @Override
    public Map<String, Object> getOutput() {
        return new HashMap<>();
    }
}
