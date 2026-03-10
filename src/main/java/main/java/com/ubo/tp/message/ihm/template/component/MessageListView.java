package main.java.com.ubo.tp.message.ihm.template.component;

import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Composant graphique de la liste des messages.
 * Aucune référence vers un contrôleur, aucun Supplier.
 * Toutes les données sont passées en paramètre au moment du rendu.
 */
public class MessageListView extends JPanel {

    private final JPanel listPanel;
    private final JTextField searchField;

    /** Callback de suppression enregistré une seule fois par le controller. */
    private Consumer<Message> onDelete;

    /** Liste complète des messages (non filtrée) — pour pouvoir re-filtrer sans refresh. */
    private List<Message> allMessages = new ArrayList<>();
    private User currentUser;

    public MessageListView() {
        super(new BorderLayout());
        setOpaque(true);
        setBackground(Color.WHITE);

        // Barre de recherche
        JPanel searchPanel = new JPanel(new BorderLayout(4, 0));
        searchPanel.setOpaque(true);
        searchPanel.setBackground(new Color(0xF5F5F5));
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xDDDDDD)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));

        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(searchIcon.getFont().deriveFont(Font.PLAIN, 12f));
        searchPanel.add(searchIcon, BorderLayout.WEST);

        searchField = new JTextField();
        searchField.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        searchField.setBackground(new Color(0xF5F5F5));
        searchField.putClientProperty("JTextField.placeholderText", "Rechercher dans les messages...");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { applyFilter(); }
            @Override public void removeUpdate(DocumentEvent e) { applyFilter(); }
            @Override public void changedUpdate(DocumentEvent e) { applyFilter(); }
        });
        searchPanel.add(searchField, BorderLayout.CENTER);

        add(searchPanel, BorderLayout.NORTH);

        // Liste
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

    /** Enregistre le callback de suppression une seule fois. */
    public void setOnDelete(Consumer<Message> onDelete) {
        this.onDelete = onDelete;
    }

    /**
     * Remplace toute la liste, triée par date croissante.
     */
    public void refreshMessage(Set<Message> messages, User connectedUser) {
        this.currentUser = connectedUser;
        this.allMessages = new ArrayList<>(messages);
        this.allMessages.sort(Comparator.comparingLong(Message::getEmissionDate));
        // Réinitialise la recherche à chaque changement de conversation
        searchField.setText("");
        applyFilter();
    }

    /**
     * Ajoute un seul message en bas (si il passe le filtre actuel).
     */
    public void addSingleMessage(Message message, User connectedUser) {
        this.currentUser = connectedUser;
        // Ajouter à la liste complète
        allMessages.add(message);

        // N'ajouter visuellement que si le message passe le filtre courant
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty() || matchesSearch(message, query)) {
            int count = listPanel.getComponentCount();
            if (count > 0 && listPanel.getComponent(count - 1) instanceof Box.Filler) {
                listPanel.remove(count - 1);
            }
            listPanel.add(new MessageComponent(message, connectedUser, onDelete));
            listPanel.add(Box.createVerticalGlue());
            listPanel.revalidate();
            listPanel.repaint();
            scrollToBottom();
        }
    }

    private void applyFilter() {
        String query = searchField.getText().trim().toLowerCase();
        listPanel.removeAll();

        for (Message m : allMessages) {
            if (query.isEmpty() || matchesSearch(m, query)) {
                listPanel.add(new MessageComponent(m, currentUser, onDelete));
            }
        }

        listPanel.add(Box.createVerticalGlue());
        listPanel.revalidate();
        listPanel.repaint();
        scrollToBottom();
    }

    private boolean matchesSearch(Message message, String query) {
        // Recherche dans le texte du message et dans le tag/nom de l'expéditeur
        String text = message.getText() != null ? message.getText().toLowerCase() : "";
        String senderName = message.getSender() != null && message.getSender().getName() != null
                ? message.getSender().getName().toLowerCase() : "";
        String senderTag = message.getSender() != null && message.getSender().getUserTag() != null
                ? message.getSender().getUserTag().toLowerCase() : "";
        return text.contains(query) || senderName.contains(query) || senderTag.contains(query);
    }

    public void clear() {
        allMessages.clear();
        searchField.setText("");
        listPanel.removeAll();
        listPanel.revalidate();
        listPanel.repaint();
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = ((JScrollPane) getComponent(1)).getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
    }
}