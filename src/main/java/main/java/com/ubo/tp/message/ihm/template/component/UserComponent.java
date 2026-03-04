package main.java.com.ubo.tp.message.ihm.template.component;

import main.java.com.ubo.tp.message.datamodel.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * Composant graphique représentant un utilisateur dans une liste.
 * Expose addSelectListener() pour que le contrôleur s'abonne — sans référence retour.
 */
public class UserComponent extends JPanel {

    private static final Color BG_DEFAULT   = Color.WHITE;
    private static final Color BG_HOVER     = new Color(0xF5F5F5);
    private static final Color BG_SELECTED  = new Color(0xD6EAFF);

    private Consumer<User> onSelect;
    private final User user;
    private boolean selected;

    public User getUser() {
        return user;
    }

    public UserComponent(User user) {
        super(new GridBagLayout());
        this.user = user;
        this.selected = false;
        setOpaque(true);
        setBackground(BG_DEFAULT);
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
        nameLabel.setOpaque(false);
        add(nameLabel, new GridBagConstraints(1, 0,
                1, 1, 1.0, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                insets, 0, 0));

        JLabel tagLabel = new JLabel("@" + user.getUserTag());
        tagLabel.setFont(tagLabel.getFont().deriveFont(Font.PLAIN, 11f));
        tagLabel.setForeground(Color.GRAY);
        tagLabel.setOpaque(false);
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
        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (onSelect != null) onSelect.accept(user);
            }
            @Override public void mouseEntered(MouseEvent e) {
                if (!selected) setBackground(BG_HOVER);
            }
            @Override public void mouseExited(MouseEvent e) {
                if (!selected) setBackground(BG_DEFAULT);
            }
        });
    }

    /** Change l'état de sélection visuelle. */
    public void setSelected(boolean selected) {
        this.selected = selected;
        setBackground(selected ? BG_SELECTED : BG_DEFAULT);
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(super.getMaximumSize().width, getPreferredSize().height);
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