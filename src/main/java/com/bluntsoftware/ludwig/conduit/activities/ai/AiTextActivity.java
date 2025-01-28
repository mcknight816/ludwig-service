package com.bluntsoftware.ludwig.conduit.activities.ai;

import com.bluntsoftware.ludwig.conduit.activities.ActivityImpl;
import com.bluntsoftware.ludwig.conduit.activities.ai.domain.AIText;
import com.bluntsoftware.ludwig.conduit.config.ai.OpenAiConfigActivity;
import com.bluntsoftware.ludwig.conduit.config.ai.domain.OpenAiConfig;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
@Slf4j
@Service
public class AiTextActivity extends ActivityImpl {

    public AiTextActivity(ActivityConfigRepository activityConfigRepository) {
        super(activityConfigRepository);
    }

    @Override
    public Map<String, Object> run(Map<String, Object> input) throws Exception {

        AIText aiText = convertValue(input, AIText.class);
        OpenAiConfig openAiConfig  = getExternalConfigByName(aiText.getConfig(), OpenAiConfig.class);

        log.info("AI Text Activity input: {}", input);
        return Map.of();
    }

    @Override
    public JsonSchema getJsonSchema() {
        return AIText.builder().build().getJsonSchema();
    }
}
