package com.bluntsoftware.ludwig.conduit.activities.input;


import com.bluntsoftware.ludwig.conduit.config.queue.QConnectionConfig;
import com.bluntsoftware.ludwig.conduit.impl.ActivityImpl;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.schema.RecordProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class ListenerActivity extends ActivityImpl {

    @Autowired
    QConnectionConfig qConnectionConfig;

    @Override
    public JsonSchema getSchema() {
        //Config Parameters
        JsonSchema editor = new JsonSchema("Event Listener");
        editor.addConfig(qConnectionConfig);
        editor.addString("destination","dev.q.test",null);
        List<String> requests = new ArrayList<String>();
        requests.add("Log and Save");
        requests.add("None");
        editor.addEnum("Flow Request Log","flow_request_log",requests,"Log and Save");

        editor.addString("hold","false",null);

        //Hidden Parameters
        editor.addString("flow","",true);
        editor.addString("requested","",true);
        editor.addString("payload","",true);

        RecordProperty user = new RecordProperty("");
        user.addString("role","",true);
        user.addString("email","",true);
        user.addString("first_name","",true);
        user.addString("last_name","",true);
        user.addString("user_name","",true);
        user.addString("company","",true);
        user.addString("tenant_id","",true);
        editor.addRecord("user",user);

        return editor;
    }

    @Override
    public Map<String, Object> run(Map<String, Object> input)throws Exception {
        return input;
    }

    @Override
    public String getIcon() {
        return "fa-inbox";
    }

    @Override
    public String getCategory() {
        return Category.Input.name();
    }
}
