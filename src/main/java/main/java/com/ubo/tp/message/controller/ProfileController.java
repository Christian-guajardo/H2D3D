package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.Response;
import main.java.com.ubo.tp.message.datamodel.User;

public class ProfileController implements IDatabaseObserver {

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

    /**
     * Supprime le compte de l'utilisateur connecté.
     * Déconnecte la session puis supprime le fichier physique.
     *
     * @return une Response indiquant le succès ou l'échec.
     */
    public Response onDeleteAccount() {
        User connectedUser = session.getConnectedUser();

        if (connectedUser == null) {
            return new Response(false, "Aucun utilisateur connecté.");
        }

        // Déconnexion de la session avant suppression
        connectedUser.setOnline(false);
        session.disconnect();

        // Suppression du fichier physique (le WatchableDirectory met à jour la DB)
        boolean deleted = dataManager.deleteUser(connectedUser);
        if (!deleted) {
            return new Response(false, "Impossible de supprimer le compte : fichier introuvable ou erreur de suppression.");
        }

        return new Response(true, "Compte supprimé avec succès.");
    }

    @Override
    public void notifyMessageAdded(Message addedMessage) {

    }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {

    }

    @Override
    public void notifyMessageModified(Message modifiedMessage) {

    }

    @Override
    public void notifyUserAdded(User addedUser) {

    }

    @Override
    public void notifyUserDeleted(User deletedUser) {

    }

    @Override
    public void notifyUserModified(User modifiedUser) {

    }

    @Override
    public void notifyChannelAdded(Channel addedChannel) {

    }

    @Override
    public void notifyChannelDeleted(Channel deletedChannel) {

    }

    @Override
    public void notifyChannelModified(Channel modifiedChannel) {

    }
}
