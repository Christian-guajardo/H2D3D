package main.java.com.ubo.tp.message.ihm.template.component;

import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class ChannelListView extends JPanel {

    // --- Composants graphiques ---
    private final JPanel listPanel;
    private final JButton createButton;
    private final List<ChannelComponent> channelComponents = new ArrayList<>();

    // --- Callbacks vers le controller  ---
    private Consumer<Channel> onChannelSelected;
    private TriConsumer<String, Boolean, List<User>> onCreateChannel;
    private BiConsumer<Channel, List<User>> onManageMembers;
    private Consumer<Channel> onLeaveChannel;
    private Consumer<Channel> onDeleteChannel;

    // --- Fournisseurs de données  ---
    private Supplier<Set<User>> usersProvider;
    private Supplier<User> connectedUserProvider;

    @FunctionalInterface
    public interface TriConsumer<A, B, C> {
        void accept(A a, B b, C c);
    }

    public ChannelListView() {
        super(new BorderLayout());
        setOpaque(true);
        setBackground(new Color(0x2C2C2C));

        JPanel header = new JPanel(new BorderLayout(5, 0));
        header.setOpaque(true);
        header.setBackground(new Color(0x2C2C2C));
        header.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JLabel title = new JLabel("Canaux");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 13f));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        createButton = new JButton("+");
        createButton.setFont(createButton.getFont().deriveFont(Font.BOLD, 14f));
        createButton.setForeground(Color.WHITE);
        createButton.setBackground(new Color(0x2F80ED));
        createButton.setBorderPainted(false);
        createButton.setPreferredSize(new Dimension(28, 24));
        createButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        createButton.setToolTipText("Créer un canal");
        // La vue gère l'ouverture du dialog directement
        createButton.addActionListener(e -> handleCreateChannel());
        header.add(createButton, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(true);
        listPanel.setBackground(new Color(0x2C2C2C));

        JScrollPane scroll = new JScrollPane(listPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.setOpaque(true);
        scroll.setBackground(new Color(0x2C2C2C));
        scroll.getViewport().setOpaque(true);
        scroll.getViewport().setBackground(new Color(0x2C2C2C));
        add(scroll, BorderLayout.CENTER);
    }



    private void handleCreateChannel() {
        Frame parent = getParentFrame();
        Set<User> users = usersProvider != null ? usersProvider.get() : Set.of();

        CreateChannelDialog.CreateChannelResult result = CreateChannelDialog.show(parent, users);
        if (result != null && onCreateChannel != null) {
            onCreateChannel.accept(result.name, result.isPrivate, result.members);
        }
    }

    private void handleChannelOptions(Channel channel) {
        User me = connectedUserProvider != null ? connectedUserProvider.get() : null;
        boolean isCreator = me != null && channel.getCreator().equals(me);
        Frame parent = getParentFrame();

        if (isCreator) {
            String[] options = {"Gérer les membres", "Supprimer le canal", "Annuler"};
            int choice = JOptionPane.showOptionDialog(
                    parent,
                    "Que voulez-vous faire avec #" + channel.getName() + " ?",
                    "Options du canal",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null, options, options[0]);

            if (choice == 0) {
                handleManageMembers(channel, parent);
            } else if (choice == 1) {
                handleDeleteChannel(channel, parent);
            }
        } else {
            // Membre simple : quitter uniquement
            int confirm = JOptionPane.showConfirmDialog(
                    parent,
                    "Quitter le canal #" + channel.getName() + " ?",
                    "Quitter le canal",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION && onLeaveChannel != null) {
                onLeaveChannel.accept(channel);
            }
        }
    }

    private void handleManageMembers(Channel channel, Frame parent) {
        Set<User> allUsers = usersProvider != null ? usersProvider.get() : Set.of();
        List<User> newMembers = ManageChannelMembersDialog.show(parent, channel, allUsers);
        if (newMembers != null && onManageMembers != null) {
            onManageMembers.accept(channel, newMembers);
        }
    }

    private void handleDeleteChannel(Channel channel, Frame parent) {
        int confirm = JOptionPane.showConfirmDialog(
                parent,
                "Supprimer définitivement #" + channel.getName() + " ?",
                "Supprimer le canal",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION && onDeleteChannel != null) {
            onDeleteChannel.accept(channel);
        }
    }

    private Frame getParentFrame() {
        Window w = SwingUtilities.getWindowAncestor(this);
        return w instanceof Frame ? (Frame) w : null;
    }


    public void refreshChannel(Set<Channel> channels) {
        listPanel.removeAll();
        channelComponents.clear();
        for (Channel c : channels) addChannelRow(c);
        listPanel.add(Box.createVerticalGlue());
        listPanel.revalidate();
        listPanel.repaint();
    }

    private void addChannelRow(Channel channel) {
        ChannelComponent comp = new ChannelComponent(channel);
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, comp.getPreferredSize().height));

        if (onChannelSelected != null) comp.addSelectListener(onChannelSelected);
        // Le bouton ⚙ déclenche handleChannelOptions (dialogs gérés ici)
        comp.addManageMembersListener(this::handleChannelOptions);

        channelComponents.add(comp);
        listPanel.add(comp);
    }

    public void setSelectedChannel(Channel channel) {
        for (ChannelComponent comp : channelComponents) {
            comp.setSelected(comp.getChannel().equals(channel));
        }
    }

    public void clear() {
        listPanel.removeAll();
        channelComponents.clear();
        listPanel.revalidate();
        listPanel.repaint();
    }

    // -------------------------------------------------------------------------
    // Setters des callbacks (appelés par le controller)
    // -------------------------------------------------------------------------

    public void setOnChannelSelected(Consumer<Channel> listener) {
        this.onChannelSelected = listener;
    }

    public void setOnCreateChannel(TriConsumer<String, Boolean, List<User>> listener) {
        this.onCreateChannel = listener;
    }

    public void setOnManageMembers(BiConsumer<Channel, List<User>> listener) {
        this.onManageMembers = listener;
    }

    public void setOnLeaveChannel(Consumer<Channel> listener) {
        this.onLeaveChannel = listener;
    }

    public void setOnDeleteChannel(Consumer<Channel> listener) {
        this.onDeleteChannel = listener;
    }

    public void setUsersProvider(Supplier<Set<User>> provider) {
        this.usersProvider = provider;
    }

    public void setConnectedUserProvider(Supplier<User> provider) {
        this.connectedUserProvider = provider;
    }

    /** @deprecated Utiliser setOnChannelSelected() */
    @Deprecated
    public void addCreateChannelListener(ActionListener listener) {
        // no-op : géré en interne désormais
    }

    /** @deprecated Utiliser setOnChannelSelected() */
    @Deprecated
    public void addChannelSelectionListener(Consumer<Channel> listener) {
        this.onChannelSelected = listener;
    }
}