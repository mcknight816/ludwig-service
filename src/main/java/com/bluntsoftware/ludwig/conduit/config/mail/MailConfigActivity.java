package com.bluntsoftware.ludwig.conduit.config.mail;

import com.bluntsoftware.ludwig.conduit.config.ActivityConfigImpl;
import com.bluntsoftware.ludwig.conduit.config.ConfigTestResult;
import com.bluntsoftware.ludwig.conduit.config.mail.domain.MailConfig;
import org.jetbrains.annotations.NotNull;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Properties;

@Service
public class MailConfigActivity extends ActivityConfigImpl<MailConfig> {

    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final String PROP_SMTP_AUTH = "mail.smtp.auth";
    private static final String PROP_SMTP_TRUST =  "mail.smtp.ssl.trust";
    private static final String PROP_STARTTLS = "mail.smtp.starttls.enable";
    private static final String PROP_TRANSPORT_PROTO = "mail.transport.protocol";

    public static JavaMailSenderImpl getMailSender(MailConfig props){
        String host = props.getHost();
        String port = props.getPort();
        String user = props.getUser();
        String password = props.getPassword();
        String protocol = props.getProtocol();
        String tls = props.getTls();
        String auth = "true";//props.getAuth()

        String localhost = props.getLocalhost();
        return getMailSender(host, Integer.parseInt(port),user,password,protocol, Boolean.parseBoolean(tls), Boolean.parseBoolean(auth),localhost);
    }
    public static JavaMailSenderImpl getMailSender(String host, int port, String user, String password,
                                                   String protocol, Boolean tls, Boolean auth , String localhost){

        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        if (host != null && !host.isEmpty()) {
            sender.setHost(host);
        } else {
            sender.setHost(DEFAULT_HOST);
        }
        sender.setPort(port);
        sender.setUsername(user);
        sender.setPassword(password);
        sender.setProtocol(protocol);

        Properties sendProperties = new Properties();
        sendProperties.setProperty(PROP_TRANSPORT_PROTO, protocol);
        if(protocol.equalsIgnoreCase("smtps")){
            sendProperties.setProperty("mail.smtps.auth", auth.toString());
            //sendProperties.setProperty("mail.smtps.starttls.enable", tls.toString());
            //sendProperties.setProperty("mail.smtps.ssl.trust", PROP_HOST);
            sendProperties.setProperty("mail.smtps.localhost", localhost);
        }else{
            sendProperties.setProperty(PROP_SMTP_AUTH, auth.toString());
            sendProperties.setProperty(PROP_STARTTLS, tls.toString());
            sendProperties.setProperty(PROP_SMTP_TRUST, host);
            sendProperties.setProperty(PROP_TRANSPORT_PROTO, protocol);
            sendProperties.setProperty("mail.smtp.localhost", localhost);
        }
        sender.setJavaMailProperties(sendProperties);
        return sender;
    }

    @Override
    public ConfigTestResult testConfig(MailConfig config) {
        JavaMailSenderImpl mailSender = getMailSender(config);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(config.getTestEmail());
            helper.setFrom(config.getFrom());
            helper.setSubject("Test mail from  " + config.getFrom());
            helper.setText("This is a test email sent from the ludwig system.");
            mailSender.send(mimeMessage);
            return ConfigTestResult.builder()
                    .success(true)
                    .message(String.format("Test mail sent from %s successfully.", config.getFrom()))
                    .build();
        } catch (Exception e) {
            return ConfigTestResult.builder()
                    .error(true)
                    .message(String.format("Test mail sent from %s failed.", config.getFrom()))
                    .hint(String.format(" Error is : %s", e.getMessage()))
                    .build();
        }
    }

    @NotNull
    public static MailConfig getStaticConfig(Map<String,Object> config){
        MailConfigActivity mailConfig = new MailConfigActivity();
        return mailConfig.getConfig(config);
    }
}
