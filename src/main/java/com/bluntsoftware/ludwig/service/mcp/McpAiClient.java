package com.bluntsoftware.ludwig.service.mcp;

import com.bluntsoftware.ludwig.service.OpenAiService;
import com.bluntsoftware.ludwig.service.mcp.domain.McpTool;
import com.bluntsoftware.ludwig.service.mcp.dto.ToolCallRequest;
import com.bluntsoftware.ludwig.service.mcp.dto.ToolCallResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class McpAiClient {

    private final McpOpenApiService mcpOpenApiService;
    private final ObjectMapper objectMapper;
    private final OpenAiService openAiService; // Provides callOpenAi(String prompt)

    public Mono<String> answer(String userPrompt) {
        List<McpTool> tools = mcpOpenApiService.listTools();
        if (tools.isEmpty()) {
            return Mono.fromCallable(() ->
                    openAiService.callOpenAi("User request:\n" + userPrompt + "\nNo tools available. Answer directly.")
            );
        }

        return Mono.fromCallable(() -> selectToolWithAi(userPrompt, tools))
                .flatMap(selection -> {
                    if (selection == null || selection.getTool() == null || "none".equalsIgnoreCase(selection.getTool())) {
                        return Mono.fromCallable(() ->
                                openAiService.callOpenAi("User request:\n" + userPrompt + "\nAnswer directly.")
                        );
                    }
                    McpTool tool = tools.stream()
                            .filter(t -> t.getName().equalsIgnoreCase(selection.getTool()))
                            .findFirst()
                            .orElse(null);
                    if (tool == null) {
                        return Mono.fromCallable(() ->
                                openAiService.callOpenAi("User request:\n" + userPrompt + "\nTool not found after selection. Answer directly.")
                        );
                    }

                    ToolCallRequest request = ToolCallRequest.builder()
                            .toolName(tool.getName())
                            .arguments(Optional.ofNullable(selection.getArgs()).orElse(Collections.emptyMap()))
                            .build();

                    return mcpOpenApiService.call(request)
                            .onErrorResume(e -> {
                                log.warn("Tool call failed for {}: {}", tool.getName(), e.getMessage());
                                return Mono.just(ToolCallResponse.builder()
                                        .status(500)
                                        .contentType("text/plain")
                                        .body("Tool call failed: " + e.getMessage())
                                        .build());
                            })
                            .flatMap(resp -> Mono.fromCallable(() -> composeFinalAnswer(userPrompt, tool, selection.getArgs(), resp)));
                });
    }

    private Selection selectToolWithAi(String userPrompt, List<McpTool> tools) {
        try {
            String toolsSummary = tools.stream()
                    .map(t -> Map.of(
                            "name", t.getName(),
                            "description", Optional.ofNullable(t.getDescription()).orElse(""),
                            "inputSchema", Optional.ofNullable(t.getInputSchema()).orElse(Map.of())
                    ))
                    .collect(Collectors.toList())
                    .toString();

            String selectorPrompt =
                    "You are an assistant that selects the best tool and arguments for a user's request.\n" +
                            "Return ONLY a compact JSON object with fields: tool (string) and args (object).\n" +
                            "If no tool is appropriate, return: {\"tool\":\"none\",\"args\":{}}.\n\n" +
                            "Available tools (name, description, input_schema):\n" + toolsSummary + "\n\n" +
                            "User request:\n" + userPrompt + "\n\n" +
                            "JSON response with no commentary:";

            String ai = openAiService.callOpenAi(selectorPrompt);
            return parseSelection(ai);
        } catch (Exception e) {
            log.warn("Failed to select tool with AI: {}", e.getMessage());
            return Selection.builder().tool("none").args(Map.of()).build();
        }
    }

    private Selection parseSelection(String json) {
        try {
            return objectMapper.readValue(json, Selection.class);
        } catch (Exception e) {
            log.debug("Primary parse failed, trying to extract JSON substring: {}", e.getMessage());
            String extracted = extractFirstJsonObject(json);
            if (extracted != null) {
                try {
                    return objectMapper.readValue(extracted, Selection.class);
                } catch (Exception ignore) {
                }
            }
            return Selection.builder().tool("none").args(Map.of()).build();
        }
    }

    private String composeFinalAnswer(String userPrompt, McpTool tool, Map<String, Object> args, ToolCallResponse resp) {
        String toolResultJson;
        try {
            toolResultJson = objectMapper.writeValueAsString(resp.getBody());
        } catch (Exception e) {
            toolResultJson = String.valueOf(resp.getBody());
        }

        String summarizerPrompt =
                "You are an assistant that used an MCP tool to fulfill a user request.\n" +
                        "Summarize the result in helpful natural language for the user.\n" +
                        "Include key details and avoid raw JSON unless necessary.\n\n" +
                        "User request:\n" + userPrompt + "\n\n" +
                        "Tool used: " + tool.getName() + " (" + tool.getDescription() + ")\n" +
                        "Arguments used (JSON): " + safeJson(args) + "\n" +
                        "HTTP status: " + resp.getStatus() + "\n" +
                        "Content-Type: " + resp.getContentType() + "\n" +
                        "Result body (JSON or text): " + toolResultJson + "\n\n" +
                        "Final answer:";

        return openAiService.callOpenAi(summarizerPrompt);
    }

    private String safeJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            return String.valueOf(o);
        }
    }

    // Very simple JSON object extractor as a fallback
    private String extractFirstJsonObject(String s) {
        int start = s.indexOf('{');
        if (start < 0) return null;
        int depth = 0;
        for (int i = start; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '{') depth++;
            if (c == '}') depth--;
            if (depth == 0) {
                return s.substring(start, i + 1);
            }
        }
        return null;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Selection {
        private String tool;
        private Map<String, Object> args;

        public Selection() {
            this.tool = "none";
            this.args = new HashMap<>();
        }
    }
}