package com.bluntsoftware.ludwig.conduit.activities.output;


import com.bluntsoftware.ludwig.conduit.activities.ActivityImpl;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.PropertyFormat;
import com.bluntsoftware.ludwig.conduit.utils.schema.StringProperty;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex Mcknight on 2/27/2017.
 */
@Service
public class HttpResponseActivity extends ActivityImpl {
    public HttpResponseActivity(ActivityConfigRepository activityConfigRepository) {
        super(activityConfigRepository);
    }

    @Override
    public Map<String, Object> run(Map<String, Object> input)throws Exception {
        return null;
    }

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
