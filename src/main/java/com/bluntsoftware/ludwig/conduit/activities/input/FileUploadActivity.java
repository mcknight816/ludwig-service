package com.bluntsoftware.ludwig.conduit.activities.input;


import com.bluntsoftware.ludwig.conduit.config.model.PayloadSchemaConfig;
import com.bluntsoftware.ludwig.conduit.config.model.domain.PayloadSchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.ValidationUtils;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
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

    public FileUploadActivity(PayloadSchemaConfig payloadSchemaConfig, ActivityConfigRepository activityConfigRepository) {
        super(activityConfigRepository);
        this.payloadSchemaConfig = payloadSchemaConfig;
    }

    @Override
    public JsonSchema getJsonSchema() {
        JsonSchema schema = super.getJsonSchema();
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
        PayloadSchema config = this.getExternalConfigByName(input.get(payloadSchemaConfig.getPropertyName()), PayloadSchema.class);
        if(config != null && config.getSchema() != null){
                String schema = config.getSchema();
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(input.get("payload"));
                final com.github.fge.jsonschema.main.JsonSchema schemaNode = ValidationUtils.getSchemaNode(schema);
                final JsonNode jsonNode = ValidationUtils.getJsonNode(json);
                ValidationUtils.validateJson(schemaNode,jsonNode);
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
