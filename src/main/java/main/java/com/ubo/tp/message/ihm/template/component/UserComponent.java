package main.java.com.ubo.tp.message.ihm.template.component;

import main.java.com.ubo.tp.message.datamodel.User;
import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Composant graphique représentant un utilisateur dans une liste.
 * Expose addSelectListener() pour que le contrôleur s'abonne — sans référence retour.
 */
public class UserComponent extends JPanel {

    private Consumer<User> onSelect;
    private final User user;

    public User getUser() {
        return user;
    }

    public UserComponent(User user) {
        super(new GridBagLayout());
        this.user = user;
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xEEEEEE)));

        Insets insets = new Insets(6, 8, 6, 8);

        JLabel avatar = new JLabel(getInitials(user));
        avatar.setOpaque(true);
        avatar.setBackground(new Color(0x2F80ED));
        avatar.setForeground(Color.WHITE);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(36, 36));
        avatar.setFont(avatar.getFont().deriveFont(Font.BOLD, 13f));
        add(avatar, new GridBagConstraints(0, 0,
                1, 2, 0, 0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                insets, 0, 0));

        JLabel nameLabel = new JLabel(user.getName());
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 13f));
        add(nameLabel, new GridBagConstraints(1, 0,
                1, 1, 1.0, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                insets, 0, 0));

        JLabel tagLabel = new JLabel("@" + user.getUserTag());
        tagLabel.setFont(tagLabel.getFont().deriveFont(Font.PLAIN, 11f));
        tagLabel.setForeground(Color.GRAY);
        add(tagLabel, new GridBagConstraints(1, 1,
                1, 1, 1.0, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                insets, 0, 0));

        JLabel status = new JLabel();
        status.setOpaque(true);
        status.setPreferredSize(new Dimension(10, 10));
        status.setBackground(user.isOnline() ? new Color(0x2ECC71) : new Color(0x95A5A6));
        status.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        add(status, new GridBagConstraints(2, 0,
                1, 2, 0, 0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                insets, 0, 0));

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (onSelect != null) onSelect.accept(user);
            }
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                setBackground(new Color(0xF5F5F5));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                setBackground(Color.WHITE);
            }
        });
    }

    /**
     * Le contrôleur s'abonne ici. La vue ne connaît pas le contrôleur.
     */
    public void addSelectListener(Consumer<User> listener) {
        this.onSelect = listener;
    }

    private String getInitials(User user) {
        String name = user.getName();
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1)
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }
}