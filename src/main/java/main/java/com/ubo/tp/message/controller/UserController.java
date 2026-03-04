package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.core.selection.Selection;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.template.component.UserListView;


public class UserController implements IDatabaseObserver {
    private final DataManager dataManager;
    private final Selection selection;
    private UserListView userListView;


    public UserController(DataManager dataManager, Selection selection) {
        this.dataManager = dataManager;
        this.selection = selection;
        this.userListView = new UserListView();
        attachListener();
    }

    public void attachListener() {
        userListView.addUserSelectListener(user -> selection.changeSelection(user));
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
        this.userListView.refreshUsers(dataManager.getUsers());
    }

    @Override
    public void notifyUserDeleted(User deletedUser) {
        this.userListView.refreshUsers(dataManager.getUsers());

    }

    @Override
    public void notifyUserModified(User modifiedUser) {
        this.userListView.refreshUsers(dataManager.getUsers());
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