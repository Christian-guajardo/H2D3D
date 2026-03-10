package main.java.com.ubo.tp.message.ihm.template.component;

import main.java.com.ubo.tp.message.datamodel.Channel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class ChannelListView extends JPanel {

    private final JPanel listPanel;
    private final JButton createButton;
    private final List<ChannelComponent> channelComponents = new ArrayList<>();

    // Stockés une fois, appliqués à chaque nouveau composant
    private Consumer<Channel> onChannelSelected;
    private Consumer<Channel> onManageMembers;

    public ChannelListView() {
        super(new BorderLayout());
        setOpaque(true);
        setBackground(new Color(0x2C2C2C));

        JPanel header = new JPanel(new BorderLayout(5, 0));
        header.setOpaque(true);
        header.setBackground(new Color(0x2C2C2C));
        header.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JLabel title = new JLabel("Canaux");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 13f));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        createButton = new JButton("+");
        createButton.setFont(createButton.getFont().deriveFont(Font.BOLD, 14f));
        createButton.setForeground(Color.WHITE);
        createButton.setBackground(new Color(0x2F80ED));
        createButton.setBorderPainted(false);
        createButton.setPreferredSize(new Dimension(28, 24));
        createButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        createButton.setToolTipText("Créer un canal");
        header.add(createButton, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(true);
        listPanel.setBackground(new Color(0x2C2C2C));

        JScrollPane scroll = new JScrollPane(listPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.setOpaque(true);
        scroll.setBackground(new Color(0x2C2C2C));
        scroll.getViewport().setOpaque(true);
        scroll.getViewport().setBackground(new Color(0x2C2C2C));
        add(scroll, BorderLayout.CENTER);
    }

    /** Enregistre le listener de sélection (une seule fois). */
    public void setOnChannelSelected(Consumer<Channel> listener) {
        this.onChannelSelected = listener;
    }

    /** Enregistre le listener de gestion des membres (une seule fois). */
    public void setOnManageMembers(Consumer<Channel> listener) {
        this.onManageMembers = listener;
    }

    public void refreshChannel(Set<Channel> channels) {
        listPanel.removeAll();
        channelComponents.clear();
        for (Channel c : channels) addChannelRow(c);
        listPanel.add(Box.createVerticalGlue());
        listPanel.revalidate();
        listPanel.repaint();
    }

    private void addChannelRow(Channel channel) {
        ChannelComponent comp = new ChannelComponent(channel);
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, comp.getPreferredSize().height));

        if (onChannelSelected != null) {
            comp.addSelectListener(onChannelSelected);
        }
        if (onManageMembers != null) {
            comp.addManageMembersListener(onManageMembers);
        }

        channelComponents.add(comp);
        listPanel.add(comp);
    }

    public void setSelectedChannel(Channel channel) {
        for (ChannelComponent comp : channelComponents) {
            comp.setSelected(comp.getChannel().equals(channel));
        }
    }

    public void addCreateChannelListener(ActionListener listener) {
        createButton.addActionListener(listener);
    }

    public void clear() {
        listPanel.removeAll();
        channelComponents.clear();
        listPanel.revalidate();
        listPanel.repaint();
    }

    /** @deprecated Utiliser setOnChannelSelected() */
    @Deprecated
    public void addChannelSelectionListener(Consumer<Channel> listener) {
        this.onChannelSelected = listener;
        for (ChannelComponent comp : channelComponents) {
            comp.addSelectListener(listener);
        }
    }
}