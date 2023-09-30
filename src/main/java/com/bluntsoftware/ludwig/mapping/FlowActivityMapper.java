package com.bluntsoftware.ludwig.mapping;

import com.bluntsoftware.ludwig.conduit.schema.JsonPath;
import com.bluntsoftware.ludwig.domain.ConnectionMap;
import com.bluntsoftware.ludwig.domain.ConnectionPath;
import com.bluntsoftware.ludwig.domain.FlowActivity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FlowActivityMapper {


    public static void mapFields(List<ConnectionMap> connectionMapList,FlowActivity tgtFlowActivity, List<FlowActivity> flowActivities ){
        Map<String,Object> tgt = tgtFlowActivity.getInput();
        if(tgt != null){
            getActivityConnectionMaps(connectionMapList,tgtFlowActivity).forEach(m->{
                String srcId = m.getSourcePath().getFlowActivityId();
                FlowActivity srcFlowActivity = flowActivities.stream()
                        .filter(f->f.getId().equalsIgnoreCase(srcId))
                        .findAny().orElse(null);//.orElseGet(null);
                if(srcFlowActivity != null){
                    Map<String,Object> src = srcFlowActivity.getOutput();
                    if(src != null){
                        if(m.getSourcePath().getFieldType() == ConnectionPath.FieldType.input){
                            src = srcFlowActivity.getInput();
                        }
                        JsonPath srcJson = new JsonPath(src);
                        JsonPath tgtJson = new JsonPath(tgt);
                        String key = m.getSourcePath().getPath();
                        Object val = srcJson.getValue(key);
                        String targetPath = m.getTargetPath().getPath();
                        tgtJson.setValue(targetPath,val);
                    }
                }
            });
        }
    }

    private static List<ConnectionMap> getActivityConnectionMaps(List<ConnectionMap> connectionMapList,FlowActivity flowActivity){
        return  connectionMapList.stream()
                .filter(c->c.getTargetPath().getFlowActivityId().equalsIgnoreCase(flowActivity.getId()))
                .collect(Collectors.toList());
    }

}
