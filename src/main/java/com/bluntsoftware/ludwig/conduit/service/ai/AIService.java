package com.bluntsoftware.ludwig.conduit.service.ai;

import com.bluntsoftware.ludwig.conduit.service.ai.domain.AICompletionRequest;
import com.bluntsoftware.ludwig.conduit.service.ai.domain.AICompletionResponse;
import com.bluntsoftware.ludwig.conduit.service.ai.domain.AIImageRequest;
import com.bluntsoftware.ludwig.conduit.service.ai.domain.AIImageResponse;
import com.bluntsoftware.ludwig.config.AppConfig;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
@Service
public class AIService {

    private final AppConfig appConfig;
    private static final String API_COMPLETIONS_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_IMAGES_URL = "https://api.openai.com/v1/images/generations";

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
}
