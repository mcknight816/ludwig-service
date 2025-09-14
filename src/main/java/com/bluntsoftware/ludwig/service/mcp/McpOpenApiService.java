package com.bluntsoftware.ludwig.service.mcp;

import com.bluntsoftware.ludwig.service.mcp.domain.McpTool;
import com.bluntsoftware.ludwig.service.mcp.domain.OpenApiParameter;
import com.bluntsoftware.ludwig.service.mcp.domain.OpenApiSpec;
import com.bluntsoftware.ludwig.service.mcp.dto.ToolCallRequest;
import com.bluntsoftware.ludwig.service.mcp.dto.ToolCallResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class McpOpenApiService {

    private final WebClient mcpWebClient;
    private final OpenApiParser openApiParser;
    private final ObjectMapper objectMapper;

    private volatile OpenApiSpec currentSpec;
    private final Map<String, McpTool> toolsByName = new ConcurrentHashMap<>();

    // Load/refresh OpenAPI definition (JSON string). Builds tools dynamically.
    public synchronized void loadOpenApi(String openApiJson) {
        this.currentSpec = openApiParser.parse(openApiJson);
        List<McpTool> tools = openApiParser.toTools(currentSpec);
        toolsByName.clear();
        for (McpTool t : tools) {
            toolsByName.put(t.getName(), t);
        }
        log.info("MCP loaded {} tool(s) from OpenAPI '{}'", toolsByName.size(),
                currentSpec.getInfo() != null ? currentSpec.getInfo().getTitle() : "unknown");
    }

    public List<McpTool> listTools() {
        return new ArrayList<>(toolsByName.values());
    }

    public Mono<ToolCallResponse> call(ToolCallRequest request) {
        McpTool tool = toolsByName.get(request.getToolName());
        if (tool == null) {
            return Mono.error(new IllegalArgumentException("Unknown tool: " + request.getToolName()));
        }
        Map<String, Object> args = Optional.ofNullable(request.getArguments()).orElse(Map.of());

        String url = buildUrl(tool, args);
        WebClient.RequestBodySpec spec = mcpWebClient
                .method(org.springframework.http.HttpMethod.valueOf(tool.getMethod()))
                .uri(url)
                .headers(h -> applyHeaderParams(h::set, tool, args));

        if (tool.isHasRequestBody()) {
            Object body = args.get("body");
            String ct = Optional.ofNullable(tool.getRequestBodyContentType()).orElse(MediaType.APPLICATION_JSON_VALUE);
            spec = spec.contentType(MediaType.parseMediaType(ct));
            if (body == null) {
                // If body required but missing, send empty object
                body = Map.of();
            }
            spec.bodyValue(body);
        }

        return spec.exchangeToMono(cr -> {
            MediaType mt = cr.headers().contentType().orElse(MediaType.APPLICATION_OCTET_STREAM);
            Mono<Object> bodyMono;
            if (MediaType.APPLICATION_JSON.isCompatibleWith(mt)) {
                bodyMono = cr.bodyToMono(Object.class);
            } else {
                bodyMono = cr.bodyToMono(String.class).map(s -> s);
            }
            return bodyMono.map(b -> ToolCallResponse.builder()
                    .status(cr.statusCode().value())
                    .contentType(mt.toString())
                    .body(b)
                    .build());
        });
    }

    private String buildUrl(McpTool tool, Map<String, Object> args) {
        String base = Optional.ofNullable(tool.getServerBaseUrl()).orElse("");
        String path = substitutePathParams(tool.getPath(), tool.getParameters(), args);
        String query = buildQueryParams(tool.getParameters(), args);
        return (base.endsWith("/") ? base.substring(0, base.length() - 1) : base) + path + query;
    }

    private String substitutePathParams(String path, List<OpenApiParameter> params, Map<String, Object> args) {
        String result = path;
        for (OpenApiParameter p : params) {
            if (!"path".equalsIgnoreCase(p.getInLocation())) continue;
            Object v = args.get(p.getName());
            if (v == null && p.isRequired()) {
                throw new IllegalArgumentException("Missing required path parameter: " + p.getName());
            }
            if (v != null) {
                String enc = urlEncode(String.valueOf(v));
                result = result.replace("{" + p.getName() + "}", enc);
            }
        }
        return result;
    }

    private String buildQueryParams(List<OpenApiParameter> params, Map<String, Object> args) {
        List<String> pairs = new ArrayList<>();
        for (OpenApiParameter p : params) {
            if (!"query".equalsIgnoreCase(p.getInLocation())) continue;
            Object v = args.get(p.getName());
            if (v == null) continue;
            if (v instanceof Map || v instanceof List) {
                pairs.add(urlEncode(p.getName()) + "=" + urlEncode(toJson(v)));
            } else {
                pairs.add(urlEncode(p.getName()) + "=" + urlEncode(String.valueOf(v)));
            }
        }
        return pairs.isEmpty() ? "" : "?" + String.join("&", pairs);
    }

    private void applyHeaderParams(java.util.function.BiConsumer<String, String> headerSetter,
                                   McpTool tool, Map<String, Object> args) {
        for (OpenApiParameter p : tool.getParameters()) {
            if (!"header".equalsIgnoreCase(p.getInLocation())) continue;
            Object v = args.get(p.getName());
            if (v == null && p.isRequired()) {
                throw new IllegalArgumentException("Missing required header: " + p.getName());
            }
            if (v != null) {
                headerSetter.accept(p.getName(), String.valueOf(v));
            }
        }
    }

    private String urlEncode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private String toJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            return String.valueOf(o);
        }
    }
}