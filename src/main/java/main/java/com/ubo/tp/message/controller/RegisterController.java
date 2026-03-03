package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.ihm.template.RegisterView;
import main.java.com.ubo.tp.message.datamodel.User;

import java.util.Set;

public class RegisterController {
    private final DataManager mDataManager;

    public RegisterController(DataManager mDataManager) {
        this.mDataManager = mDataManager;
    }

    public void onRegister(RegisterView rv) {
        if (!rv.getConfirmPassword().equals(rv.getPassword())) {
            rv.showError("Erreur", "Les mots de passe ne correspondent pas.");
            return;
        }
        User user = register(rv.getTag(), rv.getUsername(), rv.getPassword());
        if (user == null) {
            rv.showError("Erreur", "Échec de l'inscription. Le tag est peut-être déjà utilisé ou les champs sont invalides.");
        } else {
            rv.showInfo("Inscription réussie ! Vous pouvez maintenant vous connecter.");
        }
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
}