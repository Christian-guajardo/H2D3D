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

import java.util.Set;
import java.util.stream.Collectors;

public class UserController implements IDatabaseObserver {
    private final DataManager dataManager;
    private final Selection selection;
    private final UserListView userListView;
    private final Session session;

    private Runnable onUserSelected;

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
            // set le nombre de notif a 0 pour le user sélectionné
            userListView.setNotificationCount(user, 0);
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

    private void refreshAllBadges() {
        if (selection.getmSelected() instanceof User selected) {
            userListView.setNotificationCount(selected, 0);
        }
    }

    @Override
    public void notifyMessageAdded(Message addedMessage) {
        User me = session.getConnectedUser();
        if (me == null) return;
        if (!addedMessage.getRecipient().equals(me.getUuid())) return;
        User sender = addedMessage.getSender();
        if (sender == null) return;
        // Si l'expéditeur est actuellement sélectionné, pas de badge
        if (selection.getmSelected() != null && selection.getmSelected().getUuid().equals(sender.getUuid())) return;
        userListView.incrementNotificationCount(sender);
    }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {
        User me = session.getConnectedUser();
        if (me == null) return;
        if (!deletedMessage.getRecipient().equals(me.getUuid())) return;
        User sender = deletedMessage.getSender();
        if (sender == null) return;
        // Si l'expéditeur est actuellement sélectionné, le badge est déjà à 0
        if (selection.getmSelected() != null && selection.getmSelected().getUuid().equals(sender.getUuid())) return;
        userListView.decrementNotificationCount(sender);
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