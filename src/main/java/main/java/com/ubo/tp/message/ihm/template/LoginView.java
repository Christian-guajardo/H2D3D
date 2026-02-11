package main.java.com.ubo.tp.message.ihm.template;

import main.java.com.ubo.tp.message.controller.UserController;
import main.java.com.ubo.tp.message.datamodel.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginView extends JPanel{
    private JTextField username;
    private JPasswordField password;
    private JButton loginButton;


    public LoginView() {
        super((new GridBagLayout()));
        initComponents();
    }

    private void initComponents() {

        username = new JTextField(20);
        password = new JPasswordField(20);
        loginButton = new JButton("Se connecter");

        setuplayout();

    }
    public void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public void showError(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private void setuplayout() {

        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        Insets insets = new Insets(5, 5, 5, 5);

        this.add(new JLabel("Username:"),new GridBagConstraints(0, 1,
                1, 1,
                1, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                insets,
                0, 0));

        this.add(username,new GridBagConstraints(1, 1,
                1, 1,
                1, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                insets,
                0, 0));
        this.add(new JLabel("Mot de passe:"),
                new GridBagConstraints(0, 2,
                        1, 1,
                        0, 0,
                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                        insets,
                        0, 0));

        this.add(password, new GridBagConstraints(1, 2,
                1, 1,
                1, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                insets,
                0, 0));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(loginButton);

        this.add(buttonPanel, new GridBagConstraints(0, 3,
                2, 1,
                0, 0,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                insets,
                0, 0));

    }

    public String getUsername() {
        return username.getText();
    }


    public String getPassword() {
        return new String(password.getPassword());
    }



    public void setRegisterActionListener(ActionListener listener) {
        loginButton.addActionListener(listener);
    }
}
