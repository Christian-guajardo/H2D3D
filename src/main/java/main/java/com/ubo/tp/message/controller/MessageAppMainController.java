package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.MessageAppMainView;


public class MessageAppMainController implements ISessionObserver {
    private final DataManager mDataManager;
    private final MessageAppMainView view;
    private final Session session = new Session();
    private final RegisterController registerController;
    private final LoginController loginController;

    public MessageAppMainController(DataManager mDataManager, MessageAppMainView view, UserController userController) {
        session.addObserver(this);
        this.mDataManager = mDataManager;
        this.view = view;
        this.registerController = new RegisterController(userController);
        this.loginController = new LoginController(userController, session);

    }

    public void init() {

        view.setOnChooseExchangeAction(e -> setExchangeDirectory());
        view.setOnRegisterViewCreated(registerController::attach);
        view.setOnLoginViewCreated(loginController::attach);
        view.setOnLogoutAction(e -> session.disconnect());
    }
    public void setExchangeDirectory(){
        	String directory = view.getExchangeDirectory();
            if (directory != null && !directory.isEmpty()) {
                mDataManager.setExchangeDirectory(directory);
                view.showInfo("Le répertoire d'échange a été défini avec succès.");
            }else{
                view.showError("Erreur", "Le répertoire d'échange est invalide. Veuillez sélectionner un répertoire valide.");
            }
    }

    @Override
    public void notifyLogin(User connectedUser) {
        view.connect();
    }

    @Override
    public void notifyLogout() {
        view.disconnect();
    }
}
