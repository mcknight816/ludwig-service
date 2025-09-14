package com.bluntsoftware.ludwig.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Represents a Model Context Protocol (MCP) Server Tool.
 * Conforms to the MCP spec: name, description, input_schema.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class McpServerTool {
    /**
     * Unique tool name. Clients use this identifier to call the tool.
     */
    private String name;

    /**
     * Human-readable description of what the tool does and how to use it.
     */
    private String description;

    /**
     * JSON Schema (draft-like) describing the input object the tool expects.
     * Example:
     * {
     *   "type": "object",
     *   "properties": { "query": { "type": "string" } },
     *   "required": ["query"]
     * }
     */
    private Map<String, Object> inputSchema;
    private Application application;
    private Flow flow;
    private FlowActivity flowActivity;
}

