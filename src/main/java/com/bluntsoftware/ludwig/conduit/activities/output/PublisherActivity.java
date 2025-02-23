package com.bluntsoftware.ludwig.conduit.activities.output;

import com.bluntsoftware.ludwig.conduit.config.queue.ConnectionFactoryChooser;
import com.bluntsoftware.ludwig.conduit.config.queue.ActiveMQConfigActivity;
import com.bluntsoftware.ludwig.conduit.activities.ActivityImpl;
import com.bluntsoftware.ludwig.conduit.config.queue.domain.ActiveMQConfig;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.PropertyFormat;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PublisherActivity extends ActivityImpl {

    @Autowired
    ActiveMQConfigActivity qConnectionConfig;
    @Autowired
    ConnectionFactoryChooser connectionFactoryChooser;

    public PublisherActivity(ActivityConfigRepository activityConfigRepository) {
        super(activityConfigRepository);
    }

    @Override
    public JsonSchema getJsonSchema() {
        //Config Parameters
        JsonSchema editor = JsonSchema.builder().title("Event Publisher").build();
        List<String> type = new ArrayList<String>();
        type.add("Queue");
        type.add("Topic");
        editor.addConfig( qConnectionConfig);
        editor.addEnum("type",type,"Queue");
        editor.addString("destination","dev.q.test");
        editor.addString("payload","{}", PropertyFormat.JSON);
        return editor;
    }

    @Override
    public Map<String, Object> run(Map<String, Object> input) throws Exception {
        ActiveMQConfig  config = this.getExternalConfigByName(input.get(qConnectionConfig.getPropertyName()), ActiveMQConfig.class);
        JmsMessagingTemplate template = new JmsMessagingTemplate();
        template.setConnectionFactory(connectionFactoryChooser.connectionFactory(config));
        Map<String,Object> msg = new HashMap<>();
        Object payload = input.get("payload");
        ObjectMapper mapper = new ObjectMapper();
        if(payload instanceof Map){
            msg = (Map<String,Object>)payload;
        }else if(payload instanceof String){
            try{
                msg = mapper.readValue(payload.toString(), Map.class);
            }catch (Exception e){
                msg.put("msg",payload);
            }
        }
        String destination =  input.get("destination").toString();
        sendMessage(template,destination,mapper.writeValueAsString(msg));
        return input;
    }
    public void sendMessage(JmsMessagingTemplate template,String queue, String message) {
        System.out.println("sending: " + message);
        template.convertAndSend(queue, message);
    }

}
