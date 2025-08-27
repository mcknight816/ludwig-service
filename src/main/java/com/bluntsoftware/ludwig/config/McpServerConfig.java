package com.bluntsoftware.ludwig.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpServerTransportProvider;
import org.springframework.context.annotation.Bean;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class McpServerConfig {

    static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
/*
    @Bean
    public McpServer mcpServer(DynamicToolRegistry toolRegistry) {
        McpServerTransportProvider transportProvider = new StdioServerTransportProvider();

        return McpServer.sync(transportProvider)
                .toolsProvider(() -> {
                    String tenantId = TenantContext.getCurrentTenant();
                    if (tenantId == null) {
                        throw new IllegalStateException("Tenant not found in context");
                    }
                    return toolRegistry.getToolsForTenant(tenantId);
                })
                .build();
    }
   */



}
