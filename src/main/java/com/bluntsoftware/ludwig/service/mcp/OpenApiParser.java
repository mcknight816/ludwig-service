package com.bluntsoftware.ludwig.service.mcp;

import com.bluntsoftware.ludwig.service.mcp.domain.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenApiParser {

    private final ObjectMapper objectMapper;

    public OpenApiSpec parse(String openApiJson) {
        try {
            Map<String, Object> raw = objectMapper.readValue(openApiJson, new TypeReference<Map<String, Object>>() {});
            OpenApiSpec.OpenApiInfo info = objectMapper.convertValue(raw.getOrDefault("info", Map.of()), OpenApiSpec.OpenApiInfo.class);
            List<OpenApiServer> servers = Optional.ofNullable(raw.get("servers"))
                    .map(v -> objectMapper.convertValue(v, new TypeReference<List<OpenApiServer>>() {}))
                    .orElse(List.of());
            Map<String, OpenApiPathItem> paths = Optional.ofNullable(raw.get("paths"))
                    .map(v -> objectMapper.convertValue(v, new TypeReference<Map<String, OpenApiPathItem>>() {}))
                    .orElse(Map.of());
            Map<String, Object> components = Optional.ofNullable(raw.get("components"))
                    .map(v -> objectMapper.convertValue(v, new TypeReference<Map<String, Object>>() {}))
                    .orElse(Map.of());
            String url = Optional.ofNullable((String) raw.get("url")).orElse(null);

            return OpenApiSpec.builder()
                    .openapi((String) raw.get("openapi"))
                    .paths(paths)
                    .components(components)
                    .servers(servers)
                    .info(info)
                    .url(url)
                    .build();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse OpenAPI JSON: " + e.getMessage(), e);
        }
    }

    public List<McpTool> toTools(OpenApiSpec spec) {
        String base = chooseBaseUrl(spec);
        Map<String, OpenApiPathItem> paths = Optional.ofNullable(spec.getPaths()).orElse(Map.of());

        List<McpTool> tools = new ArrayList<>();
        for (Map.Entry<String, OpenApiPathItem> e : paths.entrySet()) {
            String path = e.getKey();
            OpenApiPathItem item = e.getValue();
            if (item.getGet() != null) tools.add(buildTool("GET", path, item.getGet(), base));
            if (item.getPost() != null) tools.add(buildTool("POST", path, item.getPost(), base));
            if (item.getPut() != null) tools.add(buildTool("PUT", path, item.getPut(), base));
            if (item.getDelete() != null) tools.add(buildTool("DELETE", path, item.getDelete(), base));
            if (item.getPatch() != null) tools.add(buildTool("PATCH", path, item.getPatch(), base));
            if (item.getHead() != null) tools.add(buildTool("HEAD", path, item.getHead(), base));
            if (item.getOptions() != null) tools.add(buildTool("OPTIONS", path, item.getOptions(), base));
            if (item.getTrace() != null) tools.add(buildTool("TRACE", path, item.getTrace(), base));
        }
        return tools;
    }

    private String chooseBaseUrl(OpenApiSpec spec) {
        if (spec.getServers() != null && !spec.getServers().isEmpty()) {
            return spec.getServers().get(0).getUrl();
        }
        if (spec.getUrl() != null) return spec.getUrl();
        return "";
    }

    private McpTool buildTool(String method, String path, OpenApiOperation op, String base) {
        String name = generateToolName(method, path, op);
        List<OpenApiParameter> params = Optional.ofNullable(op.getParameters()).orElse(List.of()).stream()
                .map(this::normalizeParameter)
                .collect(Collectors.toList());

        Map<String, Object> inputSchema = buildInputSchema(params, op.getRequestBody());
        boolean hasBody = op.getRequestBody() != null;
        String contentType = resolvePrimaryContentType(op.getRequestBody());

        return McpTool.builder()
                .name(name)
                .description(Optional.ofNullable(op.getDescription()).orElse(method + " " + path))
                .method(method)
                .path(path)
                .serverBaseUrl(base)
                .parameters(params)
                .inputSchema(inputSchema)
                .hasRequestBody(hasBody)
                .requestBodyContentType(contentType)
                .build();
    }

    private String generateToolName(String method, String path, OpenApiOperation op) {
        String opId = Optional.ofNullable(op.getOperationId()).orElse("");
        if (!opId.isBlank()) return opId;
        // derive from method + path
        String clean = path.replaceAll("[{}]", "").replace('/', '_').replaceAll("_+", "_");
        if (clean.startsWith("_")) clean = clean.substring(1);
        return (method + "_" + clean).toLowerCase();
    }

    private OpenApiParameter normalizeParameter(OpenApiParameter p) {
        // The JSON property is "in", but "in" is reserved in Java; we mapped to inLocation
        if (p.getInLocation() == null) {
            // Try to map from a raw map if needed (in case Jackson didn't bind)
            return OpenApiParameter.builder()
                    .name(p.getName())
                    .inLocation(p.getInLocation())
                    .required(p.isRequired())
                    .description(p.getDescription())
                    .schema(p.getSchema())
                    .build();
        }
        return p;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> buildInputSchema(List<OpenApiParameter> params, OpenApiRequestBody requestBody) {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");

        Map<String, Object> properties = new LinkedHashMap<>();
        List<String> required = new ArrayList<>();

        for (OpenApiParameter p : params) {
            Map<String, Object> prop = new LinkedHashMap<>();
            Map<String, Object> s = Optional.ofNullable(p.getSchema()).orElse(Map.of());
            if (s.get("type") != null) prop.put("type", s.get("type"));
            if (s.get("enum") != null) prop.put("enum", s.get("enum"));
            if (s.get("default") != null) prop.put("default", s.get("default"));
            if (p.getDescription() != null) prop.put("description", p.getDescription());

            String name = p.getName();
            properties.put(name, prop);
            if (p.isRequired()) required.add(name);
        }

        if (requestBody != null) {
            // Represent body as "body" property. Keep it open (object) if we cannot resolve exact schema.
            properties.put("body", Map.of("type", "object", "description", Optional.ofNullable(requestBody.getDescription()).orElse("Request body")));
            if (requestBody.isRequired()) required.add("body");
        }

        schema.put("properties", properties);
        if (!required.isEmpty()) schema.put("required", required);
        schema.put("additionalProperties", false);
        return schema;
    }

    @SuppressWarnings("unchecked")
    private String resolvePrimaryContentType(OpenApiRequestBody body) {
        if (body == null || body.getContent() == null || body.getContent().isEmpty()) return "application/json";
        // Pick the first content type key
        return body.getContent().keySet().iterator().next();
    }
}