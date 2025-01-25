package com.bluntsoftware.ludwig.conduit.activities.input;

import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
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
    public JsonSchema getJsonSchema() {
        JsonSchema schema = super.getJsonSchema();
        schema.addString("id","");
        return schema;
    }

    @Override
    public String getIcon() {
        return "fa-edit";
    }
}
