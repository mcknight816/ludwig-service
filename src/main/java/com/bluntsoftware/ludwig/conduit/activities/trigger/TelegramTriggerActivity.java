package com.bluntsoftware.ludwig.conduit.activities.trigger;

import com.bluntsoftware.ludwig.conduit.activities.ActivityImpl;
import com.bluntsoftware.ludwig.conduit.activities.trigger.domain.TelegramTrigger;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.Map;

/**
 * TelegramTriggerActivity is a concrete implementation of the Activity interface
 * that handles Telegram-related trigger activities. It extends the functionality
 * of ActivityImpl and provides specific behavior for processing input, schema
 * definitions, and visual representation.
 *
 * This class is designed to work within the context of a modular activity-based
 * system, leveraging dependency injection and configuration management through
 * the ActivityConfigRepository.
 */
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
