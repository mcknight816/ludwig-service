package com.bluntsoftware.ludwig.conduit.activities.conduit;


import com.bluntsoftware.ludwig.conduit.impl.ActivityImpl;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import com.bluntsoftware.ludwig.repository.FlowConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;
//TODO fix repository
@Service
public class CounterActivity extends ActivityImpl {
    public CounterActivity(FlowConfigRepository flowConfigRepository) {
        super(flowConfigRepository);
    }
    //@Autowired
    //MongoRepository mongoRepository;

    @Override
    public String getIcon() {
        return "fa-plus";
    }

    @Override
    @Transactional
    public Map<String, Object> run(Map<String, Object> input) throws Exception {
        String name = input.get("name").toString();
        Integer start = Integer.parseInt(input.get("start").toString());
        Integer increment = Integer.parseInt(input.get("increment").toString());
       // MongoCollection<Document> collection = mongoRepository.getCreateCollection(input.get("db").toString(),"counter");
        Map<String, Object> record = new HashMap<>();//collection.find(eq("_id", name)).first();
        if(record == null){
            record = new HashMap<>();
            record.put("_id",name);
            record.put("seq",start);
        }
        Integer seq = Integer.parseInt( record.get("seq").toString());
        seq += increment;
        record.put("seq",seq);
        //return mongoRepository.save(input.get("db").toString(),"counter",record,true);
        return null;
    }

    @Override
    public JsonSchema getSchema() {
        JsonSchema schema = new JsonSchema("Counter");
        schema.addString("name","counter","");
        schema.addString("increment","1","");
        schema.addString("start","1573","");
        schema.addString("db","ludwig","");
        return schema;
    }
}
