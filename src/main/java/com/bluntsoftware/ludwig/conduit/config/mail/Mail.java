package com.bluntsoftware.ludwig.conduit.config.mail;

import com.bluntsoftware.ludwig.conduit.schema.EntitySchema;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.schema.PropertyFormat;
import com.bluntsoftware.ludwig.conduit.schema.StringProperty;
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
    @Builder.Default
    private String host = "smtp.gmail.com";
    @Builder.Default
    private String port = "587";
    private String user;
    private String from;
    private String password;
    @Builder.Default
    private String protocol = "smtp";
    @Builder.Default
    private String tls = "false";
    @Builder.Default
    private String auth = "true";
    @Builder.Default
    private String localhost= "localhost";
    private String testEmail;

    static JsonSchema getSchema() {
        JsonSchema mail = JsonSchema.builder().title("mail").build();
        mail.addString("host","smtp.gmail.com");
        mail.addString("port","587");
        mail.addString("user");
        mail.addString("from");
        mail.addString("password", StringProperty.builder().format(PropertyFormat.PASSWORD).build());
        mail.addString("protocol","smtp");
        mail.addString("localhost","localhost");
        mail.addString("testEmail","someone@somewhere.com");
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
