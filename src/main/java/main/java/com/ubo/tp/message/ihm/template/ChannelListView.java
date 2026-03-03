package main.java.com.ubo.tp.message.ihm.template;

import main.java.com.ubo.tp.message.datamodel.Channel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Composant graphique de la liste des canaux.
 * Expose addChannelSelectListener() et addCreateChannelListener().
 */
public class ChannelListView extends JPanel {

    private final JPanel listPanel;
    private final JButton createButton;
    private final List<ActionListener> selectListeners = new ArrayList<>();

    public ChannelListView() {
        super(new BorderLayout());
        setBackground(new Color(0x2C2C2C));

        // En-tête
        JPanel header = new JPanel(new BorderLayout(5, 0));
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
        header.add(createButton, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(0x2C2C2C));

        JScrollPane scroll = new JScrollPane(listPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.setBackground(new Color(0x2C2C2C));
        add(scroll, BorderLayout.CENTER);
    }

    public void setChannels(List<Channel> channels) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> setChannels(channels));
            return;
        }
        listPanel.removeAll();
        for (Channel c : channels) addChannelRow(c);
        listPanel.revalidate();
        listPanel.repaint();
    }

    public void addChannel(Channel channel) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> addChannel(channel));
            return;
        }
        addChannelRow(channel);
        listPanel.revalidate();
        listPanel.repaint();
    }

    private void addChannelRow(Channel channel) {
        ChannelComponent comp = new ChannelComponent(channel);
        for (ActionListener l : selectListeners) {
            comp.addSelectListener(e -> l.actionPerformed(
                    new java.awt.event.ActionEvent(channel,
                            java.awt.event.ActionEvent.ACTION_PERFORMED,
                            channel.getUuid().toString())));
        }
        listPanel.add(comp);
    }

    /**
     * Le contrôleur s'abonne à la sélection d'un canal.
     * La commande de l'ActionEvent contient l'UUID du canal.
     */
    public void addChannelSelectListener(ActionListener listener) {
        selectListeners.add(listener);
    }

    /**
     * Le contrôleur s'abonne à la création d'un nouveau canal.
     */
    public void addCreateChannelListener(ActionListener listener) {
        createButton.addActionListener(listener);
    }

    public void clear() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::clear);
            return;
        }
        listPanel.removeAll();
        listPanel.revalidate();
        listPanel.repaint();
    }
}