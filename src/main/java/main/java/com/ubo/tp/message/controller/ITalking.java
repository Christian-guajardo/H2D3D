package main.java.com.ubo.tp.message.controller;

import java.util.UUID;

public interface ITalking {

        void addObserver(ITalkingObserver observer);
        void removeObserver(ITalkingObserver observer);
        void notifyMessageSent(UUID id);
}
