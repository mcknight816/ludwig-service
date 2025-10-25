package com.bluntsoftware.ludwig.controller;

import com.bluntsoftware.ludwig.conduit.service.ai.AIService;
import com.bluntsoftware.ludwig.dto.ChatResponseDto;
import com.bluntsoftware.ludwig.service.AiConversationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/core/application")
public class AiLudwigController {

    private final AiConversationService conversationService;

    public AiLudwigController(AiConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @PostMapping("/chat/{sessionId}")
    public ResponseEntity<ChatResponseDto> chatWithAi(@PathVariable String sessionId, @RequestBody Map<String, String> request) {
        try {
            String userMessage = request.get("message");

            String[] apps = Optional.ofNullable(request.get("apps"))
                    .filter(s -> !s.isBlank())
                    .map(s -> s.split("\\s*,\\s*"))
                    .orElse(null);

            String[] knowledgeBases = Optional.ofNullable(request.get("knowledgeBases"))
                    .filter(s -> !s.isBlank())
                    .map(s -> s.split("\\s*,\\s*"))
                    .orElse(new String[0]);

            ChatResponseDto aiResponse = conversationService.processQuery(sessionId, userMessage,apps,knowledgeBases);
            return ResponseEntity.ok(aiResponse);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(ChatResponseDto.builder()
                            .response("❌ Error processing AI request: " + e.getMessage())
                    .build());
        }
    }

    @PostMapping("/chat/{sessionId}/update")
    public ResponseEntity<String> updateConversation(@PathVariable String sessionId, @RequestBody Map<String, String> request) {
        //conversationService.updateConversation(sessionId, request.get("key"), request.get("value"));
        return ResponseEntity.ok("✅ Updated conversation context.");
    }
}

