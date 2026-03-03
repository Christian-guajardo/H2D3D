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
        super(new BorderLayout(6, 4));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xE0E0E0)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        setBackground(Color.WHITE);

        // En-tête : @tag  +  date
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel senderLabel = new JLabel("@" + message.getSender().getUserTag());
        senderLabel.setFont(senderLabel.getFont().deriveFont(Font.BOLD, 12f));
        senderLabel.setForeground(new Color(0x2F80ED));

        JLabel dateLabel = new JLabel(DATE_FORMAT.format(new Date(message.getEmissionDate())));
        dateLabel.setFont(dateLabel.getFont().deriveFont(Font.PLAIN, 10f));
        dateLabel.setForeground(Color.GRAY);

        header.add(senderLabel, BorderLayout.WEST);
        header.add(dateLabel,   BorderLayout.EAST);

        // Corps
        JTextArea textArea = new JTextArea(message.getText());
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setOpaque(false);
        textArea.setFont(textArea.getFont().deriveFont(Font.PLAIN, 13f));
        textArea.setBorder(null);

        add(header,   BorderLayout.NORTH);
        add(textArea, BorderLayout.CENTER);
    }
}