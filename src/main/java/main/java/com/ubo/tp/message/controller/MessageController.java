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
    public static final int MESSAGE_MAX_LENGTH = 200;
    private final DataManager mDataManager;
    private final Session session;
    private AbstractMessageAppObject selectedObject;
    private final MessageListView messageListView;

    public MessageController(DataManager mDataManager, Session session) {
        this.mDataManager = mDataManager;
        this.session = session;
        this.messageListView = new MessageListView();
        // Le callback de suppression est enregistré une seule fois
        this.messageListView.setOnDelete(mDataManager::deleteMessage);
    }

    public void refreshMessages() {
        messageListView.refreshMessage(getCurrentMessages(), session.getConnectedUser());
    }

    public void sendMessage(String text) {
        mDataManager.sendMessage(new Message(session.getConnectedUser(), selectedObject.getUuid(), text));
    }

    public AbstractMessageAppObject getSelectedObject() { return selectedObject; }
    public void setSelectedObject(AbstractMessageAppObject selectedObject) { this.selectedObject = selectedObject; }

    @Override
    public void notify(AbstractMessageAppObject selectedObject) {
        this.setSelectedObject(selectedObject);
        refreshMessages();
    }

    public Set<Message> getCurrentMessages() {
        if (selectedObject == null) return new HashSet<>();
        if (selectedObject instanceof User) return getMessagesForUser();
        if (selectedObject instanceof Channel) return getMessagesForChannel();
        return new HashSet<>();
    }

    private Set<Message> getMessagesForUser() {
        Set<Message> messages = new HashSet<>();
        User me = session.getConnectedUser();
        for (Message message : mDataManager.getMessages()) {
            boolean iSentToThem = message.getRecipient().equals(selectedObject.getUuid())
                    && message.getSender().equals(me);
            boolean theySentToMe = message.getRecipient().equals(me.getUuid())
                    && message.getSender().equals((User) selectedObject);
            if (iSentToThem || theySentToMe) messages.add(message);
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

    public MessageListView getMessageListView() { return messageListView; }

    @Override
    public void notifyMessageAdded(Message addedMessage) {
        if (selectedObject == null) return;
        User me = session.getConnectedUser();
        boolean isForCurrentConversation =
                addedMessage.getRecipient().equals(selectedObject.getUuid())
                        || (selectedObject instanceof User
                        && addedMessage.getRecipient().equals(me.getUuid())
                        && addedMessage.getSender().equals((User) selectedObject));

        if (isForCurrentConversation) {
            messageListView.addSingleMessage(addedMessage, me);
        }
    }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {
        refreshMessages();
    }

    @Override
    public void notifyMessageModified(Message modifiedMessage) {
        refreshMessages();
    }

    @Override public void notifyUserAdded(User addedUser) {}

    @Override
    public void notifyUserDeleted(User deletedUser) {
        if (selectedObject != null && selectedObject.getUuid().equals(deletedUser.getUuid())) {
            selectedObject = null;
        }
        refreshMessages();
    }

    @Override
    public void notifyUserModified(User modifiedUser) {
        refreshMessages();
    }

    @Override public void notifyChannelAdded(Channel addedChannel) {}

    @Override
    public void notifyChannelDeleted(Channel deletedChannel) {
        if (selectedObject != null && selectedObject.getUuid().equals(deletedChannel.getUuid())) {
            selectedObject = null;
        }
        refreshMessages();
    }

    @Override
    public void notifyChannelModified(Channel modifiedChannel) {
        User me = session.getConnectedUser();
        // Si on est retiré du canal et qu'on l'avait sélectionné → vider
        boolean isMember = modifiedChannel.getUsers().contains(me)
                || modifiedChannel.getCreator().equals(me);
        if (!isMember && selectedObject != null
                && selectedObject.getUuid().equals(modifiedChannel.getUuid())) {
            selectedObject = null;
        }
        refreshMessages();
    }
}