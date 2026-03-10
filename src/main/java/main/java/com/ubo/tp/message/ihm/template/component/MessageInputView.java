package main.java.com.ubo.tp.message.ihm.template.component;

import main.java.com.ubo.tp.message.controller.MessageController;
import main.java.com.ubo.tp.message.controller.MessageInputController;
import main.java.com.ubo.tp.message.datamodel.User;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MessageInputView extends JPanel {
    private final MessageInputController messageInputController;
    private final JTextArea inputArea;
    private final JButton sendButton;

    /** Popup d'autocomplétion @ */
    private JPopupMenu mentionPopup;

    public MessageInputView(MessageInputController messageInputController) {
        super(new BorderLayout(6, 0));
        this.messageInputController = messageInputController;

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xDDDDDD)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        setBackground(Color.WHITE);

        inputArea = new JTextArea(3, 20);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setFont(inputArea.getFont().deriveFont(Font.PLAIN, 13f));
        inputArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCCCCCC)),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));

        // Limite de caractères
        ((AbstractDocument) inputArea.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string == null) return;
                if (fb.getDocument().getLength() + string.length() <= MessageController.MESSAGE_MAX_LENGTH)
                    super.insertString(fb, offset, string, attr);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text == null) text = "";
                int newLength = fb.getDocument().getLength() - length + text.length();
                if (newLength <= MessageController.MESSAGE_MAX_LENGTH) {
                    super.replace(fb, offset, length, text, attrs);
                } else {
                    int available = MessageController.MESSAGE_MAX_LENGTH - (fb.getDocument().getLength() - length);
                    if (available > 0) super.replace(fb, offset, length, text.substring(0, available), attrs);
                }
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

        // Afficher le popup sous le curseur dans le textArea
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
        inputArea.setCaretPosition(before.length() + tag.length() + 2); // après @tag + espace
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