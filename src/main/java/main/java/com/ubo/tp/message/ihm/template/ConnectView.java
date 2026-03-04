package main.java.com.ubo.tp.message.ihm.template;

import main.java.com.ubo.tp.message.controller.ConnectController;
import main.java.com.ubo.tp.message.ihm.template.component.ChannelListView;
import main.java.com.ubo.tp.message.ihm.template.component.MessageInputView;
import main.java.com.ubo.tp.message.ihm.template.component.MessageListView;
import main.java.com.ubo.tp.message.ihm.template.component.UserListView;

import javax.swing.*;
import java.awt.*;

/**
 * Conteneur principal de la vue connectée.
 * Reçoit les composants déjà construits — ne crée rien.
 */
public class ConnectView extends JPanel {
    private final UserListView userListView;
    private final ChannelListView channelListView;
    private final MessageListView messageListView;
    private final MessageInputView messageInputView;
    private final ConnectController connectController;

    public ConnectView(UserListView userListView,
                       ChannelListView channelListView,
                       MessageListView messageListView,
                       MessageInputView messageInputView,
                       ConnectController connectController) {

        super(new BorderLayout());
        this.userListView = userListView;
        this.channelListView = channelListView;
        this.messageListView = messageListView;
        this.messageInputView = messageInputView;
        this.connectController = connectController;
        setOpaque(true);
        setBackground(Color.WHITE);
        initGui();
    }

    private void initGui() {
        // Panneau gauche : canaux (haut) + utilisateurs (bas)
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(true);
        leftPanel.setBackground(new Color(0x2C2C2C));
        leftPanel.setPreferredSize(new Dimension(220, 0));
        channelListView.setPreferredSize(new Dimension(220, 200));
        leftPanel.add(channelListView, BorderLayout.NORTH);
        leftPanel.add(userListView,    BorderLayout.CENTER);

        // Panneau centre : messages + saisie
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(true);
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(messageListView,  BorderLayout.CENTER);
        centerPanel.add(messageInputView, BorderLayout.SOUTH);

        add(leftPanel,   BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
    }
}