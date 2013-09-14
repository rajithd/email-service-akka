package com.email;

import java.io.IOException;

/**
 * Test class to invoke email service actor to send mail
 */

public class TestApp {
    public static void main(String[] args) throws IOException {
        EmailService emailService = new EmailService();
        emailService.send();

    }
}
