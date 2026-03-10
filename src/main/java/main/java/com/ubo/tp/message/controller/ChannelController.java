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
import main.java.com.ubo.tp.message.ihm.template.component.CreateChannelDialog;
import main.java.com.ubo.tp.message.ihm.template.component.ManageChannelMembersDialog;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;
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

        this.channelListView.setOnChannelSelected(channel -> {
            selection.changeSelection(channel);
            channelListView.setSelectedChannel(channel);
        });


        this.channelListView.addCreateChannelListener(e -> openCreateChannelDialog());


        this.channelListView.setOnManageMembers(this::openManageMembersDialog);

        this.channelListView.refreshChannel(this.getFilteredChannels());

    }

    /** Ouvre le dialog de création de canal. */
    private void openCreateChannelDialog() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(channelListView);

        Set<User> availableUsers = mDataManager.getUsers().stream()
                .filter(u -> !u.getUuid().equals(Constants.UNKNONWN_USER_UUID))
                .filter(u -> !u.equals(session.getConnectedUser()))
                .collect(Collectors.toSet());

        CreateChannelDialog.CreateChannelResult result =
                CreateChannelDialog.show(parentFrame, availableUsers);

        if (result != null) {
            Channel newChannel = new Channel(
                    session.getConnectedUser(),
                    result.name,
                    result.members
            );

            if (result.isPrivate && result.members.isEmpty()) {
                Channel privateEmpty = new Channel(
                        java.util.UUID.randomUUID(),
                        session.getConnectedUser(),
                        result.name,
                        result.members,
                        true
                );
                mDataManager.sendChannel(privateEmpty);
            } else {
                mDataManager.sendChannel(newChannel);
            }
        }
    }

    /** Ouvre le dialog de gestion des membres (créateur uniquement). */
    private void openManageMembersDialog(Channel channel) {
        if (!channel.getCreator().equals(session.getConnectedUser())) {
            JOptionPane.showMessageDialog(channelListView,
                    "Seul le créateur du canal peut gérer les membres.",
                    "Accès refusé", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(channelListView);

        Set<User> allUsers = mDataManager.getUsers().stream()
                .filter(u -> !u.getUuid().equals(Constants.UNKNONWN_USER_UUID))
                .filter(u -> !u.equals(session.getConnectedUser()))
                .collect(Collectors.toSet());

        List<User> newMembers = ManageChannelMembersDialog.show(parentFrame, channel, allUsers);

        if (newMembers != null) {
            // Recréer le canal avec la nouvelle liste (même UUID)
            Channel updated = new Channel(
                    channel.getUuid(),
                    channel.getCreator(),
                    channel.getName(),
                    newMembers,
                    channel.isPrivate() || !newMembers.isEmpty()
            );
            mDataManager.sendChannel(updated);
        }
    }

    public Set<Channel> getFilteredChannels() {
        return mDataManager.getChannels().stream()
                .filter(channel ->
                        channel.getUsers().contains(session.getConnectedUser())
                                || channel.getCreator().equals(session.getConnectedUser())
                                || !channel.isPrivate())
                .collect(Collectors.toSet());
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