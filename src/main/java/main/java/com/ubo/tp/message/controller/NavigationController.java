package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.MessageAppMainView;
import main.java.com.ubo.tp.message.ihm.template.*;
import main.java.com.ubo.tp.message.ihm.template.component.ChannelListView;
import main.java.com.ubo.tp.message.ihm.template.component.MessageInputView;
import main.java.com.ubo.tp.message.ihm.template.component.MessageListView;
import main.java.com.ubo.tp.message.ihm.template.component.UserListView;

public class NavigationController implements ISessionObserver {
    private final MessageAppMainView mainView;

    private final DataManager dataManager;

    private final LoginController loginController;
    private final RegisterController registerController;
    private final ConnectController connectController;

    public NavigationController(MessageAppMainView mainView,
                                DataManager dataManager,
                                LoginController loginController,
                                RegisterController registerController,
                                ConnectController connectController) {
        this.mainView = mainView;
        this.dataManager = dataManager;
        this.loginController = loginController;
        this.registerController = registerController;
        this.connectController = connectController;

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

        MessageInputView messageInputView = new MessageInputView(connectController.getMessageInputController());
        ConnectView connectView = new ConnectView(connectController.getUserController().getUserListView(),
                connectController.getChannelController().getChannelListView(),
                connectController.getMessageController().getMessageListView(),
                messageInputView, connectController);
        mainView.showConnectedState(connectView);
    }

    @Override
    public void notifyLogout() {
        mainView.showDisconnectedState();
    }
}