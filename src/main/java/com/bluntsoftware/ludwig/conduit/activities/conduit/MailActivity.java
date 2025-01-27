package com.bluntsoftware.ludwig.conduit.activities.conduit;

import com.bluntsoftware.ludwig.conduit.config.mail.domain.MailConfig;
import com.bluntsoftware.ludwig.conduit.config.mail.MailConfigActivity;
import com.bluntsoftware.ludwig.conduit.activities.ActivityImpl;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import org.apache.commons.lang3.CharEncoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
/**
 * Created by Alex Mcknight on 2/13/2017.
 *
 */
@Service
public class MailActivity extends ActivityImpl {
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final String PROP_SMTP_AUTH = "mail.smtp.auth";
    private static final String PROP_SMTP_TRUST =  "mail.smtp.ssl.trust";
    private static final String PROP_STARTTLS = "mail.smtp.starttls.enable";
    private static final String PROP_TRANSPORT_PROTO = "mail.transport.protocol";
    private MailConfigActivity mailConfig;
    private JavaMailSenderImpl javaMailSender;

    private final static String to = "to";
    private final static String cc = "cc";
    private final static String bcc = "bcc";
    private final static String from = "from";
    private final static String subject = "subject";
    private final static String isHtml = "isHtml";
    private final static String content = "content";
    private final static String isMultipart = "isMultipart";


    @Autowired
    public MailActivity(MailConfigActivity mailConfig , ActivityConfigRepository activityConfigRepository) {
        super(activityConfigRepository);
        this.mailConfig = mailConfig;
    }

    @Override
    public JsonSchema getJsonSchema() {
        JsonSchema schema = JsonSchema.builder().title("Mail").build();
        schema.addConfig(mailConfig);
        schema.addString(to,"admin@bluntsoftware.com");
        schema.addString(cc,"admin@bluntsoftware.com");
        schema.addString(bcc,"admin@bluntsoftware.com");
        schema.addString(from,"admin@bluntsoftware.com");
        schema.addString(subject,"Test Email");
        schema.addString(isHtml,"false");
        schema.addString(content,"Hello World");
        schema.addString(isMultipart,"false");
        return schema;
    }



    @Override
    public Boolean fireAndForget() {
        return true;
    }


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
    public Map<String, Object> run(Map<String, Object> input) throws Exception {
        Map<String, Object> ret = new HashMap<>();
        Map<String, Object>  config = this.getExternalConfigByName(input.get("mail"), MailConfigActivity.class);
        if(config != null){
            this.javaMailSender = getMailSender(MailConfigActivity.getStaticConfig(config));
        }

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            boolean bisMultiPart = false;
            if(input.get(isMultipart) != null){
                bisMultiPart = Boolean.parseBoolean(input.get(isMultipart).toString());
            }
            boolean bisHtml = false;
            if(input.get(isHtml) != null){
                bisHtml = Boolean.parseBoolean(input.get(isHtml).toString());
            }
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, bisMultiPart, CharEncoding.UTF_8);

            if(input.get(to) != null){
                String[] mailTo = input.get(to).toString().split(",");
                message.setTo(mailTo);
            }
            if(input.get(cc) != null){
                String[] ccTo = input.get(cc).toString().split(",");
                message.setCc(ccTo);
            }
            if(input.get(bcc) != null){
                String[] bccTo = input.get(bcc).toString().split(",");
                message.setBcc(bccTo);
            }

            message.setFrom(input.get(from).toString());
            message.setSubject(input.get(subject).toString());
            message.setText(input.get(content).toString(), bisHtml);
            //  ByteArrayResource stream = new ByteArrayResource(content.getBytes());
            //  message.addAttachment("content.html", stream);
            javaMailSender.send(mimeMessage);
            ret.put("msg","success");
        return ret;
    }
    @Override
    public Map<String, Object> getOutput() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("msg","success");
        return ret;
    }
    @Override
    public String getIcon() {
        return "fa-send";
    }

    public static void main(String[] args) {
        String host = "smtpout.secureserver.net";
        int port = 465; //465
        String password = "Firehawk00";
        String userName = "info@graniteforest.org";

        Properties props = new Properties();
       // props.put("mail.transport.protocol", "smtps");
        //props.put("mail.smtps.host", host);
        props.put("mail.smtps.auth", "true");
        props.put("mail.smtps.localhost","ec2-54-71-26-250.us-west-2.compute.amazonaws.com");
        //props.put("spring.mail.auth", "true");
        //ec2-54-71-26-250.us-west-2.compute.amazonaws.com
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setProtocol("smtps");
        mailSender.setHost(host);
        mailSender.setPort(port);

        mailSender.setPassword(password);
        mailSender.setUsername(userName);
        mailSender.setJavaMailProperties(props);

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false, CharEncoding.UTF_8);
            message.setFrom("info@graniteforest.org");
            message.setTo("admin@bluntsoftware.com");
            message.setCc("geomck1967@gmail.com");
            message.setSubject("Test");
            message.setText("Hello World",false);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        //MailActivity activity = new MailActivity();
       // System.out.println(activity.getOutput());
    }
}
