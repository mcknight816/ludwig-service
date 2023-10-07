package com.bluntsoftware.ludwig.ai;


import com.bluntsoftware.ludwig.ai.domain.AICompletion;
import com.bluntsoftware.ludwig.ai.domain.AICompletionResponse;
import com.bluntsoftware.ludwig.ai.domain.AIImage;
import com.bluntsoftware.ludwig.ai.domain.AIImageResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class AIService {

    private static final String API_KEY = "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";
    private static final String API_COMPLETIONS_URL = "https://api.openai.com/v1/completions";
    private static final String API_IMAGES_URL = "https://api.openai.com/v1/images/generations";
    public static AICompletionResponse completions(AICompletion completion) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + API_KEY);
        HttpEntity<AICompletion> request = new HttpEntity<>(completion, headers);
        ResponseEntity<AICompletionResponse> response = restTemplate.postForEntity(API_COMPLETIONS_URL, request, AICompletionResponse.class);
        return response.getBody();
    }

    public static AIImageResponse images(AIImage imageRequest){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + API_KEY);
        HttpEntity<AIImage> request = new HttpEntity<>(imageRequest, headers);
        ResponseEntity<AIImageResponse> response = restTemplate.postForEntity(API_IMAGES_URL, request, AIImageResponse.class);
        return response.getBody();
    }

    public static void main(String[] args) {
        AICompletionResponse completionResponse = completions(new AICompletion()
                .toBuilder()
                .prompt("build a complex json model with lowercase fields for a Business Plan")
                .build());
        System.out.println(completionResponse);


        AIImageResponse imageResponse = images(new AIImage()
                .toBuilder()
                .prompt("A t-shirt printing company")
                .size("256x256")
                .build());
        System.out.println(imageResponse);

    }

}
