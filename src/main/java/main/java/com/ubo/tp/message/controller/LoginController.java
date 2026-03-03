package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.ihm.template.LoginView;
import main.java.com.ubo.tp.message.datamodel.User;

public class LoginController {
    private final UserController userController;
    private final Session session;

    public LoginController(UserController userController, Session session) {
        this.userController = userController;
        this.session = session;
    }

    public void attach(LoginView loginView) {
        loginView.setLoginActionListener(event -> onLogin(loginView));
    }

    private void onLogin(LoginView lv) {
        String userTagOrName = lv.getUsername();
        String password = lv.getPassword();
        User user = userController.login(userTagOrName, password, session);
        if (user != null) {
            lv.showInfo("Connexion réussie ! Bienvenue, " + user.getName() + " !");
        } else {
            lv.showError("Erreur de connexion", "Échec de la connexion. Veuillez vérifier vos identifiants.");
        }
    }
}