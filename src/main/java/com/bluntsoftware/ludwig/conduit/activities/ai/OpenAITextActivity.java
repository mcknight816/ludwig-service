package com.bluntsoftware.ludwig.conduit.activities.ai;

import com.bluntsoftware.ludwig.conduit.activities.ActivityImpl;
import com.bluntsoftware.ludwig.conduit.activities.ai.domain.AITextRequest;
import com.bluntsoftware.ludwig.conduit.activities.ai.domain.AITextResponse;
import com.bluntsoftware.ludwig.conduit.config.ai.domain.OpenAiConfig;
import com.bluntsoftware.ludwig.conduit.service.ai.AIService;
import com.bluntsoftware.ludwig.conduit.service.ai.domain.AICompletionRequest;
import com.bluntsoftware.ludwig.conduit.service.ai.domain.AICompletionResponse;
import com.bluntsoftware.ludwig.conduit.service.ai.domain.AIMessage;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Map;

@Slf4j
@Service
public class OpenAITextActivity extends ActivityImpl {

    public OpenAITextActivity(ActivityConfigRepository activityConfigRepository) {
        super(activityConfigRepository);
    }

    @Override
    public Map<String, Object> run(Map<String, Object> input) throws Exception {

        AITextRequest aiText = convertValue(input, AITextRequest.class);
        OpenAiConfig openAiConfig  = getExternalConfigByName(aiText.getConfig(), OpenAiConfig.class);
        if(openAiConfig != null && openAiConfig.getSecret() != null){
            AIService aiService = new AIService(openAiConfig.getSecret());
            AICompletionResponse response = aiService.completions(AICompletionRequest.builder()
                    .temperature(openAiConfig.getTemperature())
                    .store(openAiConfig.isStore())
                    .model(openAiConfig.getModel())
                    .max_tokens(openAiConfig.getMax_tokens())
                    .message(AIMessage.builder()
                            .role("User")
                            .content(aiText.getText())
                            .build())
                    .build());
            log.info("AI Text Activity input: {}", input);
        }
        //Default Values
        return AITextResponse.builder().build().getJsonSchema().getValue();
    }

    @Override
    public JsonSchema getJsonSchema() {
        return AITextRequest.builder().build().getJsonSchema();
    }
}
