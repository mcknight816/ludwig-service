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
public class OpenApiSpec {
    private String openapi;
    private Map<String, OpenApiPathItem> paths;
    private Map<String, Object> components;
    private List<OpenApiServer> servers;
    private OpenApiInfo info;
    private String url;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OpenApiInfo {
        private String title;
        private String version;
        private String description;
    }
}