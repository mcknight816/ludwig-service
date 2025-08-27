package com.bluntsoftware.ludwig.conduit.config.ai;

import com.bluntsoftware.ludwig.conduit.config.ActivityConfigImpl;
import com.bluntsoftware.ludwig.conduit.config.ConfigTestResult;
import com.bluntsoftware.ludwig.conduit.config.ai.domain.McpServerConfig;
import com.bluntsoftware.ludwig.domain.McpServerDescriptor;
import com.bluntsoftware.ludwig.utils.McpServerDescriptorJsonFinder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class McpServerConfigActivity extends ActivityConfigImpl<McpServerConfig> {
    @Override
    public ConfigTestResult testConfig(McpServerConfig config) {
        ConfigTestResult result = ConfigTestResult.builder().error(true).message("JSON Mcp Server config is NOT valid !").build();
        McpServerDescriptorJsonFinder finder = new McpServerDescriptorJsonFinder();

        List<McpServerDescriptor> servers = new ArrayList<>();
        if(config != null && config.getConfig() != null){
            String schema = config.getConfig();
            ObjectMapper mapper = new ObjectMapper();
            ConcurrentHashMap<String,Object> configMap = null;
            try {
                configMap = mapper.readValue(schema, ConcurrentHashMap.class);
                var resultsFromMap = finder.findInMap(configMap);

                resultsFromMap.forEach(fd -> {
                    System.out.println("Found at " + fd.getJsonPath());
                    System.out.println("Descriptor: " + fd.getDescriptor());

                    McpServerDescriptor descriptor = fd.getDescriptor();
                    if(descriptor != null && descriptor.getTransport() == null){
                        descriptor.setTransport(config.getTransport());
                    }
                    if(descriptor != null && descriptor.getName() == null){
                        descriptor.setName(this.getName());
                    }
                    servers.add(descriptor);

                });
                if(!servers.isEmpty()){
                    return ConfigTestResult.builder().success(true).message("Success")
                            .message(servers.size() + " Mcp Servers found in the JSON Schema")
                            .build();
                }
            } catch (JsonProcessingException e) {
                result.setHint(e.getMessage());
            }
        }
        return result;
    }
}
