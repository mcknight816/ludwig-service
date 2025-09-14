package com.bluntsoftware.ludwig.service.mcp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToolCallResponse {
    private int status;
    private String contentType;
    private Object body;
}