package com.bluntsoftware.ludwig.conduit.activities.couch;


import com.bluntsoftware.ludwig.conduit.config.nosql.CouchbaseConnectionConfig;
import com.bluntsoftware.ludwig.conduit.nosql.couch.CouchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex Mcknight on 8/1/2017.
 */
@Service
public class CouchColumnsActivity extends CouchActivity {

    @Autowired
    public CouchColumnsActivity(CouchbaseConnectionConfig couchConnectionConfig) {
        super(couchConnectionConfig);
    }

    @Override
    public Map<String, Object> run(Map<String, Object> input)throws Exception {
        Map<String, Object> ret = new HashMap<>();
        validateInput(input);
        CouchRepository mongoRepository = getRepository(input);
        String databaseName =  input.get("database").toString();
        String collectionName = input.get("collection").toString();
        List<String> columns = mongoRepository.columns(databaseName,collectionName);
        ret.put("columns",columns);
        return ret;
    }

}