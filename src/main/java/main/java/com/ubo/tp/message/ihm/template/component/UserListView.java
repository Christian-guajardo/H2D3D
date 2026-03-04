package main.java.com.ubo.tp.message.ihm.template.component;

import main.java.com.ubo.tp.message.datamodel.User;
import javax.swing.*;
import java.awt.*;
import java.util.Set;
import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.List;

/**
 * Composant graphique de la liste des utilisateurs.
 * Expose addUserSelectListener() — le contrôleur s'y abonne sans couplage retour.
 */
public class UserListView extends JPanel {

    private final JPanel listPanel;
    private final List<UserComponent> userComponents = new ArrayList<>();

    public UserListView() {
        super(new BorderLayout());


        JLabel title = new JLabel("Utilisateurs", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 13f));
        title.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        title.setBackground(new Color(0x474747));
        title.setForeground(Color.WHITE);
        title.setOpaque(true);
        add(title, BorderLayout.NORTH);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(0x474747));

        JScrollPane scroll = new JScrollPane(listPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
    }

    public void refreshUsers(Set<User> users) {
        listPanel.removeAll();
        for (User u : users) addUserRow(u);
        listPanel.revalidate();
        listPanel.repaint();
    }

    private void addUserRow(User user) {
        UserComponent comp = new UserComponent(user);
        userComponents.add(comp);
        listPanel.add(comp);
    }

    /**
     * Le contrôleur enregistre ici son handler de sélection d'utilisateur.
     * La commande de l'événement contient l'UUID de l'utilisateur cliqué.
     */
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
        listPanel.revalidate();
        listPanel.repaint();
    }
}