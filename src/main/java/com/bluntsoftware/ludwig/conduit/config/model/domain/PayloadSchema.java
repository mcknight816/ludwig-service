package com.bluntsoftware.ludwig.conduit.config.model.domain;

import com.bluntsoftware.ludwig.conduit.schema.EntitySchema;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.schema.Property;
import com.bluntsoftware.ludwig.conduit.schema.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayloadSchema implements EntitySchema {
    Map<String,Object> schema;
    public static JsonSchema getSchema() {
        Map<String, Property> props = new HashMap<>();
        props.put("schema", StringProperty.builder()
                .title("Schema")
                .format("json")
                .defaultValue("{\n" +
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
                "}").build());
        return JsonSchema.builder().properties(props).build();
    }
}
