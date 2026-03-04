package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.core.selection.Selection;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.template.component.ChannelListView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChannelController implements IDatabaseObserver {
    private final DataManager mDataManager;
    private final Selection selection;
    private ChannelListView channelListView;

    public ChannelController(DataManager dataManager, Selection selection) {
        this.mDataManager = dataManager;
        this.selection = selection;
        this.channelListView = new ChannelListView();
        this.attachListeners();
    }

    public void changeCurrentSelection(Channel channel) {
        selection.changeSelection(channel);
    }

    private void attachListeners() {
        channelListView.addChannelSelectionListener(channel -> changeCurrentSelection(channel));
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
        this.channelListView.refreshChannel(mDataManager.getChannels());
    }

    @Override
    public void notifyChannelDeleted(Channel deletedChannel) {
        this.channelListView.refreshChannel(mDataManager.getChannels());
    }

    @Override
    public void notifyChannelModified(Channel modifiedChannel) {
        this.channelListView.refreshChannel(mDataManager.getChannels());
    }
}
