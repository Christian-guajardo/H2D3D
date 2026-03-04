package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.selection.Selection;
import main.java.com.ubo.tp.message.datamodel.User;


public class UserController {
    private final DataManager dataManager;
    private final Selection selection;

    public UserController(DataManager dataManager, Selection selection) {
        this.dataManager = dataManager;
        this.selection = selection;
    }

    public void changeCurrentSelection(User user) {
        selection.changeSelection(user);
    }


}
