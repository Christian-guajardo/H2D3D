package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.MessageAppMainView;

public class MessageAppMainController {
    private final DataManager dataManager;
    private final MessageAppMainView mainView;
    private final Session session;

    private final UserController userController;
    private final NavigationController navigationController;

    public MessageAppMainController(DataManager dataManager, MessageAppMainView mainView) {
        this.dataManager = dataManager;
        this.mainView = mainView;
        this.session = new Session();
        this.userController = new UserController(dataManager);
        this.navigationController = new NavigationController(
                mainView, userController, session, dataManager
        );
        session.addObserver(this.navigationController);
    }

    public void init() {
        mainView.getLogoutMenuItem().addActionListener(e -> handleLogout());
    }

    private void handleLogout() {
        session.disconnect();

    }


}