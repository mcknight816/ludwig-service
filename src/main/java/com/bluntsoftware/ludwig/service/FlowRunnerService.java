package com.bluntsoftware.ludwig.service;
import com.bluntsoftware.ludwig.conduit.Activity;
import com.bluntsoftware.ludwig.conduit.activities.input.*;
import com.bluntsoftware.ludwig.conduit.impl.ActivityImpl;
import com.bluntsoftware.ludwig.conduit.schema.JsonPath;
import com.bluntsoftware.ludwig.conduit.utils.SecurityUtils;
import com.bluntsoftware.ludwig.domain.Application;
import com.bluntsoftware.ludwig.domain.Flow;
import com.bluntsoftware.ludwig.domain.FlowActivity;
import com.bluntsoftware.ludwig.repository.ActivityRepository;
import com.bluntsoftware.ludwig.repository.ApplicationRepository;
import com.bluntsoftware.ludwig.repository.FlowRepository;
import com.bluntsoftware.ludwig.mapping.FlowActivityMapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Synchronized;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.util.*;
@Slf4j
@Service("FlowRunnerService")
public class FlowRunnerService {

    private final ApplicationRepository applicationRepository;
    private final FlowRepository flowRepository;
    private final ActivityRepository activityRepository;
    public FlowRunnerService(ApplicationRepository applicationRepository, FlowRepository flowRepository, ActivityRepository activityRepository) {
        this.applicationRepository = applicationRepository;
        this.flowRepository = flowRepository;
        this.activityRepository = activityRepository;
    }
    public void run(FlowActivity flowActivity) throws Exception {
        ActivityImpl activity = (ActivityImpl)activityRepository.getByKlass(flowActivity.getActivityClass());
        Map<String,Object> out = new HashMap<>();
        try{
            out = activity.run(flowActivity.getInput());
        }catch(Exception e){
            out.put("err",e.getMessage());
            flowActivity.setHasError(true);
        }
        flowActivity.setOutput(out);
    }
    private void run(Flow flow,FlowActivity flowActivity,List<FlowActivity> flowActivities){
        FlowRunnerService service = this;
        if(flowActivity != null && flowActivity.getId() != null){
            try {
                FlowActivityMapper.mapFields(flow.getConnectionMaps(),flowActivity,flowActivities);
                if(flowActivity.isFireAndForget()){
                    Thread t = new Thread(() -> {
                        try {
                            service.run(flowActivity);
                        }catch(Exception e){
                            //broadcastActivityError(flowActivity,e);
                        }
                    });
                    t.start();
                }else{
                    run(flowActivity);
                }
                flowActivities.add(flowActivity);
            }catch(Exception e){
              //  broadcastActivityError(flowActivity,e);
            }
        }
        getTargetFlowActivities(flow,flowActivity).forEach(t-> run(flow,t,flowActivities));
        log.info("{}", flowActivities);
    }
    private List<FlowActivity> getTargetFlowActivities(Flow flow,FlowActivity flowActivity) {
        List<FlowActivity> ret =  new ArrayList<>();
        flow.getConnections().stream()
                .filter(c->c.getSrc().equalsIgnoreCase(flowActivity.getId()))
                .forEach(c->{
                    FlowActivity fa = getFlowActivityByID(flow,c.getTgt());
                    if(fa != null){ret.add(fa);}
                });
        return ret;
    }

    public List<FlowActivity> handleGetColumns(String appPath, String flowName, String context){
        Application application = applicationRepository.findByPath(appPath).block();
        InputActivity activity = (InputActivity)activityRepository.getByKlass(ColumnsActivity.class.getName());
        Map<String,Object> in = new HashMap<>();
        Flow flow  = getOrCreateInputFlow(application,flowName,activity,50,140,in,context).block();
        return run(flow,activity,in,context);
    }

    public List<FlowActivity> handelGetById(String appPath, String flowName, String context, String id) {
        Application application = applicationRepository.findByPath(appPath).block();
        InputActivity activity = (InputActivity)activityRepository.getByKlass(GetByIdActivity.class.getName());
        Map<String,Object> in = new HashMap<>();
        in.put("id",id);
        Map<String,Object> payload = new HashMap<>();
        payload.put("id",id);
        in.put("payload",payload);
        Flow flow  = getOrCreateInputFlow(application,flowName,activity,50,140,in,context).block();
        return run(flow,activity,in,context);
    }

    public List<FlowActivity> handelDeleteById(String appPath, String flowName, String context, String id) {
        Application application = applicationRepository.findByPath(appPath).block();
        InputActivity activity = (InputActivity)activityRepository.getByKlass(DeleteActivity.class.getName());
        Map<String,Object> in = new HashMap<>();
        in.put("id",id);
        Map<String,Object> payload = new HashMap<>();
        payload.put("id",id);
        in.put("payload",payload);
        Flow flow  = getOrCreateInputFlow(application,flowName,activity,50,140,in,context).block();
        return run(flow,activity,in,context);
    }

    public List<FlowActivity> handleGet(String appPath, String flowName, String context,Map<String,Object> input) {
        Application application = applicationRepository.findByPath(appPath).block();
        InputActivity activity = (InputActivity)activityRepository.getByKlass(GetActivity.class.getName());
        Map<String,Object> in = new HashMap<>();
        in.put("payload",transform(input));
        Flow flow  = getOrCreateInputFlow(application,flowName,activity,50,140,in,context).block();
        return run(flow,activity,in,context);
    }

    public List<FlowActivity> handlePost(String appPath,String flowName, String context, Map<String,Object> input){
        Application application = applicationRepository.findByPath(appPath).block();
        InputActivity activity = (InputActivity)activityRepository.getByKlass(PostActivity.class.getName());
        Map<String,Object> in = new HashMap<>();
        in.put("user", SecurityUtils.getUserInfo());
        in.put("payload",transform(input));
        Flow flow  = getOrCreateInputFlow(application,flowName,activity,50,70,in,context).block();
        return run(flow,activity,in,context);
    }

    public FlowActivity getFlowActivityByID(Flow flow,String flowActivityId){
        return flow.getActivities().stream()
                .filter(f->f.getId().equalsIgnoreCase(flowActivityId))
                .findAny().orElseGet(null);
    }

    public List<FlowActivity> runWithInput(Flow flow,Map<String,Object> input,String context) {
        return runWithActivityClass(flow, InputActivity.class.getName(),input,context);
    }


    public Map<String,Object> getOutputFlowData(String flowName){
        Date requested = new Date();
        Map<String,Object> in = new HashMap<>();
        in.put("flow",flowName);
        in.put("requested",requested);
        in.put("user", SecurityUtils.getUserInfo());
        return in;
    }

    public Mono<Flow> getOrCreateInputFlow(Application application,String flowName, Activity activity, Integer x, Integer y, Map<String,Object> input, String context) throws InsufficientAuthenticationException {

        if(flowName == null || flowName.equalsIgnoreCase("")){
            return null;
        }
        Flow flow = application.getFlows().stream().filter(f -> f.getName().equalsIgnoreCase(flowName)).findFirst().orElse(null);
        if(flow == null){
            flow = Flow.builder().locked(false).name(flowName)
                    .activities(new ArrayList<>())
                    .connections(new ArrayList<>())
                    .connectionMaps(new ArrayList<>())
                    .id(UUID.randomUUID().toString())
                    .build();
            application.getFlows().add(flow);
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
        applicationRepository.save(application).block();
        return Mono.just(flow);
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

    public Map<String,Object> transform(Map<String,Object> data){
        Map<String,Object> ret = new HashMap<>();
        for(String key:data.keySet()){
            Object value = data.get(key);
            JsonPath.createValue(ret,key,value);
        }
        return ret;
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
