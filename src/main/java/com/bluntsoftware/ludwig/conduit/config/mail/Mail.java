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
/*
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "SMTP Email Configuration",
  "type": "object",
  "required": ["host", "port", "username", "password"],
  "properties": {
    "host": {
      "type": "string",
      "description": "The hostname or IP address of the SMTP server."
    },
    "port": {
      "type": "integer",
      "description": "The port number used to connect to the SMTP server."
    },
    "username": {
      "type": "string",
      "description": "The username for authenticating with the SMTP server."
    },
    "password": {
      "type": "string",
      "description": "The password for authenticating with the SMTP server."
    },
    "encryption": {
      "type": "string",
      "enum": ["none", "ssl", "tls"],
      "default": "none",
      "description": "The encryption method used for the SMTP connection."
    },
    "timeout": {
      "type": "integer",
      "default": 30,
      "description": "The timeout duration in seconds for the SMTP connection."
    },
    "fromAddress": {
      "type": "string",
      "format": "email",
      "description": "The default email address used as the sender."
    }
  }
}

 */
}
