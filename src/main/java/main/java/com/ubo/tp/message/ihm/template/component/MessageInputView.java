package main.java.com.ubo.tp.message.ihm.template.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Composant graphique de saisie d'un message.
 * Expose addSendListener() — le contrôleur s'y abonne.
 * La commande de l'ActionEvent contient le texte saisi.
 */
public class MessageInputView extends JPanel {


    private final JTextArea inputArea;
    private final JButton sendButton;

    public MessageInputView() {
        super(new BorderLayout(6, 0));
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
        if (!text.isEmpty()) {

            ActionEvent evt = new ActionEvent(this,
                    ActionEvent.ACTION_PERFORMED, text);

            for (ActionListener l : externalSendListeners) {
                l.actionPerformed(evt);
            }
            inputArea.setText("");
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