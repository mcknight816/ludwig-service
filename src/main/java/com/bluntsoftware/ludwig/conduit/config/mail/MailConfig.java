package com.bluntsoftware.ludwig.conduit.config.mail;


import com.bluntsoftware.ludwig.conduit.impl.ActivityConfigImpl;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Service
public class MailConfig extends ActivityConfigImpl {

    @Override
    public JsonSchema getRecord() {
        JsonSchema mail = new JsonSchema("mail");
        mail.addString("host","smtp.gmail.com",null);
        mail.addString("port","587",null);
        mail.addString("user",null,null);
        mail.addString("from",null,null);
        mail.addString("password",null,"password");
        mail.addString("protocol","smtp",null);
        mail.addString("localhost","localhost",null);
        mail.addString("testEmail","someone@somewhere.com",null);
        List<String> truefalse = new ArrayList<String>();
        truefalse.add("true");
        truefalse.add("false");
        mail.addEnum("tls","tls",truefalse,"false");
        mail.addEnum("auth","auth",truefalse,"true");

        return mail;
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

}
