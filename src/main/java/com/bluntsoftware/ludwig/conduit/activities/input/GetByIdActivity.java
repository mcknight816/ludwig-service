package com.bluntsoftware.ludwig.conduit.activities.input;

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
        JsonSchema schema = super.getSchema();
        schema.addString("id","");
        return schema;
    }

    @Override
    public String getIcon() {
        return "fa-edit";
    }
}
