package com.bluntsoftware.ludwig.conduit.activities.input.domain;


import com.bluntsoftware.ludwig.conduit.utils.schema.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class InputSettings implements EntitySchema {

    String hold;
    String flow_request_log;

    String flow;
    String requested;
    Map<String,Object> headers;

    public JsonSchema getSchema() {
        Map<String, Property> props = new HashMap<>();
        String[] requestLog = {"Log and Save","None"};
        String[] trueFalse = {"true","false"};
        props.put("flow_request_log", EnumProperty.builder().enumeration(requestLog).defaultValue("None").title("Flow Request Log").build());
        props.put("hold", EnumProperty.builder().enumeration(trueFalse).title("Hold").defaultValue("false").build());
        //hidden
        props.put("flow", StringProperty.builder().type("hidden").build());
        props.put("requested", StringProperty.builder().type("hidden").build());
        props.put("headers", JsonSchema.builder().title("headers").hidden(true).build());
        return JsonSchema.builder()
                .title("input")
                .properties(props)
                .build();
    }

}
