package com.bluntsoftware.ludwig.service.mcp.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpenApiRequestBody {
    private String description;
    private boolean required;
    // content type -> media type object (raw map so we can pass through schemas)
    private Map<String, Object> content;
}