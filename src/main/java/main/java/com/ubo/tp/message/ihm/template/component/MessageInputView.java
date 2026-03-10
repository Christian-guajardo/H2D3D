package main.java.com.ubo.tp.message.ihm.template.component;

import main.java.com.ubo.tp.message.controller.MessageController;
import main.java.com.ubo.tp.message.controller.MessageInputController;
import main.java.com.ubo.tp.message.datamodel.User;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MessageInputView extends JPanel {
    private final MessageInputController messageInputController;
    private final JTextPane inputArea;
    private final JButton sendButton;

    private static final Color MENTION_COLOR = new Color(0x2F80ED);

    /** Popup d'autocomplétion @ */
    private JPopupMenu mentionPopup;

    /** Flag pour éviter une boucle infinie lors de la recolorisation. */
    private boolean updatingStyles = false;

    public MessageInputView(MessageInputController messageInputController) {
        super(new BorderLayout(6, 0));
        this.messageInputController = messageInputController;

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xDDDDDD)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        setBackground(Color.WHITE);

        inputArea = new JTextPane();
        inputArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        inputArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCCCCCC)),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        inputArea.setPreferredSize(new Dimension(20, 60));

        // Limite de caractères
        ((AbstractDocument) inputArea.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string == null) return;
                if (fb.getDocument().getLength() + string.length() <= MessageController.MESSAGE_MAX_LENGTH) {
                    super.insertString(fb, offset, string, attr);
                    if (!updatingStyles) highlightMentions();
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text == null) text = "";
                int newLength = fb.getDocument().getLength() - length + text.length();
                if (newLength <= MessageController.MESSAGE_MAX_LENGTH) {
                    super.replace(fb, offset, length, text, attrs);
                    if (!updatingStyles) highlightMentions();
                } else {
                    int available = MessageController.MESSAGE_MAX_LENGTH - (fb.getDocument().getLength() - length);
                    if (available > 0) {
                        super.replace(fb, offset, length, text.substring(0, available), attrs);
                        if (!updatingStyles) highlightMentions();
                    }
                }
            }

            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                super.remove(fb, offset, length);
                if (!updatingStyles) highlightMentions();
            }
        });

        inputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) {
                    e.consume();
                    // Fermer le popup si ouvert
                    if (mentionPopup != null && mentionPopup.isVisible()) {
                        mentionPopup.setVisible(false);
                        return;
                    }
                    triggerSend();
                }
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    if (mentionPopup != null) mentionPopup.setVisible(false);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                handleMentionPopup();
            }
        });

        sendButton = new JButton("Envoyer");
        sendButton.setBackground(new Color(0x2F80ED));
        sendButton.setForeground(Color.WHITE);
        sendButton.setBorderPainted(false);
        sendButton.setFont(sendButton.getFont().deriveFont(Font.BOLD, 12f));
        sendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sendButton.addActionListener(e -> triggerSend());

        JScrollPane scroll = new JScrollPane(inputArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);

        add(scroll, BorderLayout.CENTER);
        add(sendButton, BorderLayout.EAST);
    }

    /**
     * Colorise en bleu toutes les occurrences de @tag dans le texte
     * (tags des utilisateurs disponibles pour la mention).
     */
    private void highlightMentions() {
        StyledDocument doc = inputArea.getStyledDocument();
        String text = inputArea.getText();

        // Style par défaut (noir)
        Style defaultStyle = inputArea.addStyle("default", null);
        StyleConstants.setForeground(defaultStyle, Color.BLACK);
        StyleConstants.setFontSize(defaultStyle, 13);

        // Style mention (bleu + gras)
        Style mentionStyle = inputArea.addStyle("mention", null);
        StyleConstants.setForeground(mentionStyle, MENTION_COLOR);
        StyleConstants.setBold(mentionStyle, true);
        StyleConstants.setFontSize(mentionStyle, 13);

        updatingStyles = true;
        try {
            // Remettre tout en noir
            doc.setCharacterAttributes(0, text.length(), defaultStyle, true);

            // Récupérer les tags connus
            Set<User> available = messageInputController.getAvailableUsersForMention();
            if (available == null || available.isEmpty()) return;

            // Construire un pattern @tag1|@tag2|...
            String tagPattern = available.stream()
                    .map(u -> Pattern.quote("@" + u.getUserTag()))
                    .collect(Collectors.joining("|"));
            Pattern pattern = Pattern.compile("(" + tagPattern + ")(?=\\s|$)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);

            while (matcher.find()) {
                doc.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(), mentionStyle, true);
            }
        } finally {
            updatingStyles = false;
        }
    }

    /**
     * Détecte si le curseur est après un @ et affiche le popup d'autocomplétion.
     */
    private void handleMentionPopup() {
        String text = inputArea.getText();
        int caretPos = inputArea.getCaretPosition();
        if (caretPos == 0) {
            hideMentionPopup();
            return;
        }

        // Chercher le dernier @ avant le curseur
        int atIndex = text.lastIndexOf('@', caretPos - 1);
        if (atIndex == -1) {
            hideMentionPopup();
            return;
        }

        // Vérifier qu'il n'y a pas d'espace entre @ et le curseur
        String partial = text.substring(atIndex + 1, caretPos);
        if (partial.contains(" ")) {
            hideMentionPopup();
            return;
        }

        // Récupérer les users correspondant au préfixe tapé
        Set<User> available = messageInputController.getAvailableUsersForMention();
        List<User> matches = available.stream()
                .filter(u -> u.getUserTag().toLowerCase().startsWith(partial.toLowerCase()))
                .sorted((a, b) -> a.getUserTag().compareToIgnoreCase(b.getUserTag()))
                .collect(Collectors.toList());

        if (matches.isEmpty()) {
            hideMentionPopup();
            return;
        }

        showMentionPopup(matches, atIndex, partial);
    }

    private void showMentionPopup(List<User> users, int atIndex, String partial) {
        if (mentionPopup != null) mentionPopup.setVisible(false);
        mentionPopup = new JPopupMenu();
        mentionPopup.setBorder(BorderFactory.createLineBorder(new Color(0xCCCCCC)));

        for (User u : users) {
            JMenuItem item = new JMenuItem("@" + u.getUserTag() + "  (" + u.getName() + ")");
            item.setFont(item.getFont().deriveFont(Font.PLAIN, 12f));
            item.addActionListener(e -> insertMention(u.getUserTag(), atIndex, partial));
            mentionPopup.add(item);
        }

        // Afficher le popup sous le curseur dans le textPane
        try {
            Rectangle rect = inputArea.modelToView(inputArea.getCaretPosition());
            mentionPopup.show(inputArea, rect.x, rect.y + rect.height);
        } catch (BadLocationException ex) {
            mentionPopup.show(inputArea, 0, inputArea.getHeight());
        }
        // Redonner le focus à la zone de saisie
        inputArea.requestFocusInWindow();
    }

    private void hideMentionPopup() {
        if (mentionPopup != null) mentionPopup.setVisible(false);
    }

    /**
     * Remplace le @partiel par @tag complet dans le texte.
     */
    private void insertMention(String tag, int atIndex, String partial) {
        String text = inputArea.getText();
        String before = text.substring(0, atIndex);
        String after = text.substring(atIndex + 1 + partial.length());
        String newText = before + "@" + tag + " " + after;
        inputArea.setText(newText);
        inputArea.setCaretPosition(Math.min(before.length() + tag.length() + 2, newText.length()));
        hideMentionPopup();
    }

    private void triggerSend() {
        String text = inputArea.getText().trim();
        if (!text.isEmpty() && text.length() <= MessageController.MESSAGE_MAX_LENGTH) {
            this.messageInputController.sendMessage(text);
            inputArea.setText("");
        } else {
            JOptionPane.showMessageDialog(this,
                    "Le message doit contenir entre 1 et " + MessageController.MESSAGE_MAX_LENGTH + " caractères.",
                    "Message invalide",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        inputArea.setEnabled(enabled);
        sendButton.setEnabled(enabled);
    }
}