package com.bluntsoftware.ludwig.conduit.config.model;


import com.bluntsoftware.ludwig.conduit.config.ConfigTestResult;
import com.bluntsoftware.ludwig.conduit.config.model.domain.PayloadSchema;
import com.bluntsoftware.ludwig.conduit.config.ActivityConfigImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service class that extends ActivityConfigImpl for handling PayloadSchema configurations.
 * This class validates JSON schema structures defined in PayloadSchema to ensure correctness.
 */
@Service
public class PayloadSchemaConfig extends ActivityConfigImpl<PayloadSchema> {

    @Override
    public ConfigTestResult testConfig(PayloadSchema config) {
        ConfigTestResult result = ConfigTestResult.builder()
                .error(true)
                .message("JSON Schema is NOT valid !")
                .build();

        if(config != null && config.getSchema() != null){
            String schema = config.getSchema();
            ObjectMapper mapper = new ObjectMapper();
            ConcurrentHashMap<String,Object> jsonSchema = null;
            try {
                jsonSchema = mapper.readValue(schema, ConcurrentHashMap.class);
                if(jsonSchema.containsKey("title")) {
                    return ConfigTestResult.builder()
                            .success(true)
                            .message(String.format("JSON Schema for %s appears to be valid",jsonSchema.get("title")))
                            .build();
                }
            } catch (JsonProcessingException e) {
                result.setHint(e.getMessage());
            }
        }
        return ConfigTestResult.builder()
                .error(true)
                .message("JSON Schema is NOT valid !")
                .build();
    }


}
