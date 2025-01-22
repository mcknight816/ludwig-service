package com.bluntsoftware.ludwig.conduit.config.model;


import com.bluntsoftware.ludwig.conduit.config.ConfigTestResult;
import com.bluntsoftware.ludwig.conduit.config.model.domain.PayloadSchema;
import com.bluntsoftware.ludwig.conduit.config.ActivityConfigImpl;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PayloadSchemaConfig extends ActivityConfigImpl<PayloadSchema> {

    @Override
    public JsonSchema getRecord() {
        return PayloadSchema.getSchema();
    }

    @Override
    public ConfigTestResult test(Map<String, Object> payloadSchema) {
        ConfigTestResult result = ConfigTestResult.builder()
                .error(true)
                .message("JSON Schema is NOT valid !")
                .build();

        if(payloadSchema.containsKey("schema")){
            String schema = payloadSchema.get("schema").toString();
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
