package main.java.com.ubo.tp.message.ihm.template.component;

import main.java.com.ubo.tp.message.controller.MessageController;
import main.java.com.ubo.tp.message.controller.MessageInputController;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * Composant graphique de saisie d'un message.
 * Expose addSendListener() — le contrôleur s'y abonne.
 * La commande de l'ActionEvent contient le texte saisi.
 */
public class MessageInputView extends JPanel {
    private final MessageInputController messageInputController;

    private final JTextArea inputArea;
    private final JButton sendButton;

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

        ((AbstractDocument) inputArea.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string == null) return;
                int newLength = fb.getDocument().getLength() + string.length();
                if (newLength <= MessageController.MESSAGE_MAX_LENGTH) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text == null) text = "";
                int newLength = fb.getDocument().getLength() - length + text.length();
                if (newLength <= MessageController.MESSAGE_MAX_LENGTH) {
                    super.replace(fb, offset, length, text, attrs);
                } else {
                    // On insère autant de caractères que possible
                    int available = MessageController.MESSAGE_MAX_LENGTH - (fb.getDocument().getLength() - length);
                    if (available > 0) {
                        super.replace(fb, offset, length, text.substring(0, available), attrs);
                    }
                }
            }
        });

        // Entrée = envoi, Maj+Entrée = saut de ligne
        inputArea.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) {
                    e.consume();
                    triggerSend();
                }
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

        add(scroll,      BorderLayout.CENTER);
        add(sendButton,  BorderLayout.EAST);
    }

    private void triggerSend() {
        String text = inputArea.getText().trim();
        if (!text.isEmpty() && text.length() <= MessageController.MESSAGE_MAX_LENGTH) {
            this.messageInputController.sendMessage(text);
            inputArea.setText("");
        }else {
            JOptionPane.showMessageDialog(this,
                    "Le message doit contenir entre 1 et " + MessageController.MESSAGE_MAX_LENGTH + " caractères.",
                    "Message invalide",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private final java.util.List<ActionListener> externalSendListeners = new java.util.ArrayList<>();

    /**
     * Le contrôleur s'abonne ici.
     * La commande de l'ActionEvent contient le texte du message.
     */
    public void addSendListener(ActionListener listener) {
        externalSendListeners.add(listener);
    }

    /** Vide la zone de saisie. */
    public void clearInput() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::clearInput);
            return;
        }
        inputArea.setText("");
    }

    /** Active ou désactive la saisie (ex : aucun canal sélectionné). */
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        inputArea.setEnabled(enabled);
        sendButton.setEnabled(enabled);
    }
}