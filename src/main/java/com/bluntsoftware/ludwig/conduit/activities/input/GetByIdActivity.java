package com.bluntsoftware.ludwig.conduit.activities.input;

import com.bluntsoftware.ludwig.conduit.activities.mongo.domain.MongoById;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import org.springframework.stereotype.Service;

/**
 * Created by Alex Mcknight on 1/12/2017.
 */
@Service
public class GetByIdActivity extends InputActivity {
    public GetByIdActivity(ActivityConfigRepository activityConfigRepository) {
        super(activityConfigRepository);
    }


    @Override
    public JsonSchema getSchema() {
        return MongoById.getSchema();
    }

    @Override
    public String getIcon() {
        return "fa-edit";
    }
}
