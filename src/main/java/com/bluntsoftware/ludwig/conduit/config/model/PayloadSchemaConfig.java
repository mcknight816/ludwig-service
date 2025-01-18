package com.bluntsoftware.ludwig.conduit.config.model;


import com.bluntsoftware.ludwig.conduit.config.ConfigTestResult;
import com.bluntsoftware.ludwig.conduit.config.model.domain.PayloadSchema;
import com.bluntsoftware.ludwig.conduit.config.ActivityConfigImpl;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PayloadSchemaConfig extends ActivityConfigImpl<PayloadSchema> {

    @Override
    public JsonSchema getRecord() {
        return PayloadSchema.getSchema();
    }

    @Override
    public ConfigTestResult test(Map<String, Object> payloadSchema) {
        return ConfigTestResult.builder()
                .error(true)
                .message(String.format("TODO: Implement Test for Params %s", payloadSchema))
                .build();
    }
}
