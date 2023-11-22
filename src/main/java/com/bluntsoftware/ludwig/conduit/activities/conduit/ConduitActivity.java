package com.bluntsoftware.ludwig.conduit.activities.conduit;



import com.bluntsoftware.ludwig.conduit.impl.ActivityImpl;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import com.bluntsoftware.ludwig.domain.FlowActivity;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class ConduitActivity extends ActivityImpl {

    public ConduitActivity(ActivityConfigRepository activityConfigRepository) {
        super(activityConfigRepository);
    }

    @Override
    public JsonSchema getSchema() {
        JsonSchema schema = new JsonSchema("ForEach");
        schema.addString("Flow Name","flow","",null,false);
        schema.addString("Context Name","context",null,null,false);
        List<String> type = new ArrayList<String>();
        type.add("post");
        type.add("get");
        type.add("delete");
        schema.addEnum("type",type ,"post");

        schema.addString("Payload","payload",null,null,true);
        schema.addString("Unique Identifier","id",null,null,true);
        schema.addString("For Each Item in Payload","foreach",null,null,true);
        return schema;
    }
    @Override
    public Map<String, Object> getOutput() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("payload", new ArrayList());
        return ret;
    }
    @Override
    public Map<String, Object> run(Map<String, Object> input) throws Exception {
        Object flow = input.get("flow");
        Object context = input.get("context");
        Object payload = input.get("payload");
        Object foreach = input.get("foreach");

        String contextName = null;
        if(context != null){
            contextName = context.toString();
            if(contextName.equalsIgnoreCase("")){
               contextName = null;
            }
        }

        Map<String,Object> msg = new HashMap<>();
        if(payload != null && payload instanceof Map){
            msg = (Map<String,Object>)payload;
        }else if(payload != null &&  payload instanceof String){
            try{
                ObjectMapper mapper = new ObjectMapper();
                msg = mapper.readValue(payload.toString(), Map.class);
            }catch (Exception e){
                msg.put("msg",payload);
            }
        }
        Map<String,Object> ret = new HashMap<>();

        List list = new ArrayList();
        if(foreach != null && foreach instanceof Map){
            Map data = (Map)foreach;
            for(Object key:data.keySet()){
                Object obj = data.get(key);
                if(obj instanceof Map){
                    msg.putAll((Map)obj);
          //          Map<String,Object> res = response(conduitService.post(flow.toString(),contextName,msg));
          //          list.add(res.get("data"));
                }
            }
        }else if(foreach != null &&  foreach instanceof List){
            List data = (List)foreach;
            for(Object obj:data){
                if(obj instanceof Map){
                    msg.putAll((Map)obj);
             //       Map<String,Object> res = response(conduitService.post(flow.toString(),contextName,msg));
             //       list.add(res.get("data"));
                }
            }
        }else{
           // return response(conduitService.post(flow.toString(),contextName,msg));
        }
        ret.put("payload",list);
        return ret;

    }
    Map<String,Object> response(List<FlowActivity> activities){
        Map<String,Object> result = new HashMap<>();
        List<Map<String,Object>> mapList = new ArrayList<>();
        for(FlowActivity flowActivity:activities) {
            if (flowActivity.getActivityClass().equalsIgnoreCase("com.bluntsoftware.lib.conduit.activities.output.HttpResponseActivity")) {
                return flowActivity.getInput();
            }
            try{
                ObjectMapper mapper = new ObjectMapper();
                mapList.add(mapper.convertValue(flowActivity, Map.class));
            }catch(Exception e){

            }
        }
        result.put("data",mapList);
        return result;
    }
    @Override
    public String getIcon() {
        return "fa-gears";
    }
}
