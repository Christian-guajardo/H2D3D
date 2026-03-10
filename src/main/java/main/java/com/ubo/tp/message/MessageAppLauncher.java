package main.java.com.ubo.tp.message;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.database.Database;
import main.java.com.ubo.tp.message.core.database.DatabaseObserver;
import main.java.com.ubo.tp.message.core.database.DbConnector;
import main.java.com.ubo.tp.message.core.database.EntityManager;
import main.java.com.ubo.tp.message.ihm.MessageApp;
import mock.MessageAppMock;

public class MessageAppLauncher {
	protected static boolean IS_MOCK_ENABLED = true;

	public static void launchApp(){
		Database database = new Database();
		EntityManager entityManager = new EntityManager(database);
		DataManager dataManager = new DataManager(database, entityManager);
		DatabaseObserver dbObserver = new DatabaseObserver();

		dataManager.addObserver(dbObserver);

		DbConnector dbConnector = new DbConnector(database);

		if (IS_MOCK_ENABLED) {
			MessageAppMock mock = new MessageAppMock(dbConnector, dataManager);
			mock.showGUI();
		}

		MessageApp messageApp = new MessageApp(dataManager);
		messageApp.init();
		messageApp.show();
	}

	public static void main(String[] args) {
		launchApp();
		launchApp();
	}
}