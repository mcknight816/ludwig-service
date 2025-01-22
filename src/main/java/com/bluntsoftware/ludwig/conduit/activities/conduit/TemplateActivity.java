package com.bluntsoftware.ludwig.conduit.activities.conduit;


import com.bluntsoftware.ludwig.conduit.activities.ActivityImpl;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.PropertyFormat;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex Mcknight on 2/19/2017.
 */
@Service
public class TemplateActivity extends ActivityImpl {

    public TemplateActivity(ActivityConfigRepository activityConfigRepository) {
        super(activityConfigRepository);
    }

    @Override
    public JsonSchema getSchema() {
        JsonSchema schema = JsonSchema.builder().title("Template").build();
        schema.addString("","context","",null,true);
        schema.addString("Template","template","hello {{name}}", PropertyFormat.HTML,false);
        return schema;
    }

    @Override
    public Map<String, Object> run(Map<String, Object> input) throws Exception {
        Map<String, Object> ret = new HashMap<>();
        StringReader reader = new StringReader((String)input.get("template"));
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(reader,"test");
        StringWriter writer = new StringWriter();
        mustache.execute(writer,input.get("context")).flush();
        ret.put("data", writer.toString());
        return ret;
    }

    @Override
    public Map<String, Object> getOutput() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("data","Data");
        return ret;
    }

    public static void main(String[] args) {
        Map<String, Object> context = new HashMap<>();
        context.put("name","Alex");
        Map<String, Object> input = new HashMap<>();

        input.put("template","Hello {{name}}");
        input.put("context",context);
        TemplateActivity templateActivity = new TemplateActivity(null);
        Map<String, Object> result = null;
        try {
            result = templateActivity.run(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(result);
    }
}
