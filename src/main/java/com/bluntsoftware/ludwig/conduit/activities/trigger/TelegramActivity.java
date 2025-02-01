package com.bluntsoftware.ludwig.conduit.activities.trigger;

import com.bluntsoftware.ludwig.conduit.activities.ActivityImpl;
import com.bluntsoftware.ludwig.conduit.activities.trigger.domain.TelegramRequest;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
public class TelegramActivity extends ActivityImpl {

    public TelegramActivity(ActivityConfigRepository activityConfigRepository) {
        super(activityConfigRepository);
    }

    @Override
    public Map<String, Object> run(Map<String, Object> input) throws Exception {
        return Collections.emptyMap();
    }

    @Override
    public JsonSchema getJsonSchema() {
        return TelegramRequest.builder().build().getJsonSchema();
    }
}
