package com.bluntsoftware.ludwig.conduit.activities.mongo;


import com.bluntsoftware.ludwig.conduit.config.nosql.MongoConnectionConfig;
import com.bluntsoftware.ludwig.conduit.nosql.mongo.MongoRepository;
import com.bluntsoftware.ludwig.repository.FlowConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex Mcknight on 8/1/2017.
 */
@Service
public class MongoColumnsActivity extends MongoActivity {

    @Autowired
    public MongoColumnsActivity(MongoConnectionConfig mongoConnectionConfig, FlowConfigRepository flowConfigRepository ) {
        super(mongoConnectionConfig,flowConfigRepository);
    }

    @Override
    public Map<String, Object> run(Map<String, Object> input)throws Exception {
        Map<String, Object> ret = new HashMap<>();
        validateInput(input);
        MongoRepository mongoRepository = getRepository(input);
        String databaseName =  input.get("database").toString();
        String collectionName = input.get("collection").toString();
        List<String> columns = mongoRepository.columns(databaseName,collectionName);
        ret.put("columns",columns);
        return ret;
    }

}
