package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.common.Constants;
import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.core.selection.ISelectionObserver;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.AbstractMessageAppObject;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessageInputController implements ISelectionObserver, IDatabaseObserver {
    private final DataManager mDataManager;
    private final Session session;
    private AbstractMessageAppObject selectedObject;

    public MessageInputController(DataManager mDataManager, Session session) {
        this.mDataManager = mDataManager;
        this.session = session;
    }

    public void sendMessage(String text) {
        if (selectedObject == null) return;
        mDataManager.sendMessage(new Message(session.getConnectedUser(), selectedObject.getUuid(), text));
    }

    /**
     * Retourne les utilisateurs disponibles pour la mention @
     * selon le contexte de sélection :
     * - Canal privé : membres + créateur
     * - Canal public : tous les users
     * - User : juste cet user
     */
    public Set<User> getAvailableUsersForMention() {
        if (selectedObject == null) return new HashSet<>();

        if (selectedObject instanceof Channel) {
            Channel channel = (Channel) selectedObject;
            if (channel.isPrivate()) {
                Set<User> users = new HashSet<>(channel.getUsers());
                users.add(channel.getCreator());
                users.remove(session.getConnectedUser());
                users.remove(Constants.UNKNOWN_USER);
                return users;
            } else {
                // Canal public : tous les users sauf moi
                Set<User> all = new HashSet<>(mDataManager.getUsers());
                all.remove(session.getConnectedUser());
                all.remove(Constants.UNKNOWN_USER);
                return all;
            }
        }

        if (selectedObject instanceof User) {
            Set<User> users = new HashSet<>();
            users.add((User) selectedObject);
            return users;
        }

        return new HashSet<>();
    }

    public AbstractMessageAppObject getSelectedObject() { return selectedObject; }
    public void setSelectedObject(AbstractMessageAppObject selectedObject) { this.selectedObject = selectedObject; }

    @Override
    public void notify(AbstractMessageAppObject selectedObject) {
        this.setSelectedObject(selectedObject);
    }

    @Override
    public void notifyMessageAdded(Message addedMessage) {

    }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {

    }

    @Override
    public void notifyMessageModified(Message modifiedMessage) {

    }

    @Override
    public void notifyUserAdded(User addedUser) {

    }

    @Override
    public void notifyUserDeleted(User deletedUser) {

    }

    @Override
    public void notifyUserModified(User modifiedUser) {

    }

    @Override
    public void notifyChannelAdded(Channel addedChannel) {

    }

    @Override
    public void notifyChannelDeleted(Channel deletedChannel) {
        if (selectedObject != null && selectedObject.getUuid().equals(deletedChannel.getUuid())) {
            selectedObject = null;
        }
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
            System.out.println("user "+ me.getName() + " removed from channel " + modifiedChannel.getName() + ", deselecting");
        }
    }
}