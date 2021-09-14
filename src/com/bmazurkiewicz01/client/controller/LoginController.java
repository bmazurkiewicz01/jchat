package com.bmazurkiewicz01.client.controller;

import com.bmazurkiewicz01.client.View;
import com.bmazurkiewicz01.client.ViewSwitcher;
import com.bmazurkiewicz01.client.model.ServerConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


public class LoginController {
    @FXML
    public TextField loginField;
    @FXML
    public Button loginButton;
    @FXML
    public Label errorLabel;
    @FXML
    public TextField passwordField;
    @FXML
    public Button registerButton;

    @FXML
    public void handleLoginButton() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        String name = loginField.getText();
        String password = passwordField.getText();
        if (name.isBlank() && password.isBlank()) {
            changeErrorLabel("Name and Password cannot be empty.");
        } else if (name.isBlank()) {
            changeErrorLabel("Name cannot be empty.");
        } else if (password.isBlank()) {
            changeErrorLabel("Password cannot be empty");
        } else {
            String result = ServerConnection.getInstance().connect(name, password);
            if (result == null) {
                changeErrorLabel("Connection refused. Try again later.");
            }
            else if (result.equals("conn:invalid")) {
                changeErrorLabel("Invalid name or password.");
            }
            else if (result.equals("conn:isalready")) {
                changeErrorLabel(String.format("%s is already on the server", name));
            }
            else {
                ViewSwitcher.getInstance().switchView(View.MAIN);
            }
        }
    }

    @FXML
    public void handleRegisterButton() {
        ViewSwitcher.getInstance().switchView(View.REGISTER);
    }

    public void changeErrorLabel(String message) {
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
        errorLabel.setText(message);
    }
}
