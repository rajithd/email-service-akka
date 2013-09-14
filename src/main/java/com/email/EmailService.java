package com.email;

import akka.actor.*;
import akka.japi.Function;
import akka.routing.SmallestMailboxRouter;
import com.email.domain.MailConfig;
import scala.concurrent.duration.Duration;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import akka.actor.SupervisorStrategy.Directive;

import static akka.actor.SupervisorStrategy.escalate;
import static akka.actor.SupervisorStrategy.restart;
import static akka.actor.SupervisorStrategy.resume;
import static akka.actor.SupervisorStrategy.stop;


/**
 * @author rajith
 */
public class EmailService {

    private static final String MAIL_SMTP_AUTH_KEY = "mail.smtp.auth";
    private static final String MAIL_SMTP_STARTTLS_KEY = "mail.smtp.starttls.enable";
    private static final String MAIL_SMTP_HOST_KEY = "mail.smtp.host";
    private static final String MAIL_SMTP_PORT_KEY = "mail.smtp.port";
    public ActorSystem system;
    public ActorRef emailActorRef;

    public EmailService() throws IOException {
        system = ActorSystem.create("system");
        emailActorRef = system.actorOf(new Props(EmailServiceActor.class).withRouter
                (new SmallestMailboxRouter(50)), "emailService");
        MailConfig.loadConfigurations();
    }

    public void send() {
        emailActorRef.tell("email", emailActorRef);
    }

    public void sendEmail() throws MessagingException {
        System.out.println("Sending email ...");
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
            throw e;
        }

    }


}

class EmailServiceActor extends UntypedActor {

    private static SupervisorStrategy strategy =
            new OneForOneStrategy(10, Duration.create("1 minute"),
                    new Function<Throwable, Directive>() {
                        @Override
                        public Directive apply(Throwable t) {
                            if (t instanceof MessagingException) {
                                return resume();
                            } else if (t instanceof Exception) {
                                return stop();
                            } else {
                                return escalate();
                            }
                        }
                    });

    @Override
    public void onReceive(Object message) {
        getContext().actorOf(new Props(EmailServiceWorker.class)).tell(message, self());
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

}

class EmailServiceWorker extends UntypedActor {

    @Override
    public void onReceive(Object message) {
        try {
            EmailService emailService = new EmailService();
            emailService.sendEmail();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (MessagingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public void preStart() {
//        getContext().system().scheduler().scheduleOnce(Duration.create(5, TimeUnit.SECONDS), self(), "emailWorker", getContext().system().dispatcher(), null);
    }

    @Override
    public void postStop() {

    }
}
