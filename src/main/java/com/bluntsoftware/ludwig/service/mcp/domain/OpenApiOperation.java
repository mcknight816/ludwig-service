package com.bluntsoftware.ludwig.service.mcp.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpenApiOperation {
    private String operationId;
    private String description;
    private List<String> tags;
    private List<OpenApiParameter> parameters;
    private Map<String, Object> responses;
    private OpenApiRequestBody requestBody;
}