package main.java.com.ubo.tp.message.ihm.template;

import main.java.com.ubo.tp.message.Response;
import main.java.com.ubo.tp.message.controller.LoginController;
import main.java.com.ubo.tp.message.controller.NavigationController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginView extends JPanel {
    private JTextField username;
    private JPasswordField password;
    private JButton loginButton;
    private LoginController loginController;

    public LoginView(LoginController loginController) {
        super(new GridBagLayout());
        this.loginController=loginController;
        initComponents();
    }

    private void initComponents() {
        username = new JTextField(20);
        password = new JPasswordField(20);
        loginButton = new JButton("Se connecter");
        loginButton.addActionListener(e->{
            Response rs = loginController.onLogin(this.getPassword(),this.getUsername());
            if(rs.isSuccess()){
                showInfo(rs.getMessage());
            }else{
                showError("Erreur de connexion", rs.getMessage());
            }
        });
        setupLayout();
    }

    public void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public void showError(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private void setupLayout() {
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        Insets insets = new Insets(5, 5, 5, 5);

        this.add(new JLabel("Username:"), new GridBagConstraints(0, 0,
                1, 1, 0, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                insets, 0, 0));

        this.add(username, new GridBagConstraints(1, 0,
                1, 1, 1, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                insets, 0, 0));

        this.add(new JLabel("Mot de passe:"),
                new GridBagConstraints(0, 1,
                        1, 1, 0, 0,
                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                        insets, 0, 0));

        this.add(password, new GridBagConstraints(1, 1,
                1, 1, 1, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                insets, 0, 0));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(loginButton);

        this.add(buttonPanel, new GridBagConstraints(0, 2,
                2, 1, 0, 0,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                insets, 0, 0));
    }

    public String getUsername() {
        return username.getText();
    }

    public String getPassword() {
        return new String(password.getPassword());
    }

    public void setLoginActionListener(ActionListener listener) {
        loginButton.addActionListener(listener);
    }
}