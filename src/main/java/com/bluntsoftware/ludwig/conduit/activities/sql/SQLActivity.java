package com.bluntsoftware.ludwig.conduit.activities.sql;

import com.bluntsoftware.ludwig.conduit.config.sql.SQLConfigActivity;
import com.bluntsoftware.ludwig.conduit.activities.ActivityImpl;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.PropertyFormat;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import org.springframework.stereotype.Service;
import java.util.Map;
/**
 * Created by Alex Mcknight on 1/24/2017.
 */
@Service
public class SQLActivity extends ActivityImpl {
    private final SQLConfigActivity sqlConnectionConfig;

    public SQLActivity(SQLConfigActivity sqlConnectionConfig, ActivityConfigRepository activityConfigRepository) {
        super(activityConfigRepository);
        this.sqlConnectionConfig = sqlConnectionConfig;
    }

    @Override
    public JsonSchema getJsonSchema() {
        JsonSchema schema = JsonSchema.builder().title("SQL Properties").build();
        schema.addConfig(sqlConnectionConfig);
        schema.addString("sql","select * from table", PropertyFormat.SQL);
        return schema;
    }

    @Override
    public Map<String, Object> run(Map<String, Object> input) {

       // System.out.println("SQL Activity " + input);

        return null;
    }

    public static void main(String[] args) {
        SQLActivity activity = new SQLActivity(new SQLConfigActivity( ),null);
        System.out.println(activity.getInput());
        System.out.println(activity.getJsonSchema().getJson());
    }

    @Override
    public String getIcon() {
        return "fa-database";
    }
}
