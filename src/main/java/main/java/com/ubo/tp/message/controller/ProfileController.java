package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.Response;
import main.java.com.ubo.tp.message.datamodel.User;

public class ProfileController {

    private final DataManager dataManager;
    private final Session session;

    public ProfileController(DataManager dataManager, Session session) {
        this.dataManager = dataManager;
        this.session = session;
    }

    public Response onUpdateUsername(String newName) {
        User connectedUser = session.getConnectedUser();

        if (connectedUser == null) {
            return new Response(false, "Aucun utilisateur connecté.");
        }

        if (newName == null || newName.trim().isEmpty()) {
            return new Response(false, "Le nom d'utilisateur ne peut pas être vide.");
        }

        String trimmedName = newName.trim();

        if (trimmedName.equals(connectedUser.getName())) {
            return new Response(false, "Le nouveau nom est identique à l'ancien.");
        }

        connectedUser.setName(trimmedName);
        dataManager.sendUser(connectedUser);

        return new Response(true, "Nom d'utilisateur modifié avec succès !");
    }

    public User getConnectedUser() {
        return session.getConnectedUser();
    }
}

