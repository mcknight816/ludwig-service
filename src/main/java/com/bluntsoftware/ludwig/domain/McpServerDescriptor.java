package com.bluntsoftware.ludwig.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Map;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class McpServerDescriptor {
    private String name;
    // Example: "stdio", "websocket", etc. Adjust based on the transports you support.
    private String transport;
    // Command to run when using stdio or similar transports
    private String command;
    // Optional args/env for starting an MCP server process
    private String[] args;
    private Map<String, String> environment;
    // Optional: URL for WebSocket transport, etc.
    private String url;
}

