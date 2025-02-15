package com.bluntsoftware.ludwig.conduit.config.ai;

import com.bluntsoftware.ludwig.conduit.config.ActivityConfigImpl;
import com.bluntsoftware.ludwig.conduit.config.ConfigTestResult;
import com.bluntsoftware.ludwig.conduit.config.ai.domain.OpenAiConfig;
import com.bluntsoftware.ludwig.conduit.service.ai.AIService;
import com.bluntsoftware.ludwig.conduit.service.ai.domain.AICompletionRequest;
import com.bluntsoftware.ludwig.conduit.service.ai.domain.AICompletionResponse;
import com.bluntsoftware.ludwig.conduit.service.ai.domain.AIMessage;
import org.springframework.stereotype.Service;

/**
 * A service class responsible for handling configuration activities related to OpenAI.
 * It extends the generic ActivityConfigImpl class with a specific type of OpenAiConfig.
 * This class provides functionality for testing the provided OpenAi configuration details.
 */
@Service
public class OpenAiConfigActivity extends ActivityConfigImpl<OpenAiConfig> {

    /**
     * Tests the provided OpenAiConfig by sending a request to the AI service
     * and validating the response.
     *
     * @param config The OpenAiConfig containing the necessary configuration details
     *               such as API secret, test question, model, temperature, and other parameters.
     * @return A ConfigTestResult indicating the success or failure of the test,
     *         along with any relevant messages or hints.
     */
    @Override
    public ConfigTestResult testConfig(OpenAiConfig config) {
        if(config != null && config.getTestQuestion() != null) {
            AIService aiService = new AIService(config.getSecret());
            try {
                AICompletionResponse response = aiService.completions(AICompletionRequest.builder()
                        .message(AIMessage.builder()
                                .role("user")
                                .content(config.getTestQuestion())
                                .build())
                        .max_tokens(config.getMax_tokens())
                        .store(config.isStore())
                        .temperature(config.getTemperature())
                        .model(config.getModel())
                        .build());

                return ConfigTestResult.builder()
                        .success(true)
                        .message("AI completed successfully")
                        .hint(response.toString()).build();
            } catch (RuntimeException e){
                return  ConfigTestResult.builder()
                        .error(true)
                        .message("AI Failed")
                        .hint(e.getMessage()).build();
            }
        } else {
            return ConfigTestResult.builder()
                    .error(true)
                    .message("AI Failed")
                    .hint("Make sure Ai Api Secret is correct and the test question is not null").build();
        }
    }
}
