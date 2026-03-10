package main.java.com.ubo.tp.message.ihm.template.component;

import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.User;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Boîte de dialogue pour gérer les membres d'un canal existant.
 * Accessible uniquement au créateur du canal.
 * Retourne la nouvelle liste de membres, ou null si annulé.
 */
public class ManageChannelMembersDialog extends JDialog {

    private final List<JCheckBox> memberCheckBoxes = new ArrayList<>();
    private List<User> result = null;

    public ManageChannelMembersDialog(Frame parent, Channel channel, Set<User> allUsers) {
        super(parent, "Gérer les membres — #" + channel.getName(), true);
        initComponents(channel, new ArrayList<>(allUsers));
        pack();
        setLocationRelativeTo(parent);
        setMinimumSize(new Dimension(350, 300));
    }

    private void initComponents(Channel channel, List<User> allUsers) {
        setLayout(new BorderLayout(10, 10));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Info canal
        JLabel infoLabel = new JLabel("<html>Canal : <b>#" + channel.getName() + "</b> "
                + (channel.isPrivate() ? "(privé)" : "(public)") + "</html>");
        infoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        add(infoLabel, BorderLayout.NORTH);

        // Liste des utilisateurs avec cases à cocher
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Membres",
                TitledBorder.LEFT, TitledBorder.TOP));

        List<User> currentMembers = channel.getUsers();

        for (User u : allUsers) {
            // Ne pas afficher le créateur (il est toujours membre implicitement)
            if (u.equals(channel.getCreator())) continue;

            boolean isMember = currentMembers.contains(u);
            JCheckBox cb = new JCheckBox(u.getName() + "  (@" + u.getUserTag() + ")", isMember);
            cb.putClientProperty("user", u);
            memberCheckBoxes.add(cb);
            listPanel.add(cb);
        }

        JScrollPane scroll = new JScrollPane(listPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setPreferredSize(new Dimension(300, 200));
        add(scroll, BorderLayout.CENTER);

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));

        JButton cancelButton = new JButton("Annuler");
        cancelButton.addActionListener(e -> dispose());

        JButton saveButton = new JButton("Enregistrer");
        saveButton.setBackground(new Color(0x2F80ED));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorderPainted(false);
        saveButton.addActionListener(e -> onConfirm());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void onConfirm() {
        result = new ArrayList<>();
        for (JCheckBox cb : memberCheckBoxes) {
            if (cb.isSelected()) {
                result.add((User) cb.getClientProperty("user"));
            }
        }
        dispose();
    }

    /** Retourne la nouvelle liste de membres (sans le créateur), ou null si annulé. */
    public List<User> getResult() {
        return result;
    }

    public static List<User> show(Frame parent, Channel channel, Set<User> allUsers) {
        ManageChannelMembersDialog dialog = new ManageChannelMembersDialog(parent, channel, allUsers);
        dialog.setVisible(true);
        return dialog.getResult();
    }
}
