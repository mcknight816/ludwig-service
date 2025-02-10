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
import com.bluntsoftware.ludwig.domain.KnowledgeChunk;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import com.bluntsoftware.ludwig.repository.impl.KnowledgeChunkCustomRepositoryImpl;
import com.bluntsoftware.ludwig.tenant.TenantResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OpenAITextActivity extends ActivityImpl {
    private final KnowledgeChunkCustomRepositoryImpl knowledgeChunkCustomRepository;
    public OpenAITextActivity(ActivityConfigRepository activityConfigRepository, KnowledgeChunkCustomRepositoryImpl knowledgeChunkCustomRepository) {
        super(activityConfigRepository);
        this.knowledgeChunkCustomRepository = knowledgeChunkCustomRepository;
    }

    @Override
    public Map<String, Object> getOutput() {
        return AITextResponse.builder().build().getJsonSchema().getValue();
    }

    @Override
    public Map<String, Object> run(Map<String, Object> input) throws Exception {
        log.info("AI Text Activity input: {}", input);
        String textOut = "No Ai Response";
        AITextRequest aiText = convertValue(input, AITextRequest.class);
        OpenAiConfig openAiConfig  = getExternalConfigByName(aiText.getConfig(), OpenAiConfig.class);

        if(openAiConfig != null && openAiConfig.getSecret() != null){
            AIService aiService = new AIService( openAiConfig.getSecret() );
            log.info("Running open AI Text Activity Current Tenant is {}" , TenantResolver.resolve());

            List<AIMessage> messages = new ArrayList<>();



            if(aiText.getKnowledgeBase() != null && !aiText.getKnowledgeBase().isEmpty()){
                List<Double> queryVector =  aiService.getEmbedding(aiText.getText());
                List<KnowledgeChunk> knowledgeChunks =  knowledgeChunkCustomRepository.findSimilarChunks(queryVector,50)
                        .collectList()
                        .block();
                StringBuilder combinedText = new StringBuilder();
                knowledgeChunks.forEach(kc -> {
                    String text = kc.getText();
                    if (text != null) {
                        combinedText.append(text).append(System.lineSeparator());
                    }
                });

                AIMessage aiMessage = AIMessage.builder()
                        .role("system")
                        .content(combinedText.toString())
                        .build();
                messages.add(aiMessage);
            }

            AIMessage userMessage = AIMessage.builder()
                    .role("user")
                    .content(aiText.getText())
                    .build();
            messages.add(userMessage);

            AICompletionRequest request =  AICompletionRequest.builder()
                    .messages(messages)
                    .max_tokens(openAiConfig.getMax_tokens())
                    .store(openAiConfig.isStore())
                    .temperature(openAiConfig.getTemperature())
                    .model(openAiConfig.getModel())
                    .build();

            AICompletionResponse response = aiService.completions(request);

            if(response != null && response.getChoices() != null && !response.getChoices().isEmpty()){
                textOut = response.getChoices().get(0).getMessage().getContent();
            }

        }
        //Default Values
        return AITextResponse.builder()
                .text(textOut)
                .build().getJsonSchema().getValue();
    }

    @Override
    public JsonSchema getJsonSchema() {
        return AITextRequest.builder().build().getJsonSchema();
    }
}
