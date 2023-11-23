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
import com.bluntsoftware.ludwig.domain.Connection;
import com.bluntsoftware.ludwig.domain.Flow;
import com.bluntsoftware.ludwig.domain.FlowActivity;
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

    void buildMongoSave(Flow flow, MongoSettings mongoSettings, MongoConnectionConfig mongoConnectionConfig, Integer x){

        MongoSave mongoSave = MongoSave.builder().settings(mongoSettings).payload(new HashMap<>()).build();

        FlowActivity postActivity = flowActivity(x,20,new PostActivity(new PayloadSchemaConfig(),null));
        FlowActivity mongoSaveActivity = flowActivity(x,70,new MongoSaveActivity(mongoConnectionConfig,null),toMap(mongoSave));
        FlowActivity httpResponseActivity = flowActivity(x,120,new HttpResponseActivity(null));

        Connection postSaveConnection = connect(postActivity,mongoSaveActivity);
        Connection saveResponseConnection = connect(mongoSaveActivity,httpResponseActivity);
        flow.getConnections().add(postSaveConnection);
        flow.getConnections().add(saveResponseConnection);

        //Post - Save
        flow.getActivities().add(postActivity);
        flow.getActivities().add(mongoSaveActivity);
        flow.getActivities().add(httpResponseActivity);
    }
    private void buildMongoFind(Flow flow,MongoSettings mongoSettings,MongoConnectionConfig mongoConnectionConfig,Integer x){

        MongoFind mongoFind = MongoFind.builder().query(DBQuery.builder().build()).settings(mongoSettings).build();
        //Get - Find
        flow.getActivities().add(flowActivity(x,20,new GetActivity(null)));
        flow.getActivities().add(flowActivity(x,70,new MongoFindActivity(mongoConnectionConfig,null),toMap(mongoFind)));
        flow.getActivities().add(flowActivity(x,120,new HttpResponseActivity(null)));
    }

    private void buildMongoGetById(Flow flow,MongoSettings mongoSettings,MongoConnectionConfig mongoConnectionConfig,Integer x){

        MongoById byId = MongoById.builder().settings(mongoSettings).build();
        //Get By id - Find By id
        flow.getActivities().add(flowActivity(x,20,new GetByIdActivity(null)));
        flow.getActivities().add(flowActivity(x,70,new MongoGetActivity(mongoConnectionConfig,null),toMap(byId)));
        flow.getActivities().add(flowActivity(x,120,new HttpResponseActivity(null)));
    }
    private void buildMongoDeleteById(Flow flow,MongoSettings mongoSettings,MongoConnectionConfig mongoConnectionConfig,Integer x){

        MongoById byId = MongoById.builder().settings(mongoSettings).build();
        //Delete By id
        flow.getActivities().add(flowActivity(x,20,new DeleteActivity(null)));
        flow.getActivities().add(flowActivity(x,70,new MongoDeleteActivity(mongoConnectionConfig,null),toMap(byId)));
        flow.getActivities().add(flowActivity(x,120,new HttpResponseActivity(null)));
    }

    public Flow createMongoCrudFlow(String name, String connection, String database, String collection) {

        Flow flow = Flow.builder()
                .name(name)
                .connections( new ArrayList<>())
                .connectionMaps( new ArrayList<>())
                .activities( new ArrayList<>())
                .build();

        MongoConnectionConfig mongoConnectionConfig = new MongoConnectionConfig();

        MongoSettings mongoSettings = MongoSettings.builder().collection(collection).database(database).connection(connection).build();

        buildMongoSave(flow,mongoSettings,mongoConnectionConfig,30);
        buildMongoFind(flow,mongoSettings,mongoConnectionConfig,80);
        buildMongoGetById(flow,mongoSettings,mongoConnectionConfig,130);
        buildMongoDeleteById(flow,mongoSettings,mongoConnectionConfig,180);

        return flow;
    }

    Connection connect(FlowActivity src,FlowActivity tgt){
        return Connection.builder().src(src.getId()).tgt(tgt.getId()).build();
    }
    FlowActivity flowActivity(Integer x, Integer y, Activity activity){
        return flowActivity(x,y,activity,activity.getInput());
    }
    FlowActivity flowActivity(Integer x,Integer y, Activity activity, Map<String,Object> input){
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

    public static void main(String[] args) {
        MongoCrudFlowTemplate service = new MongoCrudFlowTemplate();
         log.info("{}",service.createMongoCrudFlow("Test Flow","Mongo Default","test-db","customer"));

    }
}
