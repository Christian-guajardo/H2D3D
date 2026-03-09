package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.core.selection.Selection;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.template.component.ChannelListView;


public class ChannelController implements IDatabaseObserver {
    private final DataManager mDataManager;
    private final Selection selection;
    private ChannelListView channelListView;
    private Session session;

    public ChannelListView getChannelListView() {
        return channelListView;
    }

    public ChannelController(DataManager dataManager, Selection selection, Session session) {
        this.mDataManager = dataManager;
        this.selection = selection;
        this.session = session;
        this.channelListView = new ChannelListView();
        this.channelListView.addChannelSelectionListener(channel -> {
            selection.changeSelection(channel);
            channelListView.setSelectedChannel(channel);
        });
        this.channelListView.refreshChannel(mDataManager.getChannels());
        this.attachListeners();
    }

    private void attachListeners() {
        channelListView.addChannelSelectionListener(channel -> selection.changeSelection(channel));
        channelListView.addCreateChannelListener(createChannel -> {
            mDataManager.sendChannel(new Channel(this.session.getConnectedUser(), "nameChannel"));
        });
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
        this.attachListeners();
    }

    @Override
    public void notifyChannelDeleted(Channel deletedChannel) {
        this.channelListView.refreshChannel(mDataManager.getChannels());
        this.attachListeners();
    }

    @Override
    public void notifyChannelModified(Channel modifiedChannel) {
        this.channelListView.refreshChannel(mDataManager.getChannels());
        this.attachListeners();
    }
}
