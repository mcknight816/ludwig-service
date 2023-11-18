package com.bluntsoftware.ludwig.conduit.activities.input;



import com.bluntsoftware.ludwig.conduit.impl.ActivityImpl;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex Mcknight on 1/5/2017.
 */
public class InputActivity extends ActivityImpl {

    public InputActivity(ActivityConfigRepository activityConfigRepository) {
        super(activityConfigRepository);
    }

    @Override
    public JsonSchema getSchema() {
        //Config Parameters
        JsonSchema editor = new JsonSchema(this.getName());
       /* List<String> roles = new ArrayList<String>();
        roles.add("anonymous");
        roles.add("authenticated");
        roles.add("super user");
        roles.add("admin");
        editor.addEnum("authorized_role",roles,"authenticated");*/

        editor.addString("authorized_role","authenticated","roleChooser");

        List<String> requests = new ArrayList<>();
        requests.add("Log and Save");
        requests.add("None");
        editor.addEnum("Flow Request Log","flow_request_log",requests,"Log and Save");
        editor.addString("hold","false",null);

        //Hidden Parameters
        editor.addString("flow","",true);
        editor.addString("requested","",true);
        editor.addString("payload","",true);
/*
        RecordProperty user = new RecordProperty("");
        user.addString("role","",true);
        user.addString("email","",true);
        user.addString("first_name","",true);
        user.addString("last_name","",true);
        user.addString("user_name","",true);
        user.addString("company","",true);
        user.addString("tenant_id","",true);
        editor.addRecord("user",user); */

        return editor;
    }

    @Override
    public Map<String, Object> run(Map<String, Object> input)throws Exception {
        return input;
    }

    public String getEndpoint() {
        return "/";
    }

    @Override
    public String getIcon() {
        return "fa-hand-o-right";
    }

    @Override
    public String getCategory() {
        return Category.Input.name();
    }
}
