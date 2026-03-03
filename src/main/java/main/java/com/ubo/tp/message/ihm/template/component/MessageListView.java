package main.java.com.ubo.tp.message.ihm.template.component;

import main.java.com.ubo.tp.message.datamodel.Message;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Composant graphique de la liste des messages.
 * Aucune référence vers un contrôleur.
 */
public class MessageListView extends JPanel {

    private final JPanel listPanel;

    public MessageListView() {
        super(new BorderLayout());

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.GRAY);

        JScrollPane scroll = new JScrollPane(listPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(scroll, BorderLayout.CENTER);
    }

    /** Remplace toute la liste et trie par date croissante. */
    public void setMessages(List<Message> messages) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> setMessages(messages));
            return;
        }
        listPanel.removeAll();
        List<Message> sorted = new ArrayList<>(messages);
        sorted.sort(Comparator.comparingLong(Message::getEmissionDate));
        for (Message m : sorted) {
            listPanel.add(new MessageComponent(m));
        }
        listPanel.revalidate();
        listPanel.repaint();
        scrollToBottom();
    }

    /** Ajoute un seul message en bas. */
    public void addMessage(Message message) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> addMessage(message));
            return;
        }
        listPanel.add(new MessageComponent(message));
        listPanel.revalidate();
        listPanel.repaint();
        scrollToBottom();
    }

    public void clear() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::clear);
            return;
        }
        listPanel.removeAll();
        listPanel.revalidate();
        listPanel.repaint();
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = ((JScrollPane) getComponent(0)).getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
    }
}