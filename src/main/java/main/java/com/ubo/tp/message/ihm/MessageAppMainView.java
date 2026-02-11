package main.java.com.ubo.tp.message.ihm;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.ihm.template.ConnectView;
import main.java.com.ubo.tp.message.controller.UserController;
import main.java.com.ubo.tp.message.ihm.template.LoginView;
import main.java.com.ubo.tp.message.ihm.template.RegisterView;
import main.java.com.ubo.tp.message.datamodel.User;


public class MessageAppMainView {

    private UserController userController;

    private final JFrame mainFrame;
    private JMenuBar menuBar;
    private JMenu connectionMenu;





    private JPanel contentPanel;


    // Callbacks (listeners) exposés pour être branchés par un controller
    private ActionListener chooseExchangeActionListener;
    private ActionListener logoutActionListener;

    // Callbacks pour informer le controller lorsque la vue crée les formulaires
    private Consumer<RegisterView> onRegisterViewCreated;
    private Consumer<LoginView> onLoginViewCreated;


    public MessageAppMainView() {
        mainFrame = new JFrame("Message App");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 600);
        this.setTitle("Message App");
        this.setlogo("resources/images/logo_50.png");

        this.setMenu();

    }


    public void setOnRegisterViewCreated(Consumer<RegisterView> consumer) {
        this.onRegisterViewCreated = consumer;
    }

    public void setOnLoginViewCreated(Consumer<LoginView> consumer) {
        this.onLoginViewCreated = consumer;
    }

    public void setOnChooseExchangeAction(ActionListener listener) {
        this.chooseExchangeActionListener = listener;
    }

    public void setOnLogoutAction(ActionListener listener) {
        this.logoutActionListener = listener;
    }


    public String getExchangeDirectory() {
        return getExchangeDirectoryChooser();
    }

    public void setUserController(UserController controller) {
        this.userController = controller;
    }

    public void showInfo(String message) {
        JOptionPane.showMessageDialog(mainFrame, message);
    }

    public void showError(String title, String message) {
        JOptionPane.showMessageDialog(mainFrame, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private void setMenu() {
        menuBar = new JMenuBar();
        JMenu file = new JMenu("Fichier");
        JMenu help = new JMenu("Aide");
        connectionMenu = new JMenu("connexion");
        contentPanel = new JPanel((new GridBagLayout()));


        JMenuItem chooseExchange = new JMenuItem("Choisir répertoire d'échange");
        chooseExchange.addActionListener(e -> {
            chooseExchangeActionListener.actionPerformed(e);

        });
        file.add(chooseExchange);


        JMenuItem registerItem = new JMenuItem("S'inscrire");
        registerItem.addActionListener(e -> {
            RegisterView rt = new RegisterView();
            onRegisterViewCreated.accept(rt);
            showContent(rt);
        });
        connectionMenu.add(registerItem);

        JMenuItem connectionItem = new JMenuItem("Se connecter");
        connectionItem.addActionListener(e -> {
            LoginView lg = new LoginView();
            onLoginViewCreated.accept(lg);
            showContent(lg);

        });
        connectionMenu.add(connectionItem);

        JMenuItem quit = new JMenuItem("Quitter", new ImageIcon(this.loadImg("/images/exitIcon_20.png")));
        quit.addActionListener(e -> System.exit(0));
        file.add(quit);


        JMenuItem about = new JMenuItem("À propos", new ImageIcon(this.loadImg("/images/logo_20.png")));
        about.addActionListener(e -> showAboutDialog());
        help.add(about);


        menuBar.add(file);
        menuBar.add(connectionMenu);
        menuBar.add(help);
        mainFrame.setJMenuBar(menuBar);
        mainFrame.getContentPane().add(contentPanel, BorderLayout.CENTER);
    }

    public void connect() {

        mainFrame.getContentPane().removeAll();
        mainFrame.getContentPane().setLayout(new BorderLayout());

        ConnectView con = new ConnectView();
        con.setPreferredSize(new Dimension(240, 0));

        mainFrame.getContentPane().add(con, BorderLayout.WEST);

        JPanel center = new JPanel(new BorderLayout());
        JLabel placeholder = new JLabel("Sélectionnez un utilisateur pour démarrer une conversation.", SwingConstants.CENTER);
        center.add(placeholder, BorderLayout.CENTER);
        mainFrame.getContentPane().add(center, BorderLayout.CENTER);


        if (menuBar != null && connectionMenu != null) {
            menuBar.remove(connectionMenu);
            JMenu accountMenu = new JMenu("Compte");
            JMenuItem logout = new JMenuItem("Déconnexion");
            logout.addActionListener(e -> {
                    logoutActionListener.actionPerformed(e);
            });
            accountMenu.add(logout);
            menuBar.add(accountMenu);
            mainFrame.setJMenuBar(menuBar);
        }

        mainFrame.revalidate();
        mainFrame.repaint();

    }


    public void disconnect() {

        mainFrame.getContentPane().removeAll();
        mainFrame.setJMenuBar(null);
        setMenu();
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    private String getExchangeDirectoryChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(mainFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = fileChooser.getSelectedFile();
            String exchangeDirectoryPath = selectedDirectory.getAbsolutePath();
            return exchangeDirectoryPath;
        }
        return null ;
    }



    public void showContent(JComponent comp) {
        if (contentPanel == null) {
            contentPanel = new JPanel(new GridBagLayout());
            mainFrame.getContentPane().add(contentPanel, BorderLayout.CENTER);
        }
        contentPanel.removeAll();
        contentPanel.add(comp);
        mainFrame.revalidate();
        mainFrame.repaint();
    }


    private Image loadImg(String path) {
        return new ImageIcon(getClass().getResource(path)).getImage();
    }

    private void showAboutDialog() {
        JDialog aboutDialog = new JDialog(mainFrame, "À propos de Message App", true);
        aboutDialog.setSize(400, 250);
        aboutDialog.setLocationRelativeTo(mainFrame);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));


        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/images/logo_50.png"));
        JLabel logoLabel = new JLabel(logoIcon);
        panel.add(logoLabel, BorderLayout.WEST);


        JLabel aboutLabel = new JLabel("<html><center>Message App v1.0<br>Développé par MOI</center></html>", SwingConstants.CENTER);
        aboutLabel.setFont(aboutLabel.getFont().deriveFont(16.0f));
        panel.add(aboutLabel, BorderLayout.CENTER);

        aboutDialog.add(panel);
        aboutDialog.setVisible(true);

    }

    private void setTitle(String messageApp) {
        JLabel titleLabel = new JLabel(messageApp, SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(24.0f));
        mainFrame.getContentPane().add(titleLabel);
    }

    private void setlogo(String image) {
        ImageIcon icon = new ImageIcon(image);
        JLabel label = new JLabel(icon);
        mainFrame.getContentPane().add(label);
    }

    public void show() {
        mainFrame.setVisible(true);
    }
}
