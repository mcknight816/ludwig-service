package com.bluntsoftware.ludwig.service;

import com.bluntsoftware.ludwig.domain.AiEmbedding;
import com.bluntsoftware.ludwig.dto.ChatResponseDto;
import com.bluntsoftware.ludwig.repository.AiEmbeddingRepository;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;
import java.util.Map;
/*
    The `AiConversationService` provides an abstraction for managing AI-powered conversations. It makes the process user-friendly,
    efficient, and contextually relevant by bridging the gap between user input and AI capabilities while optionally maintaining
    session histories and interaction data. It acts as the backbone for powering intelligent chat-driven workflows.
 */
@Service
public class AiConversationService {
private final UserService userService;
    private final OpenAiService openAiService;
    private final AiEmbeddingRepository aiEmbeddingRepository;
    private final static String CATEGORY = "UserConversations";

    public AiConversationService(UserService userService, OpenAiService openAiService, AiEmbeddingRepository aiEmbeddingRepository) {
        this.userService = userService;
        this.openAiService = openAiService;
        this.aiEmbeddingRepository = aiEmbeddingRepository;
    }

    public ChatResponseDto processQuery(String sessionId, String userMessage) throws IOException {

        Map<String,Object> userDetails = userService.getAuthenticatedUserDetails();

        String userId = userDetails.containsKey("email") ? (String)userDetails.get("email") : (String)userDetails.get("sub");
        // Generate embedding for the user's current message
        List<Double> userMessageEmbedding = openAiService.getEmbeddings(userMessage);

        // Get previous conversation history
        List<AiEmbedding> history = aiEmbeddingRepository.getAiEmbeddingByUserIdAndCategory(userId,CATEGORY).collectList().block();

        // Retrieve the most relevant conversation history using embeddings
        List<String> relevantHistory = openAiService.getRelevantHistory(userMessageEmbedding, history, 25);

        // Construct context using the relevant history
        String context = String.join("\n", relevantHistory); // Combine relevant messages
        context += "\nUser: " + userMessage; // Include current user input

        // Generate AI response based on the constructed context
        String aiResponse = openAiService.callOpenAi(context);

        // Save the current user message and AI response (with embeddings) into the mongo aiEmbeddingRepository
        aiEmbeddingRepository.save(AiEmbedding.builder().vector(userMessageEmbedding).userId(userId).text(userMessage).category(CATEGORY).processed(true).build()).block();
        aiEmbeddingRepository.save(AiEmbedding.builder().vector(openAiService.getEmbeddings(aiResponse)).userId(userId).text(aiResponse).category(CATEGORY).processed(true).build()).block();

        // Return AI's response wrapped in a DTO
        return new ChatResponseDto(aiResponse);
    }

}
