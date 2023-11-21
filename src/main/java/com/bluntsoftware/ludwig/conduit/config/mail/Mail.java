package com.bluntsoftware.ludwig.conduit.config.mail;

import com.bluntsoftware.ludwig.conduit.schema.EntitySchema;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Mail implements EntitySchema {
    private String host = "smtp.gmail.com";
    private String port = "587";
    private String user;
    private String from;
    private String password;
    private String protocol = "smtp";
    private String tls = "false";
    private String auth = "true";
    private String localhost= "localhost";
    private String testEmail;

    static JsonSchema getSchema() {
        JsonSchema mail = JsonSchema.builder().title("mail").build();
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

}
