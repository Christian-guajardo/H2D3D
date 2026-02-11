package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.datamodel.User;

public class ConnectController implements ISessionObserver {
    private final DataManager mDataManager;

    public ConnectController(DataManager mDataManager) {
        this.mDataManager = mDataManager;
    }


    @Override
    public void notifyLogin(User connectedUser) {

    }

    @Override
    public void notifyLogout() {

    }
}
