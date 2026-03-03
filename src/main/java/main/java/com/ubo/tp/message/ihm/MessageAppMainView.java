package main.java.com.ubo.tp.message.ihm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

import main.java.com.ubo.tp.message.ihm.template.ConnectView;

public class MessageAppMainView {
    private final JFrame mainFrame;
    private JMenuBar menuBar;
    private JMenu connectionMenu;
    private JMenu accountMenu;

    private JMenuItem registerMenuItem;
    private JMenuItem loginMenuItem;
    private JMenuItem logoutMenuItem;
    private JMenuItem chooseExchangeMenuItem;

    private JPanel contentPanel;

    public MessageAppMainView() {
        mainFrame = new JFrame("Message App");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 600);

        createMenu();
    }

    private void createMenu() {
        menuBar = new JMenuBar();

        // Menu Fichier
        JMenu fileMenu = new JMenu("Fichier");
        chooseExchangeMenuItem = new JMenuItem("Choisir répertoire d'échange");
        fileMenu.add(chooseExchangeMenuItem);

        JMenuItem quit = new JMenuItem("Quitter");
        quit.addActionListener(e -> System.exit(0));
        fileMenu.add(quit);

        // Menu Connexion (visible quand déconnecté)
        connectionMenu = new JMenu("Connexion");
        registerMenuItem = new JMenuItem("S'inscrire");
        connectionMenu.add(registerMenuItem);

        loginMenuItem = new JMenuItem("Se connecter");
        connectionMenu.add(loginMenuItem);

        // Menu Compte (visible quand connecté)
        accountMenu = new JMenu("Compte");
        logoutMenuItem = new JMenuItem("Déconnexion");
        accountMenu.add(logoutMenuItem);

        // Menu Aide
        JMenu helpMenu = new JMenu("Aide");
        JMenuItem about = new JMenuItem("À propos");
        about.addActionListener(e -> showAboutDialog());
        helpMenu.add(about);

        // État initial = déconnecté
        menuBar.add(fileMenu);
        menuBar.add(connectionMenu);
        menuBar.add(helpMenu);

        mainFrame.setJMenuBar(menuBar);

        contentPanel = new JPanel(new GridBagLayout());
        mainFrame.getContentPane().add(contentPanel, BorderLayout.CENTER);
    }

    // GETTERS pour que le controller accède aux menu items
    public JMenuItem getRegisterMenuItem() {
        return registerMenuItem;
    }

    public JMenuItem getLoginMenuItem() {
        return loginMenuItem;
    }

    public JMenuItem getLogoutMenuItem() {
        return logoutMenuItem;
    }

    public JMenuItem getChooseExchangeMenuItem() {
        return chooseExchangeMenuItem;
    }

    // Affichage de contenu
    public void showContent(JComponent comp) {
        contentPanel.removeAll();
        contentPanel.add(comp);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    // État connecté
    public void showConnectedState(JPanel connectView) {
        menuBar.remove(connectionMenu);
        if (accountMenu.getParent() == null) {
            menuBar.add(accountMenu, 1);
        }
        menuBar.revalidate();
        menuBar.repaint();

        mainFrame.getContentPane().removeAll();
        mainFrame.getContentPane().setLayout(new BorderLayout());
        mainFrame.getContentPane().add(connectView, BorderLayout.CENTER);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    // État déconnecté
    public void showDisconnectedState() {
        menuBar.remove(accountMenu);
        if (connectionMenu.getParent() == null) {
            menuBar.add(connectionMenu, 1); // Après "Fichier"
        }
        menuBar.revalidate();
        menuBar.repaint();

        // Réafficher le panel de contenu
        mainFrame.getContentPane().removeAll();
        contentPanel = new JPanel(new GridBagLayout());
        mainFrame.getContentPane().add(contentPanel, BorderLayout.CENTER);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    // Sélection de répertoire
    public String selectExchangeDirectory() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(mainFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    // Messages
    public void showInfo(String message) {
        JOptionPane.showMessageDialog(mainFrame, message);
    }

    public void showError(String title, String message) {
        JOptionPane.showMessageDialog(mainFrame, message, title, JOptionPane.ERROR_MESSAGE);
    }

    // Dialog À propos
    private void showAboutDialog() {
        JDialog aboutDialog = new JDialog(mainFrame, "À propos de Message App", true);
        aboutDialog.setSize(400, 250);
        aboutDialog.setLocationRelativeTo(mainFrame);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel aboutLabel = new JLabel(
                "<html><center>Message App v1.0<br>Développé par MOI</center></html>",
                SwingConstants.CENTER
        );
        aboutLabel.setFont(aboutLabel.getFont().deriveFont(16.0f));
        panel.add(aboutLabel, BorderLayout.CENTER);

        aboutDialog.add(panel);
        aboutDialog.setVisible(true);
    }

    public void show() {
        mainFrame.setVisible(true);
    }

    public void addRegisterAction(ActionListener listener) {
        registerMenuItem.addActionListener(listener);
    }

    public void addLoginMenuItem(ActionListener listener) {
        loginMenuItem.addActionListener(listener);
    }

    public void addChooseExchangeMenuItem(ActionListener listener) {
        chooseExchangeMenuItem.addActionListener(listener);
    }
}