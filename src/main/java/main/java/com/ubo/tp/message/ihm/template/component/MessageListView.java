package main.java.com.ubo.tp.message.ihm.template.component;

import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Composant graphique de la liste des messages.
 * Aucune référence vers un contrôleur.
 */
public class MessageListView extends JPanel {
    private final JPanel listPanel;
    /** Fournit l'utilisateur connecté au moment du rendu. */
    private Supplier<User> connectedUserSupplier;
    /** Callback de suppression — transmis à chaque MessageComponent. */
    private Consumer<Message> onDelete;

    public MessageListView() {
        super(new BorderLayout());
        setOpaque(true);
        setBackground(Color.WHITE);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(true);
        listPanel.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(listPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setOpaque(true);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setOpaque(true);
        scroll.setBackground(Color.WHITE);

        add(scroll, BorderLayout.CENTER);
    }

    /** Définit le fournisseur d'utilisateur connecté et le callback de suppression. */
    public void setDeleteContext(Supplier<User> connectedUserSupplier, Consumer<Message> onDelete) {
        this.connectedUserSupplier = connectedUserSupplier;
        this.onDelete = onDelete;
    }

    /** Remplace toute la liste et trie par date croissante. */
    public void refreshMessage(Set<Message> messages) {
        listPanel.removeAll();
        User current = connectedUserSupplier != null ? connectedUserSupplier.get() : null;
        List<Message> sorted = new ArrayList<>(messages);
        sorted.sort(Comparator.comparingLong(Message::getEmissionDate));
        for (Message m : sorted) {
            listPanel.add(new MessageComponent(m, current, onDelete));
        }
        // Pousse les messages vers le haut
        listPanel.add(Box.createVerticalGlue());
        listPanel.revalidate();
        listPanel.repaint();
        scrollToBottom();
    }

    /** Ajoute un seul message en bas. */
    public void addSingleMessageView(Message message) {
        // Retirer le glue existant avant d'ajouter
        int count = listPanel.getComponentCount();
        if (count > 0 && listPanel.getComponent(count - 1) instanceof Box.Filler) {
            listPanel.remove(count - 1);
        }
        User current = connectedUserSupplier != null ? connectedUserSupplier.get() : null;
        listPanel.add(new MessageComponent(message, current, onDelete));
        listPanel.add(Box.createVerticalGlue());
        listPanel.revalidate();
        listPanel.repaint();
        scrollToBottom();
    }

    public void clear() {
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