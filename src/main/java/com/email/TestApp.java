package com.email;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

/**
 * Test class to invoke email service actor to send mail
 */

public class TestApp {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("MySystem");
        ActorRef emailActorRef = system.actorOf(new Props(EmailServiceActor.class), "email");
        emailActorRef.tell("Email Actor Started", ActorRef.noSender());
    }
}
