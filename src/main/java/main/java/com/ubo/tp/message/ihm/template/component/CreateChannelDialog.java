package main.java.com.ubo.tp.message.ihm.template.component;

import main.java.com.ubo.tp.message.datamodel.User;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Boîte de dialogue pour créer un canal.
 * Retourne null si annulé, sinon un objet CreateChannelResult.
 */
public class CreateChannelDialog extends JDialog {

    public static class CreateChannelResult {
        public final String name;
        public final boolean isPrivate;
        public final List<User> members;

        public CreateChannelResult(String name, boolean isPrivate, List<User> members) {
            this.name = name;
            this.isPrivate = isPrivate;
            this.members = members;
        }
    }

    private JTextField nameField;
    private JCheckBox privateCheckBox;
    private JPanel membersPanel;
    private final List<JCheckBox> memberCheckBoxes = new ArrayList<>();
    private final List<User> availableUsers;

    private CreateChannelResult result = null;

    public CreateChannelDialog(Frame parent, Set<User> availableUsers) {
        super(parent, "Créer un canal", true);
        this.availableUsers = new ArrayList<>(availableUsers);
        initComponents();
        pack();
        setLocationRelativeTo(parent);
        setMinimumSize(new Dimension(350, 300));
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // --- Formulaire ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        Insets insets = new Insets(5, 5, 5, 5);

        // Nom
        formPanel.add(new JLabel("Nom du canal :"),
                new GridBagConstraints(0, 0, 1, 1, 0, 0,
                        GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        nameField = new JTextField(20);
        formPanel.add(nameField,
                new GridBagConstraints(1, 0, 1, 1, 1, 0,
                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

        // Privé
        privateCheckBox = new JCheckBox("Canal privé");
        privateCheckBox.setFont(privateCheckBox.getFont().deriveFont(Font.PLAIN));
        formPanel.add(privateCheckBox,
                new GridBagConstraints(0, 1, 2, 1, 0, 0,
                        GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));

        // Panel membres (visible seulement si privé)
        membersPanel = new JPanel();
        membersPanel.setLayout(new BoxLayout(membersPanel, BoxLayout.Y_AXIS));
        membersPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Membres",
                TitledBorder.LEFT, TitledBorder.TOP));

        for (User u : availableUsers) {
            JCheckBox cb = new JCheckBox(u.getName() + "  (@" + u.getUserTag() + ")");
            cb.putClientProperty("user", u);
            memberCheckBoxes.add(cb);
            membersPanel.add(cb);
        }

        JScrollPane membersScroll = new JScrollPane(membersPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        membersScroll.setPreferredSize(new Dimension(300, 150));
        membersScroll.setVisible(false); // caché par défaut

        // Afficher/cacher la liste quand on coche "privé"
        privateCheckBox.addActionListener(e -> {
            membersScroll.setVisible(privateCheckBox.isSelected());
            pack();
        });

        formPanel.add(membersScroll,
                new GridBagConstraints(0, 2, 2, 1, 1, 1,
                        GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

        add(formPanel, BorderLayout.CENTER);

        // --- Boutons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));

        JButton cancelButton = new JButton("Annuler");
        cancelButton.addActionListener(e -> dispose());

        JButton createButton = new JButton("Créer");
        createButton.setBackground(new Color(0x2F80ED));
        createButton.setForeground(Color.WHITE);
        createButton.setBorderPainted(false);
        createButton.addActionListener(e -> onConfirm());

        buttonPanel.add(cancelButton);
        buttonPanel.add(createButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void onConfirm() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le nom du canal ne peut pas être vide.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean isPrivate = privateCheckBox.isSelected();
        List<User> selectedMembers = new ArrayList<>();

        if (isPrivate) {
            for (JCheckBox cb : memberCheckBoxes) {
                if (cb.isSelected()) {
                    selectedMembers.add((User) cb.getClientProperty("user"));
                }
            }
        }

        result = new CreateChannelResult(name, isPrivate, selectedMembers);
        dispose();
    }

    /** Retourne le résultat, ou null si annulé. */
    public CreateChannelResult getResult() {
        return result;
    }

    /**
     * Méthode statique utilitaire pour afficher le dialog et récupérer le résultat.
     */
    public static CreateChannelResult show(Frame parent, Set<User> availableUsers) {
        CreateChannelDialog dialog = new CreateChannelDialog(parent, availableUsers);
        dialog.setVisible(true);
        return dialog.getResult();
    }
}
