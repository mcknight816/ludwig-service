package com.bluntsoftware.ludwig.conduit.activities.conduit;


import com.bluntsoftware.ludwig.conduit.impl.ActivityImpl;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.schema.ValidationUtils;
import com.bluntsoftware.ludwig.repository.FlowConfigRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
@Service
public class ValidationActivity extends ActivityImpl {

    public ValidationActivity(FlowConfigRepository flowConfigRepository) {
        super(flowConfigRepository);
    }

    @Override
    public Map<String, Object> run(Map<String, Object> input) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(input.get("payload"));
        String schema = input.get("schema").toString();
        final com.github.fge.jsonschema.main.JsonSchema schemaNode = ValidationUtils.getSchemaNode(schema);
        final JsonNode jsonNode = ValidationUtils.getJsonNode(json);
        ValidationUtils.validateJson(schemaNode,jsonNode);
        Map<String, Object> ret = getOutput();
        ret.put("payload",mapper.readValue(json, Map.class));
        return ret;
    }
    @Override
    public String getIcon() {
        return "fa-thumbs-up";
    }

    @Override
    public JsonSchema getSchema() {
        JsonSchema schema = new JsonSchema("Schema");
        schema.addString("","payload","",null,true);
        schema.addString("Schema","schema","{\n" +
                "    \"$schema\": \"http://json-schema.org/draft-04/schema#\",\n" +
                "    \"title\": \"Product\",\n" +
                "    \"description\": \"A product from the catalog\",\n" +
                "    \"type\": \"object\",\n" +
                "    \"properties\": {\n" +
                "        \"name\": {\n" +
                "            \"description\": \"Name of the product\",\n" +
                "            \"type\": \"string\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"required\": [ \"name\"]\n" +
                "}","json",false);
        return schema;
    }
    @Override
    public Map<String, Object> getOutput() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("payload","");
        return ret;
    }
}
