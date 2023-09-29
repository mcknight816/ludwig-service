package com.bluntsoftware.ludwig.service;
import com.bluntsoftware.ludwig.conduit.Activity;
import com.bluntsoftware.ludwig.conduit.activities.input.InputActivity;
import com.bluntsoftware.ludwig.conduit.activities.input.PostActivity;
import com.bluntsoftware.ludwig.conduit.impl.ActivityImpl;
import com.bluntsoftware.ludwig.conduit.schema.JsonPath;
import com.bluntsoftware.ludwig.conduit.utils.SecurityUtils;
import com.bluntsoftware.ludwig.domain.Flow;
import com.bluntsoftware.ludwig.domain.FlowActivity;
import com.bluntsoftware.ludwig.repository.ActivityRepository;
import com.bluntsoftware.ludwig.repository.FlowRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Synchronized;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.util.*;

@Service("FlowRunnerService")
public class FlowRunnerService {

    private final FlowRepository flowRepository;
    private final ActivityRepository activityRepository;
    public FlowRunnerService(FlowRepository flowRepository, ActivityRepository activityRepository) {
        this.flowRepository = flowRepository;
        this.activityRepository = activityRepository;
    }


    public FlowActivity getFlowActivityByID(Flow flow,String flowActivityId){
        return flow.getActivities().stream()
                .filter(f->f.getId().equalsIgnoreCase(flowActivityId))
                .findAny().orElseGet(null);
    }

    public List<FlowActivity> runWithInput(Flow flow,Map<String,Object> input,String context) {
        return runWithActivityClass(flow, InputActivity.class.getName(),input,context);
    }

    void run(Flow flow,FlowActivity activity,List<FlowActivity> flowActivities){

    }
    public Map<String,Object> getOutputFlowData(String flowName){
        Date requested = new Date();
        Map<String,Object> in = new HashMap<>();
        in.put("flow",flowName);
        in.put("requested",requested);
        in.put("user", SecurityUtils.getUserInfo());
        return in;
    }

    public Mono<Flow> getOrCreateInputFlow(String flowName, Activity activity, Integer x, Integer y, Map<String,Object> input, String context) throws InsufficientAuthenticationException {
        if(flowName == null || flowName.equalsIgnoreCase("")){
            return null;
        }
        Flow flow = flowRepository.getByName(flowName).block();
        if(flow == null){
            flow = Flow.builder()
                    .name(flowName)
                    .connections(List.of())
                    .connectionMaps(List.of())
                    .activities(List.of())
                    .build();
            //flow.setFlowListenerService(flowListenerService); //What is this Listener ?
        }
        String classname = activity.getClass().getName();
        FlowActivity flowActivity;
        if(context != null){
            flowActivity =  getContextByClassName(flow,classname,context);
        }else{
            flowActivity =  getFirstByClassName(flow,classname);
        }

        if(!flow.getLocked()){
            if(flowActivity == null){
                flowActivity = createFlowActivity(activity);
                flowActivity.setContext(context);
                flowActivity.setX(x);
                flowActivity.setY(y);
                flow.getActivities().add(flowActivity);
            }
            flowActivity.getInput().putAll(input);
            Map<String, Object> out = flowActivity.getOutput();
            if(out == null){
                out = new HashMap<>();
            }
            out.putAll(input);
            flowActivity.setOutput(out);
        }
/*
        if(!isAuthorised(flowActivity)){
            String authorizedRole = (String)flowActivity.getInput().get("authorized_role");
            throw new InsufficientAuthenticationException("Conduit Access Denied (Role must be " + authorizedRole + " or higher.)" );
        }
*/
        return flowRepository.save(flow);

    }
    @JsonIgnore
    public FlowActivity getContextByClassName(Flow flow,String activityClass,String context){
        List<FlowActivity> flowActivities = listByClass(flow,activityClass);
        if(context != null && !context.equalsIgnoreCase("")){
            for(FlowActivity flowActivity:flowActivities){
                String flowContext = flowActivity.getContext();
                if(flowContext != null && !flowContext.equalsIgnoreCase("")){
                    if(flowContext.equalsIgnoreCase(context)){
                        return flowActivity;
                    }
                }
            }
        }
        return null;
    }
    public List<FlowActivity> listByClass(Flow flow,String activityClass){
        List<FlowActivity> ret = new ArrayList<>();
        for(FlowActivity flowActivity:flow.getActivities()){
            String flowActivityClass = flowActivity.getActivityClass();
            if(flowActivityClass != null && flowActivityClass.equalsIgnoreCase(activityClass)){
                ret.add(flowActivity);
            }
        }
        return ret;
    }

    public FlowActivity createFlowActivity(Activity activity){
        return FlowActivity.builder()
                .icon(activity.getIcon())
                .input(activity.getInput())
                .category(activity.getCategory())
                .name(activity.getName())
                .output(activity.getOutput())
                .activityClass(activity.getActivityClass())
                .id(UUID.randomUUID().toString())
                .build();
    }

    public FlowActivity getFirstByClassName(Flow flow,String activityClass){
        List<FlowActivity> flowActivities = listByClass(flow,activityClass);
        if(flowActivities.size() > 0){
            return flowActivities.get(0);
        }
        return null;
    }
    public Map<String,Object> getInputFlowData(){
        Map<String,Object> in = new HashMap<>();
        in.put("user", SecurityUtils.getUserInfo());
        return in;
    }
    public Map<String,Object> transform(Map<String,Object> data){
        Map<String,Object> ret = new HashMap<>();
        for(String key:data.keySet()){
            Object value = data.get(key);
            JsonPath.createValue(ret,key,value);
        }
        return ret;
    }

    public List<FlowActivity> handlePost(String flowName, String context, Map<String,Object> input){
        InputActivity activity = (InputActivity)activityRepository.getByKlass(PostActivity.class.getName());
        Map<String,Object> in = getInputFlowData();
        in.put("payload",transform(input));
        Flow flow  = getOrCreateInputFlow(flowName,activity,50,70,in,context).block();
        return run(flow,activity,in,context);
    }

    private List<FlowActivity> run(Flow flow, Activity activity, Map<String, Object> input, String context){
        if(flow != null){
            return  runWithActivityClass(flow,activity.getActivityClass(),input,context);
        }
        return null;
    }
    List<FlowActivity> runWithActivityClass(Flow flow, String activityClass, Map<String, Object> input, String context) {
        if(context == null){
            return runWithActivity(flow,getFirstByClassName(flow,activityClass),input);
        }else{
            return runWithActivity(flow,getContextByClassName(flow,activityClass,context),input);
        }
    }
    private List<FlowActivity> runWithActivityId(Flow flow,String flowActivityId, Map<String, Object> input) {
        return runWithActivity(flow,getFlowActivityByID(flow,flowActivityId),input);
    }

    private List<FlowActivity> runWithActivity(Flow flow,FlowActivity flowActivity, Map<String, Object> input) {
        flowActivity.getInput().putAll(input);
        return run(flow,flowActivity);
    }

    @Synchronized
    public List<FlowActivity> run(Flow flow, FlowActivity flowActivity) {
        //Add User Info to input flow data
        if( ActivityImpl.getByClassName(flowActivity.getActivityClass()) instanceof InputActivity){
            Map<String, Object> in = flowActivity.getInput();
            in.putAll( getOutputFlowData(flow.getName()));
        }
        List<FlowActivity> flowActivities = new ArrayList<>();
        run(flow,flowActivity,flowActivities);
    //    flow.broadcastActivityList(flowActivities);

        return flowActivities;
    }

}
