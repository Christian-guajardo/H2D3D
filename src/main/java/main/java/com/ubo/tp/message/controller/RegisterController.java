package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.ihm.MessageAppMainView;
import main.java.com.ubo.tp.message.ihm.template.RegisterView;
import main.java.com.ubo.tp.message.datamodel.User;


public class RegisterController {

    private final UserController userController;


    public RegisterController(UserController userController) {
        this.userController = userController;

    }

    public void attach(RegisterView registerView) {
        registerView.setRegisterActionListener(event -> onRegister(registerView));
    }

    private void onRegister(RegisterView rv) {
        if (!rv.getConfirmPassword().equals(rv.getPassword())) {
            rv.showError("Erreur", "Les mots de passe ne correspondent pas.");
            return;
        }
        User user = userController.register(rv.getTag(), rv.getUsername(), rv.getPassword());
        if (user == null) {
            rv.showError("Erreur", "Échec de l'inscription. Le tag est peut-être déjà utilisé ou les champs sont invalides.");
        } else {
            rv.showInfo("Inscription réussie ! Vous pouvez maintenant vous connecter.");
        }
    }
}
