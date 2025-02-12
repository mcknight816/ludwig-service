package com.bluntsoftware.ludwig.conduit.activities.ai;

import com.bluntsoftware.ludwig.conduit.activities.TypedActivity;
import com.bluntsoftware.ludwig.conduit.activities.ai.domain.AITextRequest;
import com.bluntsoftware.ludwig.conduit.activities.ai.domain.AITextResponse;
import com.bluntsoftware.ludwig.conduit.config.ai.domain.OpenAiConfig;
import com.bluntsoftware.ludwig.conduit.service.ai.AIService;
import com.bluntsoftware.ludwig.conduit.service.ai.domain.AICompletionRequest;
import com.bluntsoftware.ludwig.conduit.service.ai.domain.AICompletionResponse;
import com.bluntsoftware.ludwig.conduit.service.ai.domain.AIMessage;
import com.bluntsoftware.ludwig.domain.KnowledgeChunk;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import com.bluntsoftware.ludwig.repository.impl.KnowledgeChunkCustomRepositoryImpl;
import com.bluntsoftware.ludwig.tenant.TenantResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OpenAITextActivity extends TypedActivity<AITextRequest, AITextResponse> {

    private final KnowledgeChunkCustomRepositoryImpl knowledgeChunkCustomRepository;

    public OpenAITextActivity(ActivityConfigRepository activityConfigRepository, KnowledgeChunkCustomRepositoryImpl knowledgeChunkCustomRepository) {
        super(activityConfigRepository,AITextRequest.class);
        this.knowledgeChunkCustomRepository = knowledgeChunkCustomRepository;
    }

    @Override
    public AITextRequest input() {
        return AITextRequest.builder().build();
    }

    @Override
    public AITextResponse output() {
        return AITextResponse.builder().build();
    }

    @Override
    public AITextResponse run(AITextRequest aiText) throws Exception {
        OpenAiConfig openAiConfig  = getExternalConfigByName(aiText.getConfig(), OpenAiConfig.class);
        String textOut = "No Ai Response";
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
                if (knowledgeChunks != null) {
                    knowledgeChunks.forEach(kc -> {
                        String text = kc.getText();
                        if (text != null) {
                            combinedText.append(text).append(System.lineSeparator());
                        }
                    });
                }

                AIMessage aiMessage = AIMessage.builder()
                        .role("system")
                        .content(combinedText.toString())
                        .build();
                messages.add(aiMessage);
            }

            AIMessage sysMessage = AIMessage.builder()
                    .role("system")
                    .content(aiText.getInstructions())
                    .build();

            messages.add(sysMessage);

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
                .build();
    }
}
