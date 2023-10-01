package com.bluntsoftware.ludwig.conduit.activities.conduit;


import com.bluntsoftware.ludwig.conduit.impl.ActivityImpl;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import org.springframework.stereotype.Service;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.HashMap;
import java.util.Map;

@Service
public class JavascriptActivity extends ActivityImpl {

    public JavascriptActivity(ActivityConfigRepository activityConfigRepository) {
        super(activityConfigRepository);
    }

    @Override
    public JsonSchema getSchema() {
        JsonSchema schema = new JsonSchema("Javascript Runner");
        schema.addString("","context","",null,true);
        schema.addString("Javascript","js","var run = function(context){\n \treturn context;\n};","javascript",false);
        return schema;
    }

    @Override
    public Map<String, Object> run(Map<String, Object> input) {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        Map<String, Object> ret = new HashMap<>();
        ret.put("payload", "");
        try {
            engine.eval(input.get("js").toString());
            Invocable invocable = (Invocable) engine;
            Object context = input.get("context");
            if(context == null || (context instanceof String && ((String)context).equalsIgnoreCase(""))){
                context = new HashMap<String,Object>();
            }
            Object result = invocable.invokeFunction("run", context);
            ret.put("payload", result);
        }catch(Exception e){
            String m = e.getMessage();
            ret.put("error", m);
        }
        return ret;
    }
    @Override
    public String getIcon() {
        return "fa-code";
    }


    public static void main(String[] args) {
        JavascriptActivity activity = new JavascriptActivity(null);
        HashMap<String,Object> in = new HashMap<>();
        in.put("js","var run = function(context){ return context;};");
        HashMap<String,Object> context = new HashMap<>();
        context.put("name","bluntsoftware");
        in.put("context",context);
        try {
            Map<String, Object> result =  activity.run(in);
            for(String key:result.keySet()){
                System.out.println(key + result.get(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
