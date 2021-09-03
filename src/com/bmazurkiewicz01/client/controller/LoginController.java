package com.bmazurkiewicz01.client.controller;

import com.bmazurkiewicz01.client.View;
import com.bmazurkiewicz01.client.ViewSwitcher;
import com.bmazurkiewicz01.client.model.ServerConnection;
import javafx.event.ActionEvent;
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
        String name = loginField.getText();
        String password = passwordField.getText();
        if (name.isBlank() || password.isBlank()) {
            errorLabel.setVisible(true);
            errorLabel.setText("Please fill all the fields.");
        } else {
            if (ServerConnection.getInstance().connect(name, password)) {
                ViewSwitcher.getInstance().switchView(View.MAIN);
            }
            else {
                errorLabel.setVisible(true);
                errorLabel.setText("Connection refused. Try again later.");
            }
        }
    }

    @FXML
    public void handleRegisterButton() {
        ViewSwitcher.getInstance().switchView(View.REGISTER);
    }
}
