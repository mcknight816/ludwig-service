package com.bluntsoftware.ludwig.conduit.activities.trigger;

import com.bluntsoftware.ludwig.conduit.activities.ActivityImpl;
import com.bluntsoftware.ludwig.conduit.activities.trigger.domain.TelegramTrigger;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.Map;

@Slf4j
@Service
public class TelegramTriggerActivity extends ActivityImpl {

    public TelegramTriggerActivity(ActivityConfigRepository activityConfigRepository) {
        super(activityConfigRepository);
    }

    @Override
    public Map<String, Object> run(Map<String, Object> input) throws Exception {
        return input;
    }

    @Override
    public JsonSchema getJsonSchema() {
        return TelegramTrigger.builder().build().getJsonSchema();
    }

    @Override
    public String getIcon() {
        return "fa-telegram";
    }
}
