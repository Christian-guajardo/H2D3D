package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.selection.Selection;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.template.component.UserListView;


public class UserController {
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




}