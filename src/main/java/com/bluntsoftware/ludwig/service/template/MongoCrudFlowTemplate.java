package com.bluntsoftware.ludwig.service.template;

import com.bluntsoftware.ludwig.conduit.activities.Activity;
import com.bluntsoftware.ludwig.conduit.activities.input.DeleteActivity;
import com.bluntsoftware.ludwig.conduit.activities.input.GetActivity;
import com.bluntsoftware.ludwig.conduit.activities.input.GetByIdActivity;
import com.bluntsoftware.ludwig.conduit.activities.input.PostActivity;
import com.bluntsoftware.ludwig.conduit.activities.input.domain.InputSettings;
import com.bluntsoftware.ludwig.conduit.activities.input.domain.PostInput;
import com.bluntsoftware.ludwig.conduit.activities.mongo.MongoDeleteActivity;
import com.bluntsoftware.ludwig.conduit.activities.mongo.MongoFindActivity;
import com.bluntsoftware.ludwig.conduit.activities.mongo.MongoGetActivity;
import com.bluntsoftware.ludwig.conduit.activities.mongo.MongoSaveActivity;
import com.bluntsoftware.ludwig.conduit.activities.mongo.domain.*;
import com.bluntsoftware.ludwig.conduit.activities.output.HttpResponseActivity;
import com.bluntsoftware.ludwig.domain.*;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.ConcurrentModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
@Slf4j
public class MongoCrudFlowTemplate {

    Map<String,Object> toMap(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.convertValue(obj, ConcurrentModel.class);
    }

    void buildMongoSave(Flow flow, MongoCrudSettings mongoSettings, Integer x,ActivityConfigRepository activityConfigRepository) {

        MongoSave mongoSave = MongoSave.builder().settings(mongoSettings.getSettings()).payload(new HashMap<>()).build();
        PostInput postInput = PostInput.builder()
                .settings(InputSettings.builder()
                        .hold("false")
                        .flow_request_log("None")
                        .build())
                .payloadSchema(mongoSettings.getPayloadSchema()).build();

        FlowActivity postActivity = flowActivity(x,20,new PostActivity( activityConfigRepository),toMap(postInput));
        FlowActivity mongoSaveActivity = flowActivity(x,120,new MongoSaveActivity(activityConfigRepository),toMap(mongoSave));
        FlowActivity httpResponseActivity = flowActivity(x,220,new HttpResponseActivity(activityConfigRepository));

        Connection postSaveConnection = connect(postActivity,mongoSaveActivity);
        Connection saveResponseConnection = connect(mongoSaveActivity,httpResponseActivity);
        flow.getConnections().add(postSaveConnection);
        flow.getConnections().add(saveResponseConnection);

         flow.getConnectionMaps().add(ConnectionMap.builder()
                 .src("['" + postActivity.getId() + "']['output']['payload']")
                 .tgt("['" + mongoSaveActivity.getId() + "']['input']['payload']").build());

        flow.getConnectionMaps().add(ConnectionMap.builder()
                .src("['" + mongoSaveActivity.getId() + "']['output']")
                .tgt("['" + httpResponseActivity.getId() + "']['input']['payload']").build());

        //Post - Save
        flow.getActivities().add(postActivity);
        flow.getActivities().add(mongoSaveActivity);
        flow.getActivities().add(httpResponseActivity);
    }
    private void buildMongoFind(Flow flow,MongoCrudSettings mongoSettings,Integer x,ActivityConfigRepository activityConfigRepository) {

        MongoFind mongoFind = MongoFind.builder().query(DBQuery.builder().build()).settings(mongoSettings.getSettings()).build();

        FlowActivity getActivity = flowActivity(x,20,new GetActivity(activityConfigRepository));
        FlowActivity findActivity = flowActivity(x,120,new MongoFindActivity(activityConfigRepository),toMap(mongoFind));
        FlowActivity httpResponseActivity = flowActivity(x,220,new HttpResponseActivity(activityConfigRepository));

        flow.getConnections().add(connect(getActivity,findActivity));
        flow.getConnections().add(connect(findActivity,httpResponseActivity));

        flow.getConnectionMaps().add(ConnectionMap.builder()
                .src("['" + getActivity.getId() + "']['output']['payload']")
                .tgt("['" + findActivity.getId() + "']['input']['query']").build());

        flow.getConnectionMaps().add(ConnectionMap.builder()
                .src("['" + findActivity.getId() + "']['output']")
                .tgt("['" + httpResponseActivity.getId() + "']['input']['payload']").build());
        //Get - Find
        flow.getActivities().add(getActivity);
        flow.getActivities().add(findActivity);
        flow.getActivities().add(httpResponseActivity);
    }

    private void buildMongoGetById(Flow flow,MongoCrudSettings mongoSettings,Integer x, ActivityConfigRepository activityConfigRepository){

        MongoById byId = MongoById.builder().settings(mongoSettings.getSettings()).build();
        FlowActivity getByIdActivity = flowActivity(x,20,new GetByIdActivity(activityConfigRepository));
        FlowActivity mongoGetActivity = flowActivity(x,120,new MongoGetActivity(activityConfigRepository),toMap(byId));
        FlowActivity httpResponseActivity = flowActivity(x,220,new HttpResponseActivity(activityConfigRepository));
        //connections
        flow.getConnections().add(connect(getByIdActivity,mongoGetActivity));
        flow.getConnections().add(connect(mongoGetActivity,httpResponseActivity));
        //connection maps
        flow.getConnectionMaps().add(ConnectionMap.builder()
                .src("['" + getByIdActivity.getId() + "']['output']['id']")
                .tgt("['" + mongoGetActivity.getId() + "']['input']['id']").build());

        flow.getConnectionMaps().add(ConnectionMap.builder()
                .src("['" + mongoGetActivity.getId() + "']['output']")
                .tgt("['" + httpResponseActivity.getId() + "']['input']['payload']").build());

        //Get By id - Find By id
        flow.getActivities().add(getByIdActivity);
        flow.getActivities().add(mongoGetActivity);
        flow.getActivities().add(httpResponseActivity);
    }
    private void buildMongoDeleteById(Flow flow,MongoCrudSettings mongoSettings,Integer x, ActivityConfigRepository activityConfigRepository){

        MongoById byId = MongoById.builder().settings(mongoSettings.getSettings()).build();
        FlowActivity deleteByIdActivity = flowActivity(x,20,new DeleteActivity(activityConfigRepository));
        FlowActivity mongoDeleteActivity = flowActivity(x,120,new MongoDeleteActivity(activityConfigRepository),toMap(byId));
        FlowActivity httpResponseActivity = flowActivity(x,220,new HttpResponseActivity(activityConfigRepository));

        flow.getConnections().add(connect(deleteByIdActivity,mongoDeleteActivity));
        flow.getConnections().add(connect(mongoDeleteActivity,httpResponseActivity));
        flow.getConnectionMaps().add(ConnectionMap.builder()
                .src("['" + deleteByIdActivity.getId() + "']['output']['id']")
                .tgt("['" + mongoDeleteActivity.getId() + "']['input']['id']").build());

        flow.getConnectionMaps().add(ConnectionMap.builder()
                .src("['" + mongoDeleteActivity.getId() + "']['output']")
                .tgt("['" + httpResponseActivity.getId() + "']['input']['payload']").build());
        //Delete By id
        flow.getActivities().add(deleteByIdActivity);
        flow.getActivities().add(mongoDeleteActivity);
        flow.getActivities().add(httpResponseActivity);
    }

    public static String getType(){
        return "Mongo Crud Flow";
    }

    public static FlowTemplate getFlowTemplate(){

        return FlowTemplate.builder()
                .name(getType())
                .type(getType())
                .schema(MongoCrudSettings.getSchema())
                .build();
    }

    public static Flow createFlow(String name, FlowTemplate template, ActivityConfigRepository activityConfigRepository) {

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MongoCrudSettings mongoCrudSettings = mapper.convertValue(template.getContext(),MongoCrudSettings.class );


        Flow flow = Flow.builder()
                .name(name)
                .connections( new ArrayList<>())
                .connectionMaps( new ArrayList<>())
                .activities( new ArrayList<>())
                .build();

        MongoCrudFlowTemplate mongoCrudFlowTemplate = new MongoCrudFlowTemplate();

        mongoCrudFlowTemplate.buildMongoSave(flow,mongoCrudSettings,30,activityConfigRepository);
        mongoCrudFlowTemplate.buildMongoFind(flow,mongoCrudSettings,130,activityConfigRepository);
        mongoCrudFlowTemplate.buildMongoGetById(flow,mongoCrudSettings,230,activityConfigRepository);
        mongoCrudFlowTemplate.buildMongoDeleteById(flow,mongoCrudSettings,330,activityConfigRepository);

        return flow;
    }

    Connection connect(FlowActivity src,FlowActivity tgt){
        return Connection.builder().src(src.getId()).tgt(tgt.getId()).build();
    }
    FlowActivity flowActivity(Integer x, Integer y, Activity activity){
        return flowActivity(x,y,activity,activity.getInput());
    }
    FlowActivity flowActivity(Integer y,Integer x, Activity activity, Map<String,Object> input){
        return FlowActivity.builder()
                .activityClass(activity.getClass().getName())
                .input(input)
                .output(activity.getOutput())
                .name(activity.getName())
                .x(x)
                .y(y)
                .category(activity.getCategory())
                .icon(activity.getIcon())
                .id(UUID.randomUUID().toString())
                .build();
    }

}
