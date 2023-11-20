package com.bluntsoftware.ludwig.conduit.activities.mongo;

import com.bluntsoftware.ludwig.conduit.activities.mongo.domain.MongoSave;
import com.bluntsoftware.ludwig.conduit.activities.mongo.domain.MongoSettings;
import com.bluntsoftware.ludwig.conduit.config.nosql.MongoConnectionConfig;
import com.bluntsoftware.ludwig.domain.Config;
import com.bluntsoftware.ludwig.domain.FlowConfig;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.HashMap;
import java.util.Map;
import static org.mockito.ArgumentMatchers.anyString;

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

    @Test
    void run() {
       /* MongoSave mongoSave = MongoSave.builder().payload(this.payload)
                .settings(MongoSettings.builder().connection("Test").collection("unit-test").database("test").build()).build();

        Mockito.when(mongoConnectionConfig.getDefaults()).thenReturn(Config.builder().configClass(MongoConnectionConfig.class.getTypeName()).build());
        Mockito.when(activityConfigRepository.findByNameAndConfigClass(anyString(),anyString())).thenReturn(FlowConfig.builder().build());
        MongoSaveActivity mongoSaveActivity = new MongoSaveActivity(this.mongoConnectionConfig,this.activityConfigRepository);
        mongoSaveActivity.run(mongoSave); */
    }

    @Test
    void getOutput() {
    }
}