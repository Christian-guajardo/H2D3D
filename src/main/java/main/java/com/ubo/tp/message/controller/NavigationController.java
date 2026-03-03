package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.MessageAppMainView;
import main.java.com.ubo.tp.message.ihm.template.*;

public class NavigationController implements ISessionObserver {
    private final MessageAppMainView mainView;

    private final DataManager dataManager;

    private final LoginController loginController;
    private final RegisterController registerController;

    public NavigationController(MessageAppMainView mainView,
                                DataManager dataManager,
                                LoginController loginController,
                                RegisterController registerController) {
        this.mainView = mainView;
        this.dataManager = dataManager;
        this.loginController = loginController;
        this.registerController = registerController;

        attachListeners();
    }

    private void attachListeners() {
        mainView.addRegisterAction(e -> showRegisterView());
        mainView.addLoginMenuItem(e -> showLoginView());
        mainView.addChooseExchangeMenuItem(e -> handleChooseExchangeDirectory());
    }

    private void showRegisterView() {
        RegisterView registerView = new RegisterView(registerController);
        mainView.showContent(registerView);
    }

    private void showLoginView() {
        LoginView loginView = new LoginView(loginController);
        mainView.showContent(loginView);
    }


    private void handleChooseExchangeDirectory() {
        String directory = mainView.selectExchangeDirectory();
        if (directory != null && !directory.isEmpty()) {
            dataManager.setExchangeDirectory(directory);
            mainView.showInfo("Le répertoire d'échange a été défini avec succès.");
        } else {
            mainView.showError("Erreur", "Le répertoire d'échange est invalide.");
        }
    }

    @Override
    public void notifyLogin(User connectedUser) {
        UserListView userListView = new UserListView();
        ChannelListView channelListView = new ChannelListView();
        MessageListView messageListView = new MessageListView();
        MessageInputView messageInputView = new MessageInputView();
        
        ConnectView connectView = new ConnectView(
                userListView, channelListView,
                messageListView, messageInputView);
        mainView.showConnectedState(connectView);
    }

    @Override
    public void notifyLogout() {
        mainView.showDisconnectedState();
    }
}