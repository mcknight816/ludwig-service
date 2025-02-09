package com.bluntsoftware.ludwig.conduit.service.ai;

import com.bluntsoftware.ludwig.conduit.service.ai.domain.*;
import com.bluntsoftware.ludwig.config.AppConfig;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AIService {

    private final AppConfig appConfig;
    private static final String API_COMPLETIONS_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_IMAGES_URL = "https://api.openai.com/v1/images/generations";
    private static final String API_EMBEDDING_URL = "https://api.openai.com/v1/embeddings";

    public AIService(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public AIService(String openAIApiSecret) {
        this.appConfig = new AppConfig();
        appConfig.setOpenAIApiSecret(openAIApiSecret);
    }

    public AIService() {
        this.appConfig = new AppConfig();
    }

    public AICompletionResponse completions(AICompletionRequest completion) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + appConfig.getOpenAIApiSecret());
        HttpEntity<AICompletionRequest> request = new HttpEntity<>(completion, headers);
        ResponseEntity<AICompletionResponse> response = restTemplate.postForEntity(API_COMPLETIONS_URL, request, AICompletionResponse.class);
        return response.getBody();
    }

    public AIImageResponse images(AIImageRequest imageRequest){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + appConfig.getOpenAIApiSecret());
        HttpEntity<AIImageRequest> request = new HttpEntity<>(imageRequest, headers);
        ResponseEntity<AIImageResponse> response = restTemplate.postForEntity(API_IMAGES_URL, request, AIImageResponse.class);
        return response.getBody();
    }

    public List<Double> getEmbedding(String text) throws IOException {
        // Construct request
        AIEmbeddingRequest embeddingRequest = AIEmbeddingRequest.builder()
                .input( text)
                .build();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + appConfig.getOpenAIApiSecret());
        HttpEntity<AIEmbeddingRequest> request = new HttpEntity<>(embeddingRequest, headers);
        ResponseEntity<AIEmbeddingResponse> response = restTemplate.postForEntity(API_EMBEDDING_URL, request, AIEmbeddingResponse.class);
        AIEmbeddingResponse res = response.getBody();
        if(res == null || res.getData() == null || res.getData().isEmpty()){
            return new ArrayList<>();
        }
        return res.getData().get(0).getEmbedding();
    }

}
