package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.selection.Selection;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.MessageAppMainView;
import main.java.com.ubo.tp.message.ihm.template.component.MessageListView;

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
        this.channelController = new ChannelController(dataManager, selection);
        this.userController = new UserController(dataManager, selection);
        this.messageInputController = new MessageInputController(dataManager, session);
        this.connectController = new ConnectController(messageController,messageInputController, channelController, userController);
        this.navigationController = new NavigationController(mainView,dataManager,loginController,registerController,connectController);


        session.addObserver(this.navigationController);
        selection.addObserver(this.messageController);
    }

    public void init() {
        mainView.getLogoutMenuItem().addActionListener(e -> handleLogout());
    }

    private void handleLogout() {
        session.disconnect();

    }


}