package com.bluntsoftware.ludwig.conduit.activities.output;


import com.bluntsoftware.ludwig.conduit.impl.ActivityImpl;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import com.bluntsoftware.ludwig.repository.FlowConfigRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex Mcknight on 2/27/2017.
 */
@Service
public class HttpResponseActivity extends ActivityImpl {
    public HttpResponseActivity(FlowConfigRepository flowConfigRepository) {
        super(flowConfigRepository);
    }

    @Override
    public Map<String, Object> run(Map<String, Object> input)throws Exception {
        return null;
    }

    @Override
    public JsonSchema getSchema() {
        JsonSchema schema = new JsonSchema(this.getName());

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
        schema.addString("data","{}",schema.getValue().get("output_type").toString());

        return schema;
    }
}
