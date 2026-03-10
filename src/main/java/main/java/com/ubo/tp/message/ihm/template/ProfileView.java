package main.java.com.ubo.tp.message.ihm.template;

import main.java.com.ubo.tp.message.controller.ProfileController;
import main.java.com.ubo.tp.message.datamodel.Response;
import main.java.com.ubo.tp.message.datamodel.User;

import javax.swing.*;
import java.awt.*;

/**
 * Vue permettant à l'utilisateur connecté de modifier son nom d'utilisateur.
 */
public class ProfileView extends JPanel {

    private final ProfileController profileController;
    private final Runnable onBack;
    private JTextField nameField;
    private JButton saveButton;
    private JButton backButton;
    private JButton deleteButton;

    public ProfileView(ProfileController profileController, Runnable onBack) {
        super(new GridBagLayout());
        this.profileController = profileController;
        this.onBack = onBack;
        initComponents();
    }

    private void initComponents() {
        User user = profileController.getConnectedUser();

        nameField = new JTextField(user != null ? user.getName() : "", 20);
        saveButton = new JButton("Modifier");
        backButton = new JButton("Retour");
        deleteButton = new JButton("Supprimer mon compte");
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setBackground(new Color(0xC0392B));
        deleteButton.setOpaque(true);
        deleteButton.setBorderPainted(false);

        saveButton.addActionListener(e -> {
            Response result = profileController.onUpdateUsername(nameField.getText());
            if (result.isSuccess()) {
                showInfo(result.getMessage());
            } else {
                showError("Erreur", result.getMessage());
            }
        });

        backButton.addActionListener(e -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Êtes-vous sûr de vouloir supprimer votre compte ?\nCette action est irréversible.",
                    "Confirmer la suppression",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                profileController.onDeleteAccount();
            }
        });

        setupLayout(user);
    }

    private void setupLayout(User user) {
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        Insets insets = new Insets(5, 5, 5, 5);

        // Titre
        JLabel titleLabel = new JLabel("Modifier le profil");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        this.add(titleLabel, new GridBagConstraints(0, 0,
                2, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 5, 15, 5), 0, 0));

        // Tag (lecture seule)
        this.add(new JLabel("Tag:"), new GridBagConstraints(0, 1,
                1, 1, 0, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                insets, 0, 0));

        String tagText = user != null ? "@" + user.getUserTag() : "";
        JLabel tagValue = new JLabel(tagText);
        tagValue.setFont(tagValue.getFont().deriveFont(Font.ITALIC));
        this.add(tagValue, new GridBagConstraints(1, 1,
                1, 1, 1, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                insets, 0, 0));

        // Nom (modifiable)
        this.add(new JLabel("Nom:"), new GridBagConstraints(0, 2,
                1, 1, 0, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                insets, 0, 0));

        this.add(nameField, new GridBagConstraints(1, 2,
                1, 1, 1, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                insets, 0, 0));

        // Boutons Retour + Modifier
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(backButton);
        buttonPanel.add(saveButton);

        this.add(buttonPanel, new GridBagConstraints(0, 3,
                2, 1, 0, 0,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                insets, 0, 0));

        // Séparateur + bouton de suppression
        JSeparator separator = new JSeparator();
        this.add(separator, new GridBagConstraints(0, 4,
                2, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(20, 5, 10, 5), 0, 0));

        JPanel deletePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        deletePanel.add(deleteButton);

        this.add(deletePanel, new GridBagConstraints(0, 5,
                2, 1, 0, 0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                insets, 0, 0));
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
