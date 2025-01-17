package com.bluntsoftware.ludwig.conduit.activities.output;


import com.bluntsoftware.ludwig.conduit.impl.ActivityImpl;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.schema.PropertyFormat;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
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
    public JsonSchema getSchema() {
        JsonSchema schema = JsonSchema.builder().title(this.getName()).build();

        List<String> outType = new ArrayList<String>();
        outType.add("json");
        outType.add("html");
        outType.add("xml");
        outType.add("file");

        List<String> outMethod = new ArrayList<String>();
        outMethod.add("stream");
        outMethod.add("download");

        schema.addEnum("output_method",outMethod ,"stream");
        schema.addEnum("output_type",outType ,"json");
        schema.addString("file","file-location", PropertyFormat.IMAGE_CHOOSER);

        PropertyFormat payloadFormat = PropertyFormat.JSON;
        Map<String,Object> schemaMap = schema.getValue();
        if(schemaMap != null && schemaMap.containsKey("output_type") && schemaMap.get("output_type") != null) {
            Object outputType = schemaMap.get("output_type");
            if(outputType instanceof String) {
                payloadFormat = PropertyFormat.fromValue(outputType.toString());
            }
        }

        schema.addString("payload","{}",payloadFormat);

        return schema;
    }
}
