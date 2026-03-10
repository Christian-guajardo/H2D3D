package main.java.com.ubo.tp.message.ihm.template.component;

import main.java.com.ubo.tp.message.datamodel.User;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.List;

public class UserListView extends JPanel {

    private final JPanel listPanel;
    private final JTextField searchField;
    private final List<UserComponent> userComponents = new ArrayList<>();
    // user list non filtree
    private Set<User> allUsers = new HashSet<>();
    private final List<Consumer<User>> selectListeners = new ArrayList<>();

    public UserListView() {
        super(new BorderLayout());
        setOpaque(true);
        setBackground(new Color(0x474747));

        // entête -> titre
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(true);
        headerPanel.setBackground(new Color(0x474747));

        JLabel title = new JLabel("Utilisateurs", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 13f));
        title.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));
        title.setBackground(new Color(0x474747));
        title.setForeground(Color.WHITE);
        title.setOpaque(true);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(title);

        // champ de recherche
        searchField = new JTextField();
        searchField.setToolTipText("Rechercher un utilisateur...");
        searchField.putClientProperty("JTextField.placeholderText", "Rechercher...");
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 8, 6, 8),
                searchField.getBorder()));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { applyFilter(); }
            @Override
            public void removeUpdate(DocumentEvent e) { applyFilter(); }
            @Override
            public void changedUpdate(DocumentEvent e) { applyFilter(); }
        });
        headerPanel.add(searchField);

        add(headerPanel, BorderLayout.NORTH);

        // --- Liste ---
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(true);
        listPanel.setBackground(new Color(0x474747));

        JScrollPane scroll = new JScrollPane(listPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.setOpaque(true);
        scroll.setBackground(new Color(0x474747));
        scroll.getViewport().setOpaque(true);
        scroll.getViewport().setBackground(new Color(0x474747));
        add(scroll, BorderLayout.CENTER);
    }

    public void refreshUsers(Set<User> users) {
        this.allUsers = new HashSet<>(users);
        applyFilter();
    }

    // filtre la recherche d'utilisateur
    private void applyFilter() {
        String query = searchField.getText().trim().toLowerCase();

        listPanel.removeAll();
        userComponents.clear();

        for (User u : allUsers) {
            if (query.isEmpty() || matchesSearch(u, query)) {
                addUserRow(u);
            }
        }

        listPanel.add(Box.createVerticalGlue());
        listPanel.revalidate();
        listPanel.repaint();

        // Ré-attacher les listeners de sélection après le refresh
        reattachListeners();
    }

    // true si la recherche correspond au tag ou user name
    private boolean matchesSearch(User user, String query) {
        String name = user.getName() != null ? user.getName().toLowerCase() : "";
        String tag = user.getUserTag() != null ? user.getUserTag().toLowerCase() : "";
        return name.contains(query) || tag.contains(query);
    }

    private void addUserRow(User user) {
        UserComponent comp = new UserComponent(user);
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, comp.getPreferredSize().height));
        userComponents.add(comp);
        listPanel.add(comp);
    }

    public void setSelectedUser(User user) {
        for (UserComponent comp : userComponents) {
            comp.setSelected(comp.getUser().equals(user));
        }
    }

    public void addUserSelectListener(Consumer<User> listener) {
        selectListeners.add(listener);
        for (UserComponent userComponent : userComponents) {
            userComponent.addSelectListener(listener);
        }
    }

    private void reattachListeners() {
        for (UserComponent comp : userComponents) {
            for (Consumer<User> listener : selectListeners) {
                comp.addSelectListener(listener);
            }
        }
    }

    public void clear() {
        allUsers.clear();
        searchField.setText("");
        listPanel.removeAll();
        userComponents.clear();
        listPanel.revalidate();
        listPanel.repaint();
    }
}