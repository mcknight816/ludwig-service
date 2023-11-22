package com.bluntsoftware.ludwig.service;

import com.bluntsoftware.ludwig.conduit.Activity;
import com.bluntsoftware.ludwig.conduit.activities.input.DeleteActivity;
import com.bluntsoftware.ludwig.conduit.activities.input.GetActivity;
import com.bluntsoftware.ludwig.conduit.activities.input.GetByIdActivity;
import com.bluntsoftware.ludwig.conduit.activities.input.PostActivity;
import com.bluntsoftware.ludwig.conduit.activities.mongo.MongoDeleteActivity;
import com.bluntsoftware.ludwig.conduit.activities.mongo.MongoFindActivity;
import com.bluntsoftware.ludwig.conduit.activities.mongo.MongoGetActivity;
import com.bluntsoftware.ludwig.conduit.activities.mongo.MongoSaveActivity;
import com.bluntsoftware.ludwig.conduit.activities.mongo.domain.MongoSettings;
import com.bluntsoftware.ludwig.conduit.activities.output.HttpResponseActivity;
import com.bluntsoftware.ludwig.conduit.config.model.PayloadSchemaConfig;
import com.bluntsoftware.ludwig.conduit.config.nosql.MongoConnectionConfig;
import com.bluntsoftware.ludwig.conduit.config.nosql.domain.MongoConnection;
import com.bluntsoftware.ludwig.domain.Flow;
import com.bluntsoftware.ludwig.domain.FlowActivity;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FlowTemplateService {

    private final ActivityConfigRepository activityConfigRepository;

    public FlowTemplateService(ActivityConfigRepository activityConfigRepository) {
        this.activityConfigRepository = activityConfigRepository;
    }

    Flow createFlow(){
       return Flow.builder().build();
    }

    Flow createMongoCrudFlow(String name, String connection, String database, String collection) {
        List<FlowActivity> activities = new ArrayList<>();

        MongoConnectionConfig mongoConnectionConfig = new MongoConnectionConfig();

        MongoSettings mongoSettings = MongoSettings.builder().collection(collection).database(database).connection(connection).build();

        //Post - Save
        activities.add(flowActivity(new PostActivity(new PayloadSchemaConfig(),activityConfigRepository)));
        activities.add(flowActivity(new MongoSaveActivity(mongoConnectionConfig,activityConfigRepository)));
        activities.add(flowActivity(new HttpResponseActivity(activityConfigRepository)));

        //Get - Find
        activities.add(flowActivity(new GetActivity(activityConfigRepository)));
        activities.add(flowActivity(new MongoFindActivity(mongoConnectionConfig,activityConfigRepository)));
        activities.add(flowActivity(new HttpResponseActivity(activityConfigRepository)));

        //Get By id - Find By id
        activities.add(flowActivity(new GetByIdActivity(activityConfigRepository)));
        activities.add(flowActivity(new MongoGetActivity(mongoConnectionConfig,activityConfigRepository)));
        activities.add(flowActivity(new HttpResponseActivity(activityConfigRepository)));

        //Delete By id
        activities.add(flowActivity(new DeleteActivity(activityConfigRepository)));
        activities.add(flowActivity(new MongoDeleteActivity(mongoConnectionConfig,activityConfigRepository)));
        activities.add(flowActivity(new HttpResponseActivity(activityConfigRepository)));

        return Flow.builder()
                .name(name)
                .activities(activities)
                .build();
    }

    FlowActivity flowActivity(Activity activity){
        return FlowActivity.builder()
                .activityClass(activity.getClass().getName())
                .input(activity.getInput())
                .output(activity.getOutput())
                .name(activity.getName())
                .x(0)
                .y(0)
                .category(activity.getCategory())
                .icon(activity.getIcon())
                .id(UUID.randomUUID().toString())
                .build();
    }

    public static void main(String[] args) {
        FlowTemplateService service = new FlowTemplateService(null);
        log.info("{}",service.createMongoCrudFlow("Test Flow","Mongo Default","test-db","customer"));
        log.info("{}",service.createMongoCrudFlow("Wow Flow","Mongo Default","test-db","customer"));
    }



}
