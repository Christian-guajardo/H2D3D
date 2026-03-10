package main.java.com.ubo.tp.message.ihm.template.component;

import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class ChannelComponent extends JPanel {

    private static final Color BG_DEFAULT  = new Color(0x3A3A3A);
    private static final Color BG_HOVER    = new Color(0x505050);
    private static final Color BG_SELECTED = new Color(0x2F80ED);

    private Consumer<Channel> onSelect;
    private Consumer<Channel> onManageMembers;
    private final Channel channel;
    private boolean selected;
    private int notificationCount = 0;
    private final JLabel badgeLabel;

    public Channel getChannel() { return channel; }

    public ChannelComponent(Channel channel, User connectedUser) {
        super(new GridBagLayout());
        this.channel = channel;
        this.selected = false;
        setOpaque(true);
        setBackground(BG_DEFAULT);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x555555)));

        // ── Ligne 0 : icône | nom | badge | bouton ⚙ ──────────────────────────

        // Colonne 0 : icône #public / #private
        String etat = channel.isPrivate() ? "🔒" : "#";
        JLabel hashLabel = new JLabel(etat);
        hashLabel.setFont(hashLabel.getFont().deriveFont(Font.BOLD, 14f));
        hashLabel.setForeground(new Color(0xAAAAAA));
        hashLabel.setOpaque(false);
        add(hashLabel, new GridBagConstraints(0, 0,
                1, 1, 0, 0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(6, 10, 2, 4), 0, 0));

        // Colonne 1 : nom du canal (prend tout l'espace restant, tronqué si besoin)
        JLabel nameLabel = new JLabel(channel.getName());
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 13f));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setOpaque(false);
        nameLabel.setMinimumSize(new Dimension(0, nameLabel.getPreferredSize().height));
        add(nameLabel, new GridBagConstraints(1, 0,
                1, 1, 1.0, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(6, 0, 2, 4), 0, 0));

        // Colonne 2 : badge de notification
        badgeLabel = new JLabel("", SwingConstants.CENTER);
        badgeLabel.setOpaque(true);
        badgeLabel.setBackground(new Color(0xE74C3C));
        badgeLabel.setForeground(Color.WHITE);
        badgeLabel.setFont(badgeLabel.getFont().deriveFont(Font.BOLD, 10f));
        badgeLabel.setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 5));
        badgeLabel.setVisible(false);
        add(badgeLabel, new GridBagConstraints(2, 0,
                1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(6, 2, 2, 2), 0, 0));

        // Colonne 3 : bouton ⚙ (conditionnel)
        boolean isCreator = connectedUser != null && channel.getCreator().equals(connectedUser);
        boolean isMemberOfPrivate = channel.isPrivate() && connectedUser != null
                && channel.getUsers().contains(connectedUser);

        if (isCreator || isMemberOfPrivate) {
            JButton manageBtn = new JButton("⚙");
            manageBtn.setFont(manageBtn.getFont().deriveFont(Font.PLAIN, 11f));
            manageBtn.setForeground(new Color(0xBBBBBB));
            manageBtn.setBackground(new Color(0x555555));
            manageBtn.setBorderPainted(false);
            manageBtn.setFocusPainted(false);
            manageBtn.setPreferredSize(new Dimension(24, 22));
            manageBtn.setMinimumSize(new Dimension(24, 22));
            manageBtn.setMaximumSize(new Dimension(24, 22));
            manageBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            manageBtn.setToolTipText(isCreator ? "Gérer le canal" : "Quitter le canal");
            manageBtn.addActionListener(e -> {
                if (onManageMembers != null) onManageMembers.accept(channel);
            });
            add(manageBtn, new GridBagConstraints(3, 0,
                    1, 1, 0, 0,
                    GridBagConstraints.EAST, GridBagConstraints.NONE,
                    new Insets(4, 0, 2, 8), 0, 0));
        }

        // ── Ligne 1 : créateur (s'étend sur toutes les colonnes) ───────────────
        JLabel creatorLabel = new JLabel("par " + channel.getCreator().getName());
        creatorLabel.setFont(creatorLabel.getFont().deriveFont(Font.ITALIC, 10f));
        creatorLabel.setForeground(new Color(0x888888));
        creatorLabel.setOpaque(false);
        add(creatorLabel, new GridBagConstraints(0, 1,
                4, 1, 1.0, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 10, 6, 8), 0, 0));

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (onSelect != null) onSelect.accept(channel);
            }
            @Override public void mouseEntered(MouseEvent e) {
                if (!selected) setBackground(BG_HOVER);
            }
            @Override public void mouseExited(MouseEvent e) {
                if (!selected) setBackground(BG_DEFAULT);
            }
        });
    }

    /** Met à jour le badge de notification. 0 = badge caché. */
    public void setNotificationCount(int count) {
        this.notificationCount = count;
        if (count > 0) {
            badgeLabel.setText(count > 99 ? "99+" : String.valueOf(count));
            badgeLabel.setVisible(true);
        } else {
            badgeLabel.setVisible(false);
        }
        revalidate();
        repaint();
    }

    public int getNotificationCount() {
        return notificationCount;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        setBackground(selected ? BG_SELECTED : BG_DEFAULT);
        if (selected) setNotificationCount(0);
    }

    public boolean isSelected() { return selected; }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(super.getMaximumSize().width, getPreferredSize().height);
    }

    public void addSelectListener(Consumer<Channel> listener) { this.onSelect = listener; }
    public void addManageMembersListener(Consumer<Channel> listener) { this.onManageMembers = listener; }
}