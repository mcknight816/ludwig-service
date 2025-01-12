package com.bluntsoftware.ludwig.ai;


import com.bluntsoftware.ludwig.ai.domain.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class AIService {

    private static final String API_KEY = "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";
    private static final String API_COMPLETIONS_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_IMAGES_URL = "https://api.openai.com/v1/images/generations";
    public static AICompletionResponse completions(AICompletionRequest completion) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + API_KEY);
        HttpEntity<AICompletionRequest> request = new HttpEntity<>(completion, headers);
        ResponseEntity<AICompletionResponse> response = restTemplate.postForEntity(API_COMPLETIONS_URL, request, AICompletionResponse.class);
        return response.getBody();
    }

    public static AIImageResponse images(AIImageRequest imageRequest){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + API_KEY);
        HttpEntity<AIImageRequest> request = new HttpEntity<>(imageRequest, headers);
        ResponseEntity<AIImageResponse> response = restTemplate.postForEntity(API_IMAGES_URL, request, AIImageResponse.class);
        return response.getBody();
    }

    public static void main(String[] args) {
      AICompletionResponse completionResponse = completions( AICompletionRequest.builder()
                .message(AIMessage.builder()
                        .role("SYSTEM").content("you always respond in a valid json string for all of the content")
                        .role("user").content("build a complex json model with lowercase fields for a Business Plan")
                        .build())
                .model(OpenAiModel.GPT_4_MINI.toString())
                .max_tokens(2048)
               // .prompt("build a complex json model with lowercase fields for a Business Plan")
                .build());
        System.out.println(completionResponse.getChoices().get(0).getMessage().getContent());

/*
        AIImageResponse imageResponse = images(AIImageRequest.builder()
                .n(1)
                .prompt("A t-shirt printing company")
                .size("256x256")
                .build());
        System.out.println(imageResponse); */

    }

}
