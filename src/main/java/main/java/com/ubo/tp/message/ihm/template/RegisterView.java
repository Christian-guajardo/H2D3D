package main.java.com.ubo.tp.message.ihm.template;

import main.java.com.ubo.tp.message.Response;
import main.java.com.ubo.tp.message.controller.LoginController;
import main.java.com.ubo.tp.message.controller.NavigationController;
import main.java.com.ubo.tp.message.controller.RegisterController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class RegisterView extends JPanel {
    private RegisterController registerController;

    private JTextField tag;
    private JTextField username;
    private JPasswordField password;
    private JPasswordField confirmPassword;
    private JButton registerButton;

    public RegisterView(RegisterController registerController) {
        super(new GridBagLayout());
        this.registerController = registerController;
        initComponents();
    }

    private void initComponents() {
        tag = new JTextField(20);
        username = new JTextField(20);
        password = new JPasswordField(20);
        confirmPassword = new JPasswordField(20);
        registerButton = new JButton("S'inscrire");
        registerButton.addActionListener(e -> {
            Response result = registerController.onRegister(this.getTag(), this.getUsername(), this.getPassword(), this.getConfirmPassword());
            if (result.isSuccess()) {
                showInfo(result.getMessage());
            } else {
                showError("Erreur", result.getMessage());
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

        this.add(new JLabel("User Tag:"),
                new GridBagConstraints(0, 0,
                        1, 1, 0, 0,
                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                        insets, 0, 0));

        this.add(tag,
                new GridBagConstraints(1, 0,
                        1, 1, 1, 0,
                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                        insets, 0, 0));

        this.add(new JLabel("Username:"), new GridBagConstraints(0, 1,
                1, 1, 0, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                insets, 0, 0));

        this.add(username, new GridBagConstraints(1, 1,
                1, 1, 1, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                insets, 0, 0));

        this.add(new JLabel("Mot de passe:"),
                new GridBagConstraints(0, 2,
                        1, 1, 0, 0,
                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                        insets, 0, 0));

        this.add(password, new GridBagConstraints(1, 2,
                1, 1, 1, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                insets, 0, 0));

        this.add(new JLabel("Confirmer mot de passe:"), new GridBagConstraints(0, 3,
                1, 1, 0, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                insets, 0, 0));

        this.add(confirmPassword, new GridBagConstraints(1, 3,
                1, 1, 1, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                insets, 0, 0));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(registerButton);

        this.add(buttonPanel, new GridBagConstraints(0, 4,
                2, 1, 0, 0,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                insets, 0, 0));
    }

    public String getTag() {
        return tag.getText();
    }

    public String getUsername() {
        return username.getText();
    }

    public String getPassword() {
        return new String(password.getPassword());
    }

    public String getConfirmPassword() {
        return new String(confirmPassword.getPassword());
    }

    public void setRegisterActionListener(ActionListener listener) {
        registerButton.addActionListener(listener);
    }
}