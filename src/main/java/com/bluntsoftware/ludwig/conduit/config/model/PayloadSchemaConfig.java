package com.bluntsoftware.ludwig.conduit.config.model;


import com.bluntsoftware.ludwig.conduit.impl.ActivityConfigImpl;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public class PayloadSchemaConfig extends ActivityConfigImpl {

    @Override
    public JsonSchema getRecord() {
        JsonSchema schema = new JsonSchema("PayloadSchema");
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
    public Map test() {
        return null;
    }
}
