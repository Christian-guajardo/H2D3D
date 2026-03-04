package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.selection.ISelectionObserver;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.AbstractMessageAppObject;
import main.java.com.ubo.tp.message.datamodel.Message;

import java.util.Set;

public class MessageController implements ISelectionObserver {
    private DataManager mDataManager;
    private Session session;
    private AbstractMessageAppObject selectedObject;

    public MessageController(DataManager mDataManager, Session session) {
        this.mDataManager = mDataManager;
        this.session = session;
    }

    public void sendMessage(String text){
        mDataManager.sendMessage(new Message(session.getConnectedUser(), selectedObject.getUuid(), text));
    }

    public AbstractMessageAppObject getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(AbstractMessageAppObject selectedObject) {
        this.selectedObject = selectedObject;
    }

    @Override
    public void notify(AbstractMessageAppObject selectedObject) {
        this.setSelectedObject(selectedObject);
    }

    public Set<Message> getCurrentMessages(){
        Set<Message> messages = mDataManager.getMessages().stream()
                .filter(m -> m.getRecipient().equals(selectedObject.getUuid()))
                .collect(java.util.stream.Collectors.toSet());
        if (messages == null) {
            return new java.util.HashSet<>();
        }
        return messages;
    }
}
