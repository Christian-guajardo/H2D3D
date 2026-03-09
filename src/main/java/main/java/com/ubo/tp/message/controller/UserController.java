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

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class UserController implements IDatabaseObserver {
    private final DataManager dataManager;
    private final Selection selection;
    private UserListView userListView;
    private Session session;

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
        });
        this.userListView.refreshUsers(getFilteredUsers());
        attachListener();
    }

    // FIX: méthode utilitaire pour récupérer les users sans UNKNOWN_USER
    private Set<User> getFilteredUsers() {
        return dataManager.getUsers().stream()
                .filter(user -> !user.getUuid().equals(Constants.UNKNONWN_USER_UUID))
                .filter(user -> !user.getUuid().equals(session.getConnectedUser() != null ? session.getConnectedUser().getUuid() : ""))
                .collect(Collectors.toSet());
    }

    public void attachListener() {
        userListView.addUserSelectListener(user -> selection.changeSelection(user));
    }

    @Override
    public void notifyMessageAdded(Message addedMessage) {}

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {}

    @Override
    public void notifyMessageModified(Message modifiedMessage) {}

    @Override
    public void notifyUserAdded(User addedUser) {
        // FIX: ignorer l'utilisateur inconnu
        if (addedUser.getUuid().equals(Constants.UNKNONWN_USER_UUID)) return;
        this.userListView.refreshUsers(getFilteredUsers());
        attachListener();
    }

    @Override
    public void notifyUserDeleted(User deletedUser) {
        this.userListView.refreshUsers(getFilteredUsers());
        attachListener();
    }

    @Override
    public void notifyUserModified(User modifiedUser) {
        this.userListView.refreshUsers(getFilteredUsers());
        attachListener();
    }

    @Override
    public void notifyChannelAdded(Channel addedChannel) {}

    @Override
    public void notifyChannelDeleted(Channel deletedChannel) {}

    @Override
    public void notifyChannelModified(Channel modifiedChannel) {}
}