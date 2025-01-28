package com.bluntsoftware.ludwig.conduit.config.queue;

/*import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;*/
import com.bluntsoftware.ludwig.conduit.config.queue.domain.ActiveMQConfig;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.stereotype.Service;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import java.util.Map;

@Service
public class ConnectionFactoryChooser {

   public ConnectionFactory connectionFactory(ActiveMQConfig config) throws JMSException {
      /* Map<String,Object> connection = (Map<String,Object>)config.get("connection");
       switch(connection.get("binder").toString()){
            case "Active MQ":
                return activeMqConnectionFactory(config);
            case "IBM MQ":
                return null;//ibmMqConnectionFactory(config);
        }*/
        return null;
    }

    public ActiveMQConnectionFactory activeMqConnectionFactory(Map<String, Object> config)  {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        Map<String,Object> connection = (Map<String,Object>)config.get("connection");
        String host = connection.get("host").toString();
        String port = connection.get("port").toString();
        String username = connection.get("username").toString();
        String password = connection.get("password").toString();
        String broker =  "tcp://" + host + ":" + port;
        connectionFactory.setBrokerURL(broker);
        connectionFactory.setUserName(username);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }
/*
    public MQQueueConnectionFactory ibmMqConnectionFactory(Map<String, Object> config) throws JMSException {
        MQQueueConnectionFactory connectionFactory = new MQQueueConnectionFactory();
        Map<String,String> connection = (Map<String,String>)config.get("connection");
        String host = connection.get("host");
        String port = connection.get("port");
        String username = connection.get("username");
        String password = connection.get("password");
        String qManager = connection.get("q-manager");
        String channel = connection.get("channel");
        if(channel == null){
            channel = "DEV.APP.SVRCONN";
        }
        connectionFactory.setHostName(host);
        connectionFactory.setChannel(channel);
        connectionFactory.setPort(Integer.parseInt(port));//1414
        connectionFactory.setQueueManager(qManager);
        connectionFactory.setTransportType(WMQConstants.WMQ_CM_CLIENT);
        connectionFactory.setBooleanProperty(WMQConstants.CAPABILITY_USERNAME_PASSWORD, true);
        connectionFactory.setStringProperty(WMQConstants.USERID, username);//admin
        connectionFactory.setStringProperty(WMQConstants.PASSWORD, password);//passw0rd
        return connectionFactory;
    }

 */
}
