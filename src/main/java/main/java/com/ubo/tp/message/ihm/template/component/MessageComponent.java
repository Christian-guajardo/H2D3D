package main.java.com.ubo.tp.message.ihm.template.component;

import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;

/**
 * Composant graphique d'un seul message.
 * Aucune référence vers un contrôleur.
 */
public class MessageComponent extends JPanel {

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("dd/MM/yyyy HH:mm");

    /**
     * @param message        le message à afficher
     * @param connectedUser  l'utilisateur connecté (null = pas de bouton supprimer)
     * @param onDelete       callback appelé quand l'utilisateur clique sur supprimer (null = désactivé)
     */
    public MessageComponent(Message message, User connectedUser, Consumer<Message> onDelete) {
        super(new BorderLayout(0, 2));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xE0E0E0)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        setBackground(Color.WHITE);
        setOpaque(true);

        // En-tête : GridBagLayout — sender extensible | date fixe | bouton fixe
        JPanel header = new JPanel(new GridBagLayout());
        header.setOpaque(false);

        JLabel senderLabel = new JLabel(message.getSender().getName() + " - @" + message.getSender().getUserTag());
        senderLabel.setFont(senderLabel.getFont().deriveFont(Font.BOLD, 12f));
        senderLabel.setForeground(new Color(0x2F80ED));
        senderLabel.setMinimumSize(new Dimension(0, senderLabel.getPreferredSize().height));

        JLabel dateLabel = new JLabel(DATE_FORMAT.format(new Date(message.getEmissionDate())));
        dateLabel.setFont(dateLabel.getFont().deriveFont(Font.PLAIN, 10f));
        dateLabel.setForeground(Color.GRAY);

        // col 0 : sender (prend tout l'espace)
        header.add(senderLabel, new GridBagConstraints(0, 0,
                1, 1, 1.0, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 6), 0, 0));

        // col 1 : date (taille fixe)
        header.add(dateLabel, new GridBagConstraints(1, 0,
                1, 1, 0, 0,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));

        boolean isOwner = connectedUser != null
                && message.getSender() != null
                && message.getSender().equals(connectedUser);

        if (isOwner && onDelete != null) {
            // col 2 : bouton supprimer (taille fixe)
            JButton deleteBtn = new JButton("🗑");
            deleteBtn.setFont(deleteBtn.getFont().deriveFont(Font.PLAIN, 11f));
            deleteBtn.setForeground(new Color(0xE74C3C));
            deleteBtn.setBackground(Color.WHITE);
            deleteBtn.setBorderPainted(false);
            deleteBtn.setFocusPainted(false);
            deleteBtn.setContentAreaFilled(false);
            deleteBtn.setMargin(new Insets(0, 4, 0, 0));
            deleteBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            deleteBtn.setToolTipText("Supprimer ce message");
            deleteBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(
                        SwingUtilities.getWindowAncestor(this),
                        "Supprimer ce message ?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    onDelete.accept(message);
                }
            });
            header.add(deleteBtn, new GridBagConstraints(2, 0,
                    1, 1, 0, 0,
                    GridBagConstraints.EAST, GridBagConstraints.NONE,
                    new Insets(0, 4, 0, 0), 0, 0));
        }

        // Corps
        JLabel textLabel = new JLabel("<html><body style='width:100%'>"
                + escapeHtml(message.getText()) + "</body></html>");
        textLabel.setFont(textLabel.getFont().deriveFont(Font.PLAIN, 13f));
        textLabel.setForeground(Color.DARK_GRAY);
        textLabel.setOpaque(true);
        textLabel.setBackground(Color.WHITE);

        add(header,    BorderLayout.NORTH);
        add(textLabel, BorderLayout.CENTER);
    }

    /** Constructeur de compatibilité sans suppression. */
    public MessageComponent(Message message) {
        this(message, null, null);
    }

    @Override
    public Dimension getMaximumSize() {
        // Empêche le BoxLayout de stretch en hauteur
        return new Dimension(super.getMaximumSize().width, getPreferredSize().height);
    }

    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\n", "<br>");
    }
}