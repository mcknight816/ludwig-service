package com.bluntsoftware.ludwig.service.mcp.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Map;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class McpTool {
    private String name;
    private String description;
    private String method;  // GET, POST, DELETE, ...
    private String path;    // e.g. /api/biz-vest/customer/{id}
    private String serverBaseUrl; // chosen server from the spec
    // Schema for tool inputs (JSON Schema draft-ish map)
    private Map<String, Object> inputSchema;
    // Derived from parameters: which are path/query/header etc
    private List<OpenApiParameter> parameters;
    private boolean hasRequestBody;
    private String requestBodyContentType; // e.g. application/json
}