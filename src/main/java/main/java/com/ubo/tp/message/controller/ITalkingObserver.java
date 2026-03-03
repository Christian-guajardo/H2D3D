package main.java.com.ubo.tp.message.controller;

public interface ITalkingObserver {
    void notifyMessageSent(ITalking talking, String message);
}
