package com.bluntsoftware.ludwig.conduit.activities.trigger;


import com.bluntsoftware.ludwig.conduit.activities.ActivityImpl;
import com.bluntsoftware.ludwig.conduit.activities.trigger.domain.TimerTrigger;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Alex Mcknight on 1/12/2017.
 */
@Service
public class TimerTriggerActivity extends ActivityImpl {
    public TimerTriggerActivity(ActivityConfigRepository activityConfigRepository) {
        super(activityConfigRepository);
    }

    @Override
    public JsonSchema getJsonSchema() {
        return TimerTrigger.builder().build().getJsonSchema();
    }

    @Override
    public Map<String, Object> run(Map<String, Object> input)throws Exception  {
        Map<String, Object> ret = new HashMap<>();
        ret.put("time",new Date());
        ret.putAll(input);
        return ret;
    }

    @Override
    public String getIcon() {
        return "fa-calendar";
    }

}
