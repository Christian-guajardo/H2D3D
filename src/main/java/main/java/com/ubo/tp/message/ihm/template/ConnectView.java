package main.java.com.ubo.tp.message.ihm.template;

import javax.swing.*;
import java.awt.*;

/**
 * Conteneur principal de la vue connectée.
 * Reçoit les composants déjà construits — ne crée rien.
 */
public class ConnectView extends JPanel {

    public ConnectView(UserListView userListView,
                       ChannelListView channelListView,
                       MessageListView messageListView,
                       MessageInputView messageInputView) {
        super(new BorderLayout());

        // Panneau gauche : canaux (haut) + utilisateurs (bas)
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(220, 0));
        channelListView.setPreferredSize(new Dimension(220, 200));
        leftPanel.add(channelListView, BorderLayout.NORTH);
        leftPanel.add(userListView,    BorderLayout.CENTER);

        // Panneau centre : messages + saisie
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(messageListView,  BorderLayout.CENTER);
        centerPanel.add(messageInputView, BorderLayout.SOUTH);

        add(leftPanel,   BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
    }
}