package main.java.com.ubo.tp.message.ihm.template.component;

import main.java.com.ubo.tp.message.datamodel.Message;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Composant graphique d'un seul message.
 * Aucune référence vers un contrôleur.
 */
public class MessageComponent extends JPanel {

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public MessageComponent(Message message) {
        super(new BorderLayout(6, 2));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xE0E0E0)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        setBackground(Color.WHITE);
        setOpaque(true);

        // En-tête : @tag  +  date
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(true);
        header.setBackground(Color.WHITE);

        JLabel senderLabel = new JLabel(message.getSender().getName());
        senderLabel.setFont(senderLabel.getFont().deriveFont(Font.BOLD, 12f));
        senderLabel.setForeground(new Color(0x2F80ED));

        JLabel dateLabel = new JLabel(DATE_FORMAT.format(new Date(message.getEmissionDate())));
        dateLabel.setFont(dateLabel.getFont().deriveFont(Font.PLAIN, 10f));
        dateLabel.setForeground(Color.GRAY);

        header.add(senderLabel, BorderLayout.WEST);
        header.add(dateLabel,   BorderLayout.EAST);

        // Corps — JLabel HTML pour auto-sizing au lieu de JTextArea
        JLabel textLabel = new JLabel("<html><body style='width:100%'>"
                + escapeHtml(message.getText()) + "</body></html>");
        textLabel.setFont(textLabel.getFont().deriveFont(Font.PLAIN, 13f));
        textLabel.setForeground(Color.DARK_GRAY);
        textLabel.setOpaque(true);
        textLabel.setBackground(Color.WHITE);

        add(header,    BorderLayout.NORTH);
        add(textLabel, BorderLayout.CENTER);
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