package com.bluntsoftware.ludwig.conduit.config.model;


import com.bluntsoftware.ludwig.conduit.config.model.domain.PayloadSchema;
import com.bluntsoftware.ludwig.conduit.impl.ActivityConfigImpl;
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
    public PayloadSchema getConfig() {
        return PayloadSchema.builder().build();
    }

    @Override
    public Map test() {
        return null;
    }
}
