package com.bluntsoftware.ludwig.service;

import com.bluntsoftware.ludwig.conduit.service.ai.AIService;
import com.bluntsoftware.ludwig.conduit.service.ai.domain.AICompletionRequest;
import com.bluntsoftware.ludwig.conduit.service.ai.domain.AIMessage;
import com.bluntsoftware.ludwig.conduit.service.ai.domain.OpenAiModel;
import com.bluntsoftware.ludwig.config.AppConfig;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Map;

@Service
public class OpenAiService {

    private final AIService aiService;
    public OpenAiService(AppConfig appConfig) {
        this.aiService = new AIService(appConfig);
    }

    public String callOpenAi(String prompt){
        return this.aiService.completions(AICompletionRequest.builder()
                        .message(AIMessage.builder().role("user").content(prompt).build())
                        .model(OpenAiModel.GPT_4.getValue())
                .build()).getChoices().get(0).getMessage().getContent();
    }

    public String completeMissingDetails(Map<String, String> conversationState) throws IOException {
        StringBuilder prompt = new StringBuilder("The user wants to create an application but hasn't provided some required details. ");
        prompt.append("Ask for the missing information based on the current state:\n\n");

        if (!conversationState.containsKey("applicationName")) {
            prompt.append("- What name should I call this application?\n");
        }
        if (!conversationState.containsKey("flowType")) {
            prompt.append("- Would you like to create a flow? What type? Options: [\"MONGO CRUD FLOW\", \"TELEGRAM AI FLOW\"]\n");
        }
        if (!conversationState.containsKey("flowName")) {
            prompt.append("- What should we name the flow?\n");
        }

        if ("MONGO CRUD FLOW".equals(conversationState.get("flowType")) && !conversationState.containsKey("databaseType")) {
            prompt.append("- What database config should we use? Options: []\n");
        }

        if ("MONGO CRUD FLOW".equals(conversationState.get("flowType")) && !conversationState.containsKey("databaseName")) {
            prompt.append("- What should be the database name?\n");
        }
        if ("MONGO CRUD FLOW".equals(conversationState.get("flowType")) && !conversationState.containsKey("collectionName")) {
            prompt.append("- What should be the collection/table name?\n");
            if (!conversationState.containsKey("schema")) {
                prompt.append("- Should I auto-generate a schema based on the collection/table name, or would you like to provide a JSON schema?\n");
            }
        }
        return callOpenAi(prompt.toString());
    }
}
