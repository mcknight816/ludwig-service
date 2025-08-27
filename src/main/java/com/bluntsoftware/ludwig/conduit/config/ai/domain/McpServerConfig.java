package com.bluntsoftware.ludwig.conduit.config.ai.domain;

import com.bluntsoftware.ludwig.conduit.service.ai.domain.OpenAiModel;
import com.bluntsoftware.ludwig.conduit.utils.schema.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class McpServerConfig implements EntitySchema {
    private String config;
    private String transport;
    @Override
    public JsonSchema getJsonSchema() {
        Map<String, Property> props = new HashMap<>();
        JsonSchema openApiSchema = JsonSchema.builder().title("mcp-server").build();
        openApiSchema.addEnum("transport","transport", Arrays.stream(MCPTransport.values()).map(MCPTransport::toString).collect(Collectors.toList()), OpenAiModel.GPT_4_MINI.toString() );
        props.put("config", StringProperty.builder()
                .title("config")
                .format(PropertyFormat.JSON)
                .defaultValue("{\n" + "}").build());
        openApiSchema.addProperties(props);
        return openApiSchema;
    }
}

