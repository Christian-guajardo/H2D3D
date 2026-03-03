package main.java.com.ubo.tp.message.ihm.template.component;

import main.java.com.ubo.tp.message.datamodel.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Composant graphique représentant un utilisateur dans une liste.
 * Expose addSelectListener() pour que le contrôleur s'abonne — sans référence retour.
 */
public class UserComponent extends JPanel {

    private final JButton selectButton;

    public UserComponent(User user) {
        super(new GridBagLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xEEEEEE)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);

        // Avatar
        JLabel avatar = new JLabel(getInitials(user));
        avatar.setOpaque(true);
        avatar.setBackground(new Color(0x2F80ED));
        avatar.setForeground(Color.WHITE);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(36, 36));
        avatar.setFont(avatar.getFont().deriveFont(Font.BOLD, 13f));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(avatar, gbc);

        // Nom
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridheight = 1;
        gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel nameLabel = new JLabel(user.getName());
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 13f));
        add(nameLabel, gbc);

        // Tag
        gbc.gridy = 1;
        JLabel tagLabel = new JLabel("@" + user.getUserTag());
        tagLabel.setFont(tagLabel.getFont().deriveFont(Font.PLAIN, 11f));
        tagLabel.setForeground(Color.GRAY);
        add(tagLabel, gbc);

        // Indicateur online
        gbc.gridx = 2; gbc.gridy = 0; gbc.gridheight = 2;
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        JLabel status = new JLabel();
        status.setOpaque(true);
        status.setPreferredSize(new Dimension(10, 10));
        status.setBackground(user.isOnline() ? new Color(0x2ECC71) : new Color(0x95A5A6));
        status.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        add(status, gbc);

        // Bouton de sélection (invisible — déclenché par clic sur la ligne)
        selectButton = new JButton();
        selectButton.setVisible(false);
        add(selectButton);

        // Clic sur toute la ligne déclenche le bouton
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                selectButton.doClick();
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
    public void addSelectListener(ActionListener listener) {
        selectButton.addActionListener(listener);
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