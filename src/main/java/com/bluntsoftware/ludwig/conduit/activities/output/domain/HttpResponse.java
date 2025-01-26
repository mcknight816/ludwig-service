package com.bluntsoftware.ludwig.conduit.activities.output.domain;

import com.bluntsoftware.ludwig.conduit.utils.schema.EntitySchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.PropertyFormat;
import com.bluntsoftware.ludwig.conduit.utils.schema.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HttpResponse implements EntitySchema {
    String name;
    String  outputType;
    String  outputMethod;
    String  file;
    String  payload;

    @Override
    public JsonSchema getJsonSchema() {
        JsonSchema schema = JsonSchema.builder().title(this.getName()).build();

        List<String> outType = new ArrayList<>();
        outType.add("json");
        outType.add("html");
        outType.add("xml");
        outType.add("file");

        List<String> outMethod = new ArrayList<>();
        outMethod.add("stream");
        outMethod.add("download");

        schema.addEnum("output_method",outMethod ,"stream");
        schema.addEnum("output_type",outType ,"json");
        schema.addString("file", StringProperty.builder().defaultValue("file-location").format(PropertyFormat.IMAGE_CHOOSER).build());
        PropertyFormat payloadFormat = PropertyFormat.JSON;
        Map<String,Object> schemaMap = schema.getValue();
        if(schemaMap != null && schemaMap.containsKey("output_type") && schemaMap.get("output_type") != null) {
            Object outputType = schemaMap.get("output_type");
            if(outputType instanceof String) {
                payloadFormat = PropertyFormat.fromValue(outputType.toString());
            }
        }
        schema.addString("payload",StringProperty.builder().defaultValue("{}").format(payloadFormat).build());
        return schema;
    }
}
