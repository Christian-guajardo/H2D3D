package main.java.com.ubo.tp.message.controller;

public class ConnectController {
    private final MessageController messageController;
    private final ChannelController channelController;
    private final UserController userController;

    public ConnectController(MessageController messageController, ChannelController channelController, UserController userController) {
        this.messageController = messageController;
        this.channelController = channelController;
        this.userController = userController;
    }

    public UserController getUserController() {
        return userController;
    }

    public ChannelController getChannelController() {
        return channelController;
    }

    public MessageController getMessageController() {
        return messageController;
    }
}
