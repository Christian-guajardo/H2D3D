package main.java.com.ubo.tp.message.controller;

import java.util.Objects;
import java.util.Set;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.User;

public class UserController {
    private final DataManager mDataManager;

    public UserController(DataManager dataManager) {
        this.mDataManager = Objects.requireNonNull(dataManager);
    }

    public User register(String tag, String username, String password) {
        if (tag == null || tag.trim().isEmpty() || username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            System.err.println("Tous les champs (tag, username, password) doivent être remplis et non vides.");
            return null;
        }

        Set<User> users = mDataManager.getUsers();
        for (User u : users) {
            if (tag.equals(u.getUserTag())) {
                System.err.println("Le tag '" + tag + "' est déjà utilisé par un autre utilisateur.");
                return null;
            }
        }

        User user = new User(tag, password, username);
        try {
            mDataManager.sendUser(user);
        } catch (RuntimeException ex) {
            System.err.println("Erreur lors de l'enregistrement de l'utilisateur : " + ex.getMessage());
            return null;
        }

        return user;
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