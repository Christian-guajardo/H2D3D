package main.java.com.ubo.tp.message.core.database;

import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;

public class DatabaseObserver implements IDatabaseObserver{
    @Override
    public void notifyMessageAdded(Message addedMessage) {
        System.out.println("[DATABASE] Message ajouté: " + addedMessage.getText());
    }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {
        System.out.println("[DATABASE] Message supprimé: " + deletedMessage.getText());
    }

    @Override
    public void notifyMessageModified(Message modifiedMessage) {
        System.out.println("[DATABASE] Message modifié: " + modifiedMessage.getText());
    }

    @Override
    public void notifyUserAdded(User addedUser) {
        System.out.println("[DATABASE] Utilisateur ajouté: " + addedUser.getName());
    }

    @Override
    public void notifyUserDeleted(User deletedUser) {
        System.out.println("[DATABASE] Utilisateur supprimé: " + deletedUser.getName());
    }

    @Override
    public void notifyUserModified(User modifiedUser) {
        System.out.println("[DATABASE] Utilisateur modifié: " + modifiedUser.getName());
    }

    @Override
    public void notifyChannelAdded(Channel addedChannel) {
        System.out.println("[DATABASE] Canal ajouté: " + addedChannel.getName());
    }

    @Override
    public void notifyChannelDeleted(Channel deletedChannel) {
        System.out.println("[DATABASE] Canal supprimé: " + deletedChannel.getName());
    }

    @Override
    public void notifyChannelModified(Channel modifiedChannel) {
        System.out.println("[DATABASE] Canal modifié: " + modifiedChannel.getName());
    }
}
