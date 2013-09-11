package com.email.domain;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author rajith
 */
final public class MailConfig {

    public static String username;
    public static String password;
    public static String smtpAuth;
    public static String starttlsEnable;
    public static String smtpHost;
    public static String smtpPort;
    public static String fromAddress;
    public static String toAddress;

    public static void loadConfigurations() throws IOException {
        InputStream resource = MailConfig.class.getClassLoader().getResourceAsStream("mail.properties");
        if (resource == null) {
            throw new IllegalStateException("can not find mail.properties file in classpath");
        }
        Properties properties = new Properties();
        properties.load(resource);
        MailConfig.username = properties.getProperty("mail.username");
        MailConfig.password = properties.getProperty("mail.password");
        MailConfig.smtpAuth = properties.getProperty("mail.smtp.auth");
        MailConfig.starttlsEnable = properties.getProperty("mail.smtp.starttls.enable");
        MailConfig.smtpHost = properties.getProperty("mail.smtp.host");
        MailConfig.smtpPort = properties.getProperty("mail.smtp.port");
        MailConfig.toAddress = properties.getProperty("mail.to.address");
        MailConfig.fromAddress = properties.getProperty("mail.from.address");
    }


}
