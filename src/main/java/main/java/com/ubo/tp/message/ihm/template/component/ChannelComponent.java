package main.java.com.ubo.tp.message.ihm.template.component;

import main.java.com.ubo.tp.message.datamodel.Channel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * Composant graphique représentant un canal.
 * Expose addSelectListener() et addManageMembersListener().
 */
public class ChannelComponent extends JPanel {

    private static final Color BG_DEFAULT  = new Color(0x3A3A3A);
    private static final Color BG_HOVER    = new Color(0x505050);
    private static final Color BG_SELECTED = new Color(0x2F80ED);

    private Consumer<Channel> onSelect;
    private Consumer<Channel> onManageMembers;
    private final Channel channel;
    private boolean selected;

    public Channel getChannel() { return channel; }

    public ChannelComponent(Channel channel) {
        super(new GridBagLayout());
        this.channel = channel;
        this.selected = false;
        setOpaque(true);
        setBackground(BG_DEFAULT);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x555555)));

        Insets insets = new Insets(6, 10, 6, 4);

        // Icône "#"
        JLabel hashLabel = new JLabel("#");
        hashLabel.setFont(hashLabel.getFont().deriveFont(Font.BOLD, 16f));
        hashLabel.setForeground(new Color(0xAAAAAA));
        hashLabel.setOpaque(false);
        add(hashLabel, new GridBagConstraints(0, 0,
                1, 1, 0, 0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                insets, 0, 0));

        // Nom du canal
        JLabel nameLabel = new JLabel(channel.getName());
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.PLAIN, 13f));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setOpaque(false);
        add(nameLabel, new GridBagConstraints(1, 0,
                1, 1, 1.0, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                insets, 0, 0));

        // Créateur
        JLabel creatorLabel = new JLabel(channel.getCreator().getName());
        creatorLabel.setFont(creatorLabel.getFont().deriveFont(Font.ITALIC, 10f));
        creatorLabel.setForeground(new Color(0x888888));
        creatorLabel.setOpaque(false);
        add(creatorLabel, new GridBagConstraints(2, 0,
                1, 1, 0, 0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(6, 4, 6, 4), 0, 0));

        // Bouton ⚙ (gestion membres) — visible uniquement si canal privé
        if (channel.isPrivate()) {
            JButton manageBtn = new JButton("⚙");
            manageBtn.setFont(manageBtn.getFont().deriveFont(Font.PLAIN, 11f));
            manageBtn.setForeground(new Color(0xBBBBBB));
            manageBtn.setBackground(new Color(0x555555));
            manageBtn.setBorderPainted(false);
            manageBtn.setPreferredSize(new Dimension(24, 22));
            manageBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            manageBtn.setToolTipText("Gérer les membres");
            manageBtn.addActionListener(e -> {
                if (onManageMembers != null) onManageMembers.accept(channel);
            });
            add(manageBtn, new GridBagConstraints(3, 0,
                    1, 1, 0, 0,
                    GridBagConstraints.EAST, GridBagConstraints.NONE,
                    new Insets(4, 0, 4, 6), 0, 0));
        }

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

    public void setSelected(boolean selected) {
        this.selected = selected;
        setBackground(selected ? BG_SELECTED : BG_DEFAULT);
    }

    public boolean isSelected() { return selected; }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(super.getMaximumSize().width, getPreferredSize().height);
    }

    public void addSelectListener(Consumer<Channel> listener) {
        this.onSelect = listener;
    }

    public void addManageMembersListener(Consumer<Channel> listener) {
        this.onManageMembers = listener;
    }
}