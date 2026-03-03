package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.ihm.template.LoginView;
import main.java.com.ubo.tp.message.datamodel.User;

import java.util.Set;

public class LoginController {
    private final DataManager mDataManager;
    private final Session session;

    public LoginController(DataManager mDataManager, Session session) {
        this.mDataManager = mDataManager;
        this.session = session;
    }


    public int onLogin(  String password,String userTagOrName) {

        User user = login(userTagOrName, password, session);
        if (user != null) {
            return 0;
            lv.showInfo("Connexion réussie ! Bienvenue, " + user.getName() + " !");
        } else {
            return 1;
            lv.showError("Erreur de connexion", "Échec de la connexion. Veuillez vérifier vos identifiants.");
        }
    }
    public User login(String userTagOrName, String password, Session session) {
        if (userTagOrName == null || userTagOrName.trim().isEmpty() || password == null) {
            return null;
        }

        Set<User> users = mDataManager.getUsers();
        for (User u : users) {
            boolean matchTag = userTagOrName.equals(u.getUserTag());
            boolean matchName = userTagOrName.equals(u.getName());
            if ((matchTag || matchName) && password.equals(u.getUserPassword())) {
                u.setOnline(true);
                try {
                    mDataManager.sendUser(u);
                    session.connect(u);
                } catch (RuntimeException ex) {
                    System.err.println("Erreur lors de la mise à jour de l'utilisateur : " + ex.getMessage());
                }
                return u;
            }
        }

        return null;
    }

}