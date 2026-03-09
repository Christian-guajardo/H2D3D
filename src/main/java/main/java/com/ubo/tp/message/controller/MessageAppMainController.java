package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.selection.Selection;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.MessageAppMainView;

public class MessageAppMainController {
    private final DataManager dataManager;
    private final MessageAppMainView mainView;
    private final Session session;
    private final Selection selection;
    private final MessageController messageController;
    private final ChannelController channelController;
    private final UserController userController;
    private final ConnectController connectController;
    private final NavigationController navigationController;
    private final RegisterController registerController;
    private final LoginController loginController;
    private final MessageInputController messageInputController;

    public MessageAppMainController(DataManager dataManager, MessageAppMainView mainView) {
        this.dataManager = dataManager;
        this.mainView = mainView;
        this.selection = new Selection();
        this.session = new Session();
        this.loginController = new LoginController(dataManager, session);
        this.registerController = new RegisterController(dataManager);
        this.messageController = new MessageController(dataManager, session);
        this.channelController = new ChannelController(dataManager, selection, session);
        this.userController = new UserController(dataManager, selection, session);
        this.messageInputController = new MessageInputController(dataManager, session);
        this.connectController = new ConnectController(messageController, messageInputController, channelController, userController);
        this.navigationController = new NavigationController(mainView, dataManager, loginController, registerController, connectController);

        // FIX: enregistrer userController et channelController comme observateurs
        // pour recevoir les notifications de la base de données
        dataManager.addObserver(userController);
        dataManager.addObserver(channelController);
        dataManager.addObserver(messageController);

        session.addObserver(this.navigationController);
        selection.addObserver(this.messageController);
        selection.addObserver(this.messageInputController);
    }

    public void init() {
        mainView.getLogoutMenuItem().addActionListener(e -> handleLogout());

        // Quand l'utilisateur ferme la fenêtre (croix), on souhaite déconnecter et
        // réafficher l'état déconnecté avant de quitter.
        mainView.addWindowClosingAction(evt -> {
            try {
                handleLogout();
            } catch (Exception ex) {
                // ne pas empêcher la fermeture si la déconnexion échoue
                ex.printStackTrace();
            }
            // Afficher l'état déconnecté (sécurisé même si déjà déconnecté)
            try {
                mainView.showDisconnectedState();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            // Quitter l'application
            System.exit(0);
        });
    }

    private void handleLogout() {
        User u = session.getConnectedUser();
        if (u != null) {
            u.setOnline(false);
            session.disconnect();
            dataManager.sendUser(u);
        } else {
            // même si aucun utilisateur connecté, s'assurer que la session est fermée
            session.disconnect();
        }
    }
}