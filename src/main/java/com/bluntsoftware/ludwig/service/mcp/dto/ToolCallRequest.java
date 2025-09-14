package com.bluntsoftware.ludwig.service.mcp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToolCallRequest {
    private String toolName;
    // Arguments object to fill path/query/headers/body dynamically
    private Map<String, Object> arguments;
}