package com.bluntsoftware.ludwig.conduit.config.model.domain;


import com.bluntsoftware.ludwig.conduit.utils.schema.*;
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

    public JsonSchema getSchema() {
        Map<String, Property> props = new HashMap<>();
        props.put("schema", StringProperty.builder()
                .title("Payload Schema")
                .format(PropertyFormat.JSON)
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
