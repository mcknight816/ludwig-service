package com.bluntsoftware.ludwig.conduit.activities.input;


import com.bluntsoftware.ludwig.conduit.config.model.PayloadSchemaConfig;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.schema.ValidationUtils;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by Alex Mcknight on 1/12/2017.
 */
@Service
public class PostActivity extends InputActivity {
    PayloadSchemaConfig payloadSchemaConfig;

    public PostActivity(PayloadSchemaConfig payloadSchemaConfig, ActivityConfigRepository activityConfigRepository) {
        super(activityConfigRepository);
       this.payloadSchemaConfig = payloadSchemaConfig;
    }

    @Override
    public JsonSchema getSchema() {
        JsonSchema schema = super.getSchema();
        schema.addConfig(payloadSchemaConfig);
        return schema;
    }

    @Override
    public Map<String, Object> run(Map<String, Object> input) throws Exception {
        Map<String, Object> config = this.getExternalConfigByName(input.get(payloadSchemaConfig.getPropertyName()),PayloadSchemaConfig.class);
        if(config != null && config.containsKey("PayloadSchema")){
            Map<String,Object> payloadSchema = (Map<String,Object>)config.get("PayloadSchema");
            if(payloadSchema.containsKey("schema")){
                String schema = payloadSchema.get("schema").toString();
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(input.get("payload"));
                final com.github.fge.jsonschema.main.JsonSchema schemaNode = ValidationUtils.getSchemaNode(schema);
                final JsonNode jsonNode = ValidationUtils.getJsonNode(json);
                ValidationUtils.validateJson(schemaNode,jsonNode);
            }
        }
        return super.run(input);
    }

    @Override
    public String getIcon() {
        return "fa-mail-forward";
    }
}
