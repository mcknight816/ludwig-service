package com.bluntsoftware.ludwig.conduit.config.mail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Mail {
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
}
