package main.java.com.ubo.tp.message.ihm.template.component;

import main.java.com.ubo.tp.message.datamodel.User;

import javax.swing.*;
import java.awt.*;
import java.util.Set;
import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.List;

public class UserListView extends JPanel {

    private final JPanel listPanel;
    private final List<UserComponent> userComponents = new ArrayList<>();

    public UserListView() {
        super(new BorderLayout());
        setOpaque(true);
        setBackground(new Color(0x474747));

        JLabel title = new JLabel("Utilisateurs", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 13f));
        title.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        title.setBackground(new Color(0x474747));
        title.setForeground(Color.WHITE);
        title.setOpaque(true);
        add(title, BorderLayout.NORTH);

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
        listPanel.removeAll();
        userComponents.clear(); // FIX: vider la liste avant de la reconstruire
        for (User u : users) addUserRow(u);
        listPanel.add(Box.createVerticalGlue());
        listPanel.revalidate();
        listPanel.repaint();
    }

    private void addUserRow(User user) {
        UserComponent comp = new UserComponent(user);
        // FIX: contraindre la hauteur pour que le composant ne s'étire pas
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, comp.getPreferredSize().height));
        userComponents.add(comp);
        listPanel.add(comp);
    }

    /** Marque visuellement l'utilisateur sélectionné. */
    public void setSelectedUser(User user) {
        for (UserComponent comp : userComponents) {
            comp.setSelected(comp.getUser().equals(user));
        }
    }

    public void addUserSelectListener(Consumer<User> listener) {
        for (UserComponent userComponent : userComponents) {
            userComponent.addSelectListener(listener);
        }
    }

    public void clear() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::clear);
            return;
        }
        listPanel.removeAll();
        userComponents.clear();
        listPanel.revalidate();
        listPanel.repaint();
    }
}