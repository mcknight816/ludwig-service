package com.bluntsoftware.ludwig.conduit.activities.mongo;

import com.bluntsoftware.ludwig.conduit.activities.mongo.domain.MongoSave;
import com.bluntsoftware.ludwig.conduit.activities.mongo.domain.MongoSettings;
import com.bluntsoftware.ludwig.conduit.config.nosql.MongoConnectionConfig;
import com.bluntsoftware.ludwig.conduit.config.nosql.domain.MongoConnection;
import com.bluntsoftware.ludwig.domain.Config;
import com.bluntsoftware.ludwig.domain.FlowConfig;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.ConcurrentModel;

import java.util.HashMap;
import java.util.Map;
import static org.mockito.ArgumentMatchers.anyString;
@Slf4j
@ExtendWith(SpringExtension.class)
@Scope("test")
class MongoSaveActivityTest {
    @MockBean
    MongoConnectionConfig mongoConnectionConfig;
    @MockBean
    ActivityConfigRepository activityConfigRepository;

    Map<String,Object> payload;
    @BeforeEach
    void setUp() {
        this.payload = new HashMap<>();
        this.payload.put("name","Alex");

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getSchema() {
    }

    @Test
    void getInput() {


    }

   /* @Test
    void run() throws Exception {
         MongoSave mongoSave = MongoSave.builder().payload(this.payload)
                .settings(MongoSettings.builder().connection("Test").collection("unit-test").database("test").build()).build();

         MongoConnection mongoConnection = MongoConnection.builder()
                .server("localhost")
                .port("27017")
                .build();

         ObjectMapper mapper = new ObjectMapper();

        ConcurrentModel config = mapper.convertValue(mongoConnection,ConcurrentModel.class);
        ConcurrentModel input = mapper.convertValue(mongoSave,ConcurrentModel.class);
        Mockito.when(activityConfigRepository.findByNameAndConfigClass(anyString(),anyString())).thenReturn(FlowConfig.builder()
                        .configClass(MongoConnectionConfig.class.getTypeName())
                .config(config).build());

        MongoSaveActivity mongoSaveActivity = new MongoSaveActivity(this.mongoConnectionConfig,this.activityConfigRepository);

       log.info("{}", mongoSaveActivity.run(input));
    }*/

    @Test
    void getOutput() {
    }
}