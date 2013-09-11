package com.email;

import akka.actor.UntypedActor;
import com.email.domain.MailConfig;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;

/**
 * @author rajith
 */
public class EmailServiceActor extends UntypedActor {

    private static final String MAIL_SMTP_AUTH_KEY = "mail.smtp.auth";
    private static final String MAIL_SMTP_STARTTLS_KEY = "mail.smtp.starttls.enable";
    private static final String MAIL_SMTP_HOST_KEY = "mail.smtp.host";
    private static final String MAIL_SMTP_PORT_KEY = "mail.smtp.port";

    public EmailServiceActor() throws IOException {
        MailConfig.loadConfigurations();
    }

    @Override
    public void onReceive(Object message) {
        System.out.println(message.toString());
        final String username = MailConfig.username;
        final String password = MailConfig.password;

        Properties props = new Properties();
        props.put(MAIL_SMTP_AUTH_KEY, MailConfig.smtpAuth);
        props.put(MAIL_SMTP_STARTTLS_KEY, MailConfig.starttlsEnable);
        props.put(MAIL_SMTP_HOST_KEY, MailConfig.smtpHost);
        props.put(MAIL_SMTP_PORT_KEY, MailConfig.smtpPort);

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(MailConfig.fromAddress));
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(MailConfig.toAddress));
            mimeMessage.setSubject("Testing Subject");
            mimeMessage.setText("Mail Body");
            Transport.send(mimeMessage);

            System.out.println("Email Successfully send. Check " + MailConfig.toAddress);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
