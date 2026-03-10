package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.common.Constants;
import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.core.selection.Selection;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.template.component.ChannelListView;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ChannelController implements IDatabaseObserver {
    private final DataManager mDataManager;
    private final Selection selection;
    private final ChannelListView channelListView;
    private final Session session;

    public ChannelListView getChannelListView() {
        return channelListView;
    }

    public ChannelController(DataManager dataManager, Selection selection, Session session) {
        this.mDataManager = dataManager;
        this.selection = selection;
        this.session = session;
        this.channelListView = new ChannelListView();

        // Sélection d'un canal
        this.channelListView.setOnChannelSelected(channel -> {
            selection.changeSelection(channel);
            channelListView.setSelectedChannel(channel);
        });

        // Création : la vue ouvre le dialog, renvoie (name, isPrivate, members)
        this.channelListView.setOnCreateChannel((name, isPrivate, members) ->
                mDataManager.sendChannel(new Channel(
                        UUID.randomUUID(),
                        session.getConnectedUser(),
                        name,
                        members,
                        isPrivate
                ))
        );

        // Gestion membres : la vue ouvre le dialog, renvoie la nouvelle liste
        this.channelListView.setOnManageMembers((channel, newMembers) ->
                mDataManager.sendChannel(new Channel(
                        channel.getUuid(),
                        channel.getCreator(),
                        channel.getName(),
                        newMembers,
                        channel.isPrivate()
                ))
        );

        // Quitter un canal
        this.channelListView.setOnLeaveChannel(channel -> {
            List<User> newMembers = channel.getUsers().stream()
                    .filter(u -> !u.equals(session.getConnectedUser()))
                    .collect(Collectors.toList());
            mDataManager.sendChannel(new Channel(
                    channel.getUuid(),
                    channel.getCreator(),
                    channel.getName(),
                    newMembers,
                    channel.isPrivate()
            ));
        });

        // Supprimer un canal (créateur uniquement — la vue vérifie déjà via connectedUserProvider)
        this.channelListView.setOnDeleteChannel(channel -> mDataManager.deleteChannel(channel));

        // Fournisseurs de données pour que la vue peuple ses dialogs sans connaître le controller
        this.channelListView.setUsersProvider(() ->
                mDataManager.getUsers().stream()
                        .filter(u -> !u.getUuid().equals(Constants.UNKNONWN_USER_UUID))
                        .filter(u -> !u.equals(session.getConnectedUser()))
                        .collect(Collectors.toSet())
        );
        this.channelListView.setConnectedUserProvider(() -> session.getConnectedUser());
    }

    /**
     * - Créateur : voit toujours son canal
     * - Canal privé : seulement si l'user est dans la liste des membres
     * - Canal public : visible par tous
     *
     * FIX: guard null sur me (au login la session peut ne pas encore être établie)
     */
    public Set<Channel> getFilteredChannels() {
        User me = session.getConnectedUser();
        if (me == null) return Set.of();
        return mDataManager.getChannels().stream()
                .filter(channel -> {
                    if (channel.getCreator().equals(me)) return true;
                    if (channel.isPrivate()) return channel.getUsers().contains(me);
                    return true; // public
                })
                .collect(Collectors.toSet());
    }

    public void refreshChannels(){
        this.channelListView.refreshChannel(this.getFilteredChannels());
    }

    @Override public void notifyMessageAdded(Message addedMessage) {}
    @Override public void notifyMessageDeleted(Message deletedMessage) {}
    @Override public void notifyMessageModified(Message modifiedMessage) {}
    @Override public void notifyUserAdded(User addedUser) {}
    @Override public void notifyUserDeleted(User deletedUser) {}
    @Override public void notifyUserModified(User modifiedUser) {}

    @Override
    public void notifyChannelAdded(Channel addedChannel) {
        this.channelListView.refreshChannel(this.getFilteredChannels());
    }

    @Override
    public void notifyChannelDeleted(Channel deletedChannel) {
        this.channelListView.refreshChannel(this.getFilteredChannels());
    }

    @Override
    public void notifyChannelModified(Channel modifiedChannel) {
        this.channelListView.refreshChannel(this.getFilteredChannels());
    }
}