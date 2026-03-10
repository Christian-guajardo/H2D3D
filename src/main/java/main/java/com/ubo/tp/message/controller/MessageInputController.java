package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.common.Constants;
import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.selection.ISelectionObserver;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.AbstractMessageAppObject;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessageInputController implements ISelectionObserver {
    private final DataManager mDataManager;
    private final Session session;
    private AbstractMessageAppObject selectedObject;

    public MessageInputController(DataManager mDataManager, Session session) {
        this.mDataManager = mDataManager;
        this.session = session;
    }

    public void sendMessage(String text) {
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
}