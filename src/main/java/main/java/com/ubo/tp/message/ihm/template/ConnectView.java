package main.java.com.ubo.tp.message.ihm.template;

import main.java.com.ubo.tp.message.datamodel.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ConnectView extends JPanel {
    private final JPanel listPanel;

    public ConnectView() {
        super(new BorderLayout());

        listPanel = new JPanel(new GridBagLayout());
        listPanel.setBackground(new Color(0x474747));

        JScrollPane scroll = new JScrollPane(listPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);

        this.add(scroll, BorderLayout.CENTER);
    }

    public void setUsers(List<User> users) {
        if (users == null) users = new ArrayList<>();
        if (!SwingUtilities.isEventDispatchThread()) {
            final List<User> finalUsers = users;
            SwingUtilities.invokeLater(() -> setUsers(finalUsers));
            return;
        }

        listPanel.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        for (User u : users) {
            JPanel row = createUserRow(u);
            gbc.insets = new Insets(4, 6, 4, 6);
            listPanel.add(row, gbc);
            gbc.gridy++;
        }

        gbc.weighty = 1.0;
        JPanel filler = new JPanel();
        filler.setOpaque(false);
        listPanel.add(filler, gbc);

        listPanel.revalidate();
        listPanel.repaint();
    }

    public void addUser(User user) {
        if (user == null) return;
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> addUser(user));
            return;
        }

        int rowCount = listPanel.getComponentCount();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = Math.max(0, rowCount);
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(4, 6, 4, 6);

        JPanel row = createUserRow(user);
        listPanel.add(row, gbc);
        listPanel.revalidate();
        listPanel.repaint();
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

    private JPanel createUserRow(User user) {
        if (user == null) return null;
        JPanel row = new JPanel(new GridBagLayout());
        row.setBackground(Color.WHITE);
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        GridBagConstraints gbc = new GridBagConstraints();

        // Avatar
        JLabel avatar = new JLabel(getInitials(user));
        avatar.setOpaque(true);
        avatar.setBackground(new Color(0x2F80ED));
        avatar.setForeground(Color.WHITE);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(24, 24));
        avatar.setFont(avatar.getFont().deriveFont(Font.BOLD, 12f));
        avatar.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 8);
        gbc.anchor = GridBagConstraints.WEST;
        row.add(avatar, gbc);

        // Nom
        JLabel name = new JLabel(user.getName());
        name.setFont(name.getFont().deriveFont(Font.PLAIN, 13f));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        row.add(name, gbc);

        // Statut
        JLabel status = new JLabel();
        status.setOpaque(true);
        status.setPreferredSize(new Dimension(10, 10));
        status.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        status.setBackground(user.isOnline() ? new Color(0x2ECC71) : new Color(0x95A5A6));

        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 8, 0, 0);
        gbc.anchor = GridBagConstraints.EAST;
        row.add(status, gbc);

        row.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                row.setBackground(new Color(0xEEEEEE));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                row.setBackground(Color.WHITE);
            }
        });

        return row;
    }

    private String getInitials(User user) {
        if (user == null) return "?";
        String name = user.getName();
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }
}