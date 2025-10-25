package com.bluntsoftware.ludwig.service;


import com.bluntsoftware.ludwig.domain.Knowledge;
import com.bluntsoftware.ludwig.domain.KnowledgeBase;
import com.bluntsoftware.ludwig.dto.ChatResponseDto;
import com.bluntsoftware.ludwig.dto.ToolSelection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Map;
/*
    The `AiConversationService` provides an abstraction for managing AI-powered conversations. It makes the process user-friendly,
    efficient, and contextually relevant by bridging the gap between user input and AI capabilities while optionally maintaining
    session histories and interaction data. It acts as the backbone for powering intelligent chat-driven workflows.
 */
@Slf4j
@Service
public class AiConversationService {

    private final McpService mcpService;
    private final UserService userService;
    private final static String CATEGORY = "UserConversations";
    private final KnowledgeBaseService knowledgeBaseService;


    public AiConversationService(McpService mcpService, UserService userService, KnowledgeBaseService knowledgeBaseService ) {
        this.mcpService = mcpService;
        this.userService = userService;
        this.knowledgeBaseService = knowledgeBaseService;
    }

    public ChatResponseDto processQuery(String sessionId, String userMessage,String[] apps, String[] knowledgeBases) throws IOException {

        Map<String,Object> userDetails = userService.getAuthenticatedUserDetails();
        String userId = userDetails.containsKey("email") ? (String)userDetails.get("email") : (String)userDetails.get("sub");

        ToolSelection  toolSelection = mcpService.selectToolWithAi(userMessage,apps);

        if(toolSelection != null){
            log.info("Tool selection: {}",toolSelection);
            if(toolSelection.getTool() != null && !toolSelection.getTool().equalsIgnoreCase("None")){
                return new ChatResponseDto(mcpService.run(toolSelection,apps));
            }
        }

        KnowledgeBase kb = knowledgeBaseService.findFirstByCategoryAndUserId(CATEGORY,userId);
        if(kb == null){
            kb = knowledgeBaseService.save(KnowledgeBase.builder()
                    .description("User Conversations")
                    .userId(userId)
                    .name(userId + " Conversations")
                    .category(CATEGORY).build()).block();
        }

        assert kb != null;
        Knowledge request =  Knowledge.builder().baseId(kb.getId()).userId(userId).text(userMessage).category(CATEGORY).build();

        // Generate AI response based on the constructed context
        String aiResponse = knowledgeBaseService.processRequest(request,"Your role is that of an assistant.");

        // Return AI's response wrapped in a DTO
        return new ChatResponseDto(aiResponse.equalsIgnoreCase("") ? "I'm sorry, I am at a loss for words." : aiResponse );
    }


}
