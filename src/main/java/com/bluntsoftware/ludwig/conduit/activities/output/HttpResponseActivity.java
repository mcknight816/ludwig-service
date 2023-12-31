package com.bluntsoftware.ludwig.conduit.activities.output;


import com.bluntsoftware.ludwig.conduit.impl.ActivityImpl;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
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
        schema.addString("file","file-location","imageChooser");
        schema.addString("payload","{}",schema.getValue().get("output_type").toString());

        return schema;
    }
}
