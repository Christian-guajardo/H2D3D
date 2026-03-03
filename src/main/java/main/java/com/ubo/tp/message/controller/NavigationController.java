package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.MessageAppMainView;
import main.java.com.ubo.tp.message.ihm.template.LoginView;
import main.java.com.ubo.tp.message.ihm.template.RegisterView;

public class NavigationController implements ISessionObserver {
    private final MessageAppMainView mainView;
    private final UserController userController;
    private final Session session;
    private final DataManager dataManager;

    private final LoginController loginController;
    private final RegisterController registerController;

    public NavigationController(MessageAppMainView mainView,
                                UserController userController,
                                Session session,
                                DataManager dataManager) {
        this.mainView = mainView;
        this.userController = userController;
        this.session = session;
        this.dataManager = dataManager;

        this.loginController = new LoginController(userController, session);
        this.registerController = new RegisterController(userController);

        attachListeners();
    }

    private void attachListeners() {
        mainView.getRegisterMenuItem().addActionListener(e -> showRegisterView());
        mainView.getLoginMenuItem().addActionListener(e -> showLoginView());
        mainView.getChooseExchangeMenuItem().addActionListener(e -> handleChooseExchangeDirectory());
    }

    private void showRegisterView() {
        RegisterView registerView = new RegisterView();
        registerController.attach(registerView);
        mainView.showContent(registerView);
    }

    private void showLoginView() {
        LoginView loginView = new LoginView();
        loginController.attach(loginView);
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
        mainView.showConnectedState();
    }

    @Override
    public void notifyLogout() {
        mainView.showDisconnectedState();
    }
}