package com.bluntsoftware.ludwig.service.template;

import com.bluntsoftware.ludwig.conduit.Activity;
import com.bluntsoftware.ludwig.conduit.activities.input.DeleteActivity;
import com.bluntsoftware.ludwig.conduit.activities.input.GetActivity;
import com.bluntsoftware.ludwig.conduit.activities.input.GetByIdActivity;
import com.bluntsoftware.ludwig.conduit.activities.input.PostActivity;
import com.bluntsoftware.ludwig.conduit.activities.mongo.MongoDeleteActivity;
import com.bluntsoftware.ludwig.conduit.activities.mongo.MongoFindActivity;
import com.bluntsoftware.ludwig.conduit.activities.mongo.MongoGetActivity;
import com.bluntsoftware.ludwig.conduit.activities.mongo.MongoSaveActivity;
import com.bluntsoftware.ludwig.conduit.activities.mongo.domain.*;
import com.bluntsoftware.ludwig.conduit.activities.output.HttpResponseActivity;
import com.bluntsoftware.ludwig.conduit.config.model.PayloadSchemaConfig;
import com.bluntsoftware.ludwig.conduit.config.nosql.MongoConnectionConfig;
import com.bluntsoftware.ludwig.domain.*;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
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
        return mapper.convertValue(obj, ConcurrentModel.class);
    }

    void buildMongoSave(Flow flow, MongoSettings mongoSettings, Integer x,ActivityConfigRepository activityConfigRepository) {

        MongoSave mongoSave = MongoSave.builder().settings(mongoSettings).payload(new HashMap<>()).build();

        FlowActivity postActivity = flowActivity(x,20,new PostActivity(new PayloadSchemaConfig(),activityConfigRepository));
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
    private void buildMongoFind(Flow flow,MongoSettings mongoSettings,Integer x,ActivityConfigRepository activityConfigRepository) {

        MongoFind mongoFind = MongoFind.builder().query(DBQuery.builder().build()).settings(mongoSettings).build();

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

    private void buildMongoGetById(Flow flow,MongoSettings mongoSettings,Integer x, ActivityConfigRepository activityConfigRepository){

        MongoById byId = MongoById.builder().settings(mongoSettings).build();
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
    private void buildMongoDeleteById(Flow flow,MongoSettings mongoSettings,Integer x, ActivityConfigRepository activityConfigRepository){

        MongoById byId = MongoById.builder().settings(mongoSettings).build();
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

    public Flow createMongoCrudFlow(String name, MongoSettings mongoSettings,ActivityConfigRepository activityConfigRepository) {

        Flow flow = Flow.builder()
                .name(name)
                .connections( new ArrayList<>())
                .connectionMaps( new ArrayList<>())
                .activities( new ArrayList<>())
                .build();

        buildMongoSave(flow,mongoSettings,30,activityConfigRepository);
        buildMongoFind(flow,mongoSettings,130,activityConfigRepository);
        buildMongoGetById(flow,mongoSettings,230,activityConfigRepository);
        buildMongoDeleteById(flow,mongoSettings,330,activityConfigRepository);

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
