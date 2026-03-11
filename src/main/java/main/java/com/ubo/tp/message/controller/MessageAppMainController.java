package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.NotificationService;
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
    private final ProfileController profileController;
    private final NotificationService notificationService;

    public MessageAppMainController(DataManager dataManager, MessageAppMainView mainView) {
        this.dataManager = dataManager;
        this.mainView = mainView;
        this.selection = new Selection();
        this.session = new Session();
        this.notificationService = new NotificationService();
        this.loginController = new LoginController(dataManager, session);
        this.registerController = new RegisterController(dataManager);
        this.messageController = new MessageController(dataManager, session);
        this.channelController = new ChannelController(dataManager, selection, session,notificationService);
        this.userController = new UserController(dataManager, selection, session,notificationService);
        this.messageInputController = new MessageInputController(dataManager, session);
        this.profileController = new ProfileController(dataManager, session);
        this.connectController = new ConnectController(messageController, messageInputController, channelController, userController);
        this.navigationController = new NavigationController(mainView, dataManager, loginController, registerController, connectController, profileController);

        dataManager.addObserver(userController);
        dataManager.addObserver(channelController);
        dataManager.addObserver(messageController);

        userController.setOnUserSelected(() -> channelController.getChannelListView().clearSelection());
        channelController.setOnChannelSelected(() -> userController.getUserListView().clearSelection());

        session.addObserver(this.navigationController);
        selection.addObserver(this.messageController);
        selection.addObserver(this.messageInputController);
    }

    public void init() {
        mainView.addLogoutAction(e -> handleLogout());

        mainView.addWindowClosingAction(evt -> {
            try { handleLogout(); } catch (Exception ex) { ex.printStackTrace(); }
            try { mainView.showDisconnectedState(); } catch (Exception ex) { ex.printStackTrace(); }
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
            session.disconnect();
        }
    }
}