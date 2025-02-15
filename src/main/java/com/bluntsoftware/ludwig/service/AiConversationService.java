package com.bluntsoftware.ludwig.service;

import com.bluntsoftware.ludwig.dto.ChatResponseDto;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class AiConversationService {

    private final OpenAiService openAiService;
    private final Map<String, Map<String, String>> userConversations = new HashMap<>();

    public AiConversationService(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    public ChatResponseDto processQuery(String sessionId, String userMessage) throws IOException {
        userConversations.putIfAbsent(sessionId, new HashMap<>());
        Map<String, String> conversationState = userConversations.get(sessionId);

        // If it's the first message, start collecting data
        if (conversationState.isEmpty()) {
            conversationState.put("initialQuery", userMessage);
        }

        // Detect if all required fields are collected
        boolean isComplete = conversationState.containsKey("applicationName") &&
                conversationState.containsKey("flowType") &&
                conversationState.containsKey("flowName") &&
                (!"MONGO CRUD FLOW".equals(conversationState.get("flowType")) ||
                        (conversationState.containsKey("databaseConfig") &&
                                conversationState.containsKey("databaseName") &&
                                conversationState.containsKey("collectionName"))) &&
                conversationState.containsKey("schema");

        if (!isComplete) {
            // Ask for missing details
            return ChatResponseDto.builder().response(openAiService.completeMissingDetails(conversationState)).build();
        } else {
            // All details are collected, proceed to generate application
            return ChatResponseDto.builder().response("✅ All details are provided! Generating the application now.").build();
        }
    }

    public void updateConversation(String sessionId, String key, String value) {
        userConversations.putIfAbsent(sessionId, new HashMap<>());
        userConversations.get(sessionId).put(key, value);
    }
}
