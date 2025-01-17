package com.bluntsoftware.ludwig.conduit.config.mail;


import com.bluntsoftware.ludwig.conduit.config.ActivityConfig;
import com.bluntsoftware.ludwig.conduit.impl.ActivityConfigImpl;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
@Service
public class MailConfig extends ActivityConfigImpl<Mail> {

    @Override
    public JsonSchema getRecord() {
        return Mail.getSchema();
    }

    @Override
    public Map test() {
        return null;
    }

    private static String getPropString(Map<String,Object> config, String key){
        Object obj = config.get(key);
        if(obj instanceof String){
            return obj.toString();
        }
        return null;
    }

    public static Mail convertToMailConfig(Map<String,Object> config){
        Mail mail =new Mail();
        mail.setHost(getPropString(config,"host"));
        mail.setPort(getPropString(config,"port"));
        mail.setUser(getPropString(config,"user"));
        mail.setProtocol(getPropString(config,"protocol"));
        mail.setLocalhost(getPropString(config,"localhost"));
        mail.setFrom(getPropString(config,"from"));
        mail.setPassword(getPropString(config,"password"));
        mail.setTls(getPropString(config,"tls"));
        mail.setAuth(getPropString(config,"auth"));
        return mail;
    }

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ActivityConfig configSchema =  (ActivityConfig)MailConfig.class.getDeclaredConstructor().newInstance();
        System.out.println(configSchema.getSchema().getProperties());
    }

}
