package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.core.selection.ISelectionObserver;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.AbstractMessageAppObject;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.template.component.MessageListView;
import java.util.HashSet;
import java.util.Set;

public class MessageController implements ISelectionObserver, IDatabaseObserver {
    private DataManager mDataManager;
    private Session session;
    private AbstractMessageAppObject selectedObject;
    private MessageListView messageListView;

    public MessageController(DataManager mDataManager, Session session) {
        this.mDataManager = mDataManager;
        this.session = session;
        this.messageListView = new MessageListView();
    }

    public void refreshMessages() {
        Set<Message> messages = getCurrentMessages();
        messageListView.refreshMessage(messages);
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
        refreshMessages();
    }

    public Set<Message> getCurrentMessages(){
        if (selectedObject == null) {
            return new HashSet<>();
        }
        if (selectedObject instanceof User) {
            return getMessagesForUser();
        } else if (selectedObject instanceof Channel) {
            return getMessagesForChannel();
        }
        return new HashSet<>();
    }

    private Set<Message> getMessagesForUser() {
        Set<Message> messages = new HashSet<>();
        if (session.getConnectedUser() == null) {
            return messages;
        }
        for (Message message : mDataManager.getMessages()) {
            if (message.getRecipient().equals(selectedObject.getUuid()) && message.getSender().equals(session.getConnectedUser())
            ||  message.getRecipient().equals(session.getConnectedUser().getUuid()) && message.getSender().equals((User) selectedObject)) {
                messages.add(message);
            }
        }
        return messages;
    }

    private Set<Message> getMessagesForChannel() {
        Set<Message> messages = new HashSet<>();
        for (Message message : mDataManager.getMessages()) {
            if (message.getRecipient().equals(selectedObject.getUuid())) {
                messages.add(message);
            }
        }
        return messages;
    }

    public MessageListView getMessageListView() {
        return messageListView;
    }

    @Override
    public void notifyMessageAdded(Message addedMessage) {
        this.mDataManager.sendMessage(addedMessage);
        this.refreshMessages();
    }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {
        this.refreshMessages();
    }

    @Override
    public void notifyMessageModified(Message modifiedMessage) {
        this.mDataManager.sendMessage(modifiedMessage);
        this.refreshMessages();
    }

    @Override
    public void notifyUserAdded(User addedUser) {

    }

    @Override
    public void notifyUserDeleted(User deletedUser) {
        Set<Message> messagesToDelete = new HashSet<>();
        for (Message message : mDataManager.getMessages()) {
            if (message.getSender().equals(deletedUser) || message.getRecipient().equals(deletedUser.getUuid())) {
                messagesToDelete.add(message);
            }
        }
        this.refreshMessages();

    }

    @Override
    public void notifyUserModified(User modifiedUser) {
        this.refreshMessages();

    }

    @Override
    public void notifyChannelAdded(Channel addedChannel) {

    }

    @Override
    public void notifyChannelDeleted(Channel deletedChannel) {

    }

    @Override
    public void notifyChannelModified(Channel modifiedChannel) {

    }
}
