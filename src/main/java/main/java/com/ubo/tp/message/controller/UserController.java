package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.common.Constants;
import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.core.selection.Selection;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.template.component.UserListView;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserController implements IDatabaseObserver {
    private final DataManager dataManager;
    private final Selection selection;
    private final UserListView userListView;
    private final Session session;
    
    private Runnable onUserSelected;
    private final Map<UUID, Integer> liveNotifCount = new HashMap<>();

    public void setOnUserSelected(Runnable callback) {
        this.onUserSelected = callback;
    }

    public UserListView getUserListView() {
        return userListView;
    }

    public UserController(DataManager dataManager, Selection selection, Session session) {
        this.dataManager = dataManager;
        this.selection = selection;
        this.session = session;
        this.userListView = new UserListView();
        this.userListView.addUserSelectListener(user -> {
            selection.changeSelection(user);
            userListView.setSelectedUser(user);
            // Réinitialise le compteur live pour cet utilisateur (messages considérés comme lus)
            liveNotifCount.put(user.getUuid(), 0);
            if (onUserSelected != null) onUserSelected.run();
        });
        this.userListView.refreshUsers(getFilteredUsers());
    }

    // filtre notre utilisateur connecté et le unknow user
    private Set<User> getFilteredUsers() {
        return dataManager.getUsers().stream()
                .filter(u -> !u.equals(session.getConnectedUser()))
                .filter(u -> !u.getUuid().equals(Constants.UNKNONWN_USER_UUID))
                .collect(Collectors.toSet());
    }

    /**
     * Recalcule et affiche le badge de notification pour un utilisateur donné
     * en se basant uniquement sur le compteur en mémoire (messages reçus depuis la connexion).
     */
    private void refreshBadgeForUser(User sender) {
        if (sender == null) return;

        // Si cet user est actuellement sélectionné, pas de badge
        if (selection.getmSelected() != null && selection.getmSelected().getUuid().equals(sender.getUuid())) {
            userListView.setNotificationCount(sender, 0);
            return;
        }

        int count = liveNotifCount.getOrDefault(sender.getUuid(), 0);
        userListView.setNotificationCount(sender, count);
    }

    /** Recalcule tous les badges (appelé après un refresh de la liste). */
    private void refreshAllBadges() {
        for (User u : getFilteredUsers()) {
            refreshBadgeForUser(u);
        }
    }

    public void attachListener() {
        // listener déjà enregistré dans le constructeur
    }

    @Override
    public void notifyMessageAdded(Message addedMessage) {
        User me = session.getConnectedUser();
        if (me == null) return;
        // On ne met à jour que si le message nous est destiné
        if (!addedMessage.getRecipient().equals(me.getUuid())) return;
        User sender = addedMessage.getSender();
        if (sender == null) return;
        // Incrémente le compteur live pour cet expéditeur
        liveNotifCount.merge(sender.getUuid(), 1, Integer::sum);
        refreshBadgeForUser(sender);
    }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {
        User me = session.getConnectedUser();
        if (me == null) return;
        if (!deletedMessage.getRecipient().equals(me.getUuid())) return;
        User sender = deletedMessage.getSender();
        if (sender == null) return;
        // Décrémente le compteur live (minimum 0)
        liveNotifCount.merge(sender.getUuid(), -1, (old, d) -> Math.max(0, old + d));
        refreshBadgeForUser(sender);
    }

    @Override
    public void notifyMessageModified(Message modifiedMessage) {}

    @Override
    public void notifyUserAdded(User addedUser) {
        if (addedUser.getUuid().equals(Constants.UNKNONWN_USER_UUID)) return;
        this.userListView.refreshUsers(getFilteredUsers());
        refreshAllBadges();
    }

    @Override
    public void notifyUserDeleted(User deletedUser) {
        this.userListView.refreshUsers(getFilteredUsers());
        refreshAllBadges();
    }

    @Override
    public void notifyUserModified(User modifiedUser) {
        this.userListView.refreshUsers(getFilteredUsers());
        refreshAllBadges();
    }

    @Override
    public void notifyChannelAdded(Channel addedChannel) {}

    @Override
    public void notifyChannelDeleted(Channel deletedChannel) {}

    @Override
    public void notifyChannelModified(Channel modifiedChannel) {}
}