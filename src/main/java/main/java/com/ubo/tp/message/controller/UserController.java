package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.common.Constants;
import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.core.selection.ISelection;
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
    private UserListView userListView;
    private Session session;
    /** Appelé quand un user est sélectionné — permet de vider la sélection channel */
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
    }

    @Override
    public void notifyUserDeleted(User deletedUser) {
        this.userListView.refreshUsers(getFilteredUsers());
    }

    @Override
    public void notifyUserModified(User modifiedUser) {
        this.userListView.refreshUsers(getFilteredUsers());
    }

    @Override
    public void notifyChannelAdded(Channel addedChannel) {}

    @Override
    public void notifyChannelDeleted(Channel deletedChannel) {}

    @Override
    public void notifyChannelModified(Channel modifiedChannel) {}
}