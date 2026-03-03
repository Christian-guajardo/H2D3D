package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.Message;
import java.util.UUID;

public class MessageController {
    private DataManager mDataManager;
    private Session session;

    public MessageController(DataManager mDataManager, Session session) {
        this.mDataManager = mDataManager;
        this.session = session;
    }

    public void sendMessage(UUID dest, String text){
        mDataManager.sendMessage(new Message(session.getConnectedUser(), dest, text));
    }
}
