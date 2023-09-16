package com.bluntsoftware.ludwig.conduit.activities.sql;


import com.bluntsoftware.ludwig.conduit.config.sql.SQLConnectionConfig;
import com.bluntsoftware.ludwig.conduit.impl.ActivityImpl;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import org.springframework.stereotype.Service;
import java.util.Map;
/**
 * Created by Alex Mcknight on 1/24/2017.
 */
@Service
public class SQLActivity extends ActivityImpl {
    private final SQLConnectionConfig sqlConnectionConfig;

    public SQLActivity(SQLConnectionConfig sqlConnectionConfig) {
        this.sqlConnectionConfig = sqlConnectionConfig;
    }

    @Override
    public JsonSchema getSchema() {
        JsonSchema schema = new JsonSchema("SQL Properties");
        schema.addConfig(sqlConnectionConfig);
        schema.addString("sql","select * from table","sql");
        return schema;
    }

    @Override
    public Map<String, Object> run(Map<String, Object> input) {

       // System.out.println("SQL Activity " + input);

        return null;
    }

    public static void main(String[] args) {
        SQLActivity activity = new SQLActivity(new SQLConnectionConfig());
        System.out.println(activity.getInput());
        System.out.println(activity.getSchema().getJson());
    }

    @Override
    public String getIcon() {
        return "fa-database";
    }
}
