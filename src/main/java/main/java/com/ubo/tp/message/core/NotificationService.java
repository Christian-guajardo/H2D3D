package main.java.com.ubo.tp.message.core;

import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.net.URL;

public class NotificationService {
    private TrayIcon trayIcon;

    public NotificationService() {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();


            Image image = null;
            URL imageURL = getClass().getResource("/icon.png");
            if (imageURL != null) {
                image = Toolkit.getDefaultToolkit().createImage(imageURL);
            } else {

                image = new java.awt.image.BufferedImage(16, 16, java.awt.image.BufferedImage.TYPE_INT_RGB);
            }

            this.trayIcon = new TrayIcon(image, "MessageApp");
            this.trayIcon.setImageAutoSize(true);
            this.trayIcon.setToolTip("MessageApp - Notifications actives");

            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println("Impossible d'ajouter l'icône au SystemTray : " + e.getMessage());
            }
        }
    }

    public void notifyMention(String channelName, String senderName, String messageContent) {
        // Tronquer le message s'il est trop long pour la notification
        String summary = messageContent.length() > 50 ? messageContent.substring(0, 47) + "..." : messageContent;
        sendNotification("Mention dans #" + channelName, senderName + " : " + summary);
    }

    public void notifyDirectMessage(String senderName, String messageContent) {
        String summary = messageContent.length() > 50 ? messageContent.substring(0, 47) + "..." : messageContent;
        sendNotification("Message Privé de " + senderName, summary);
    }

    private void sendNotification(String title, String text) {
        if (trayIcon != null) {

            trayIcon.displayMessage(title, text, MessageType.INFO);
        }
    }

    public void shutdown() {
        if (SystemTray.isSupported() && trayIcon != null) {
            SystemTray.getSystemTray().remove(trayIcon);
        }
    }
}