package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.datamodel.Response;
import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.datamodel.User;

import java.util.Set;

public class RegisterController {
    private final DataManager mDataManager;

    public RegisterController(DataManager mDataManager) {
        this.mDataManager = mDataManager;
    }

    public User register(String tag, String username, String password) {

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

    public Response onRegister(String tag, String username, String password, String confirmPassword) {
        if (tag.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            return new Response(false, "Tous les champs doivent être remplis !");
        }
        if (!password.equals(confirmPassword)) {
            return new Response(false, "Les mots de passe ne correspondent pas !");
        }

        User user = register(tag, username, password);
        if (user == null) {
            return new Response(false, "Échec de l'inscription !");
        }
        return new Response(true, "Inscription réussie ! Vous pouvez maintenant vous connecter !");
    }
}