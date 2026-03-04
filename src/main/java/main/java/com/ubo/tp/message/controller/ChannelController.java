package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.selection.Selection;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.template.component.ChannelListView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChannelController {
    private final DataManager dataManager;
    private final Selection selection;
    private ChannelListView channelListView;

    public ChannelController(DataManager dataManager, Selection selection) {
        this.dataManager = dataManager;
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
}
