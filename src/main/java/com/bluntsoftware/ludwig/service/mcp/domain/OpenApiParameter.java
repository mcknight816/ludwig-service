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
public class OpenApiParameter {
    private String name;
    private String inLocation; // "query","path","header","cookie"
    private boolean required;
    private String description;
    private Map<String, Object> schema; // keep raw schema map for dynamic input schema generation
}