package com.bluntsoftware.ludwig.conduit.activities.input.domain;

import com.bluntsoftware.ludwig.conduit.config.queue.ActiveMQConfigActivity;
import com.bluntsoftware.ludwig.conduit.utils.schema.EntitySchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ListenerInput implements EntitySchema {
    String destination;
    String flow_request_log;
    String hold;

    @Override
    public JsonSchema getJsonSchema() {
        //Config Parameters
        JsonSchema editor = JsonSchema.builder().title("Event Listener").build();
       // editor.addConfigByClass(ActiveMQConfigActivity.class.getName());
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

        JsonSchema user = new JsonSchema("");
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
}
