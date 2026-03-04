package main.java.com.ubo.tp.message.controller;

public class ConnectController {
    private final MessageController messageController;
    private final ChannelController channelController;
    private final UserController userController;
    private final MessageInputController messageInputController;

    public ConnectController(MessageController messageController, MessageInputController messageInputController, ChannelController channelController, UserController userController) {
        this.messageController = messageController;
        this.messageInputController=messageInputController;
        this.channelController = channelController;
        this.userController = userController;
    }

    public MessageInputController getMessageInputController() {
        return messageInputController;
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
