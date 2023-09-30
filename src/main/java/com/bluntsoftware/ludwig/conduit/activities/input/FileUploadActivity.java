package com.bluntsoftware.ludwig.conduit.activities.input;


import com.bluntsoftware.ludwig.conduit.config.model.PayloadSchemaConfig;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.schema.ValidationUtils;
import com.bluntsoftware.ludwig.repository.FlowConfigRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex Mcknight on 1/12/2017.
 */
@Service
public class FileUploadActivity extends InputActivity {
    PayloadSchemaConfig payloadSchemaConfig;

    public FileUploadActivity(PayloadSchemaConfig payloadSchemaConfig, FlowConfigRepository flowConfigRepository) {
        super(flowConfigRepository);
        this.payloadSchemaConfig = payloadSchemaConfig;
    }

    @Override
    public JsonSchema getSchema() {
        JsonSchema schema = super.getSchema();
        schema.addConfig(payloadSchemaConfig);
        return schema;
    }

    @Override
    public Map<String, Object> getInput() {
        Map<String, Object> in = super.getInput();
        Map<String,Object> fileInfo = new HashMap<>();
        fileInfo.put("originalFilename","");
        fileInfo.put("name","");
        fileInfo.put("size",0);
        fileInfo.put("path","");
        fileInfo.put("uid","");
        fileInfo.put("uidFilename","");

        in.put("fileInfo",fileInfo);
        return in;
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
        return "fa-upload";
    }

    @Override
    public String getName() {
        return "Upload";
    }
}
