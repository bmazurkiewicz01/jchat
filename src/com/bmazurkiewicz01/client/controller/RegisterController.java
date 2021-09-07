package com.bmazurkiewicz01.client.controller;

import com.bmazurkiewicz01.client.View;
import com.bmazurkiewicz01.client.ViewSwitcher;
import com.bmazurkiewicz01.client.model.ServerConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class RegisterController {
    @FXML
    public TextField loginField;
    @FXML
    public Button registerButton;
    @FXML
    public Label errorLabel;
    @FXML
    public TextField passwordField;
    @FXML
    public Button cancelButton;

    @FXML
    public void handleRegisterButton() {
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
            String result = ServerConnection.getInstance().register(name, password);
            if (result == null || result.equals("conn:failed")) {
                changeErrorLabel("Register failed. Please try again.");
            } else if (result.equals("conn:taken")) {
                changeErrorLabel("Name is taken. Try another one.");
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, name + " has been registered successfully.");
                alert.setTitle("Register success");
                alert.showAndWait();
                ViewSwitcher.getInstance().switchView(View.LOGIN);
            }
        }
    }

    @FXML
    public void handleCancelButton() {
        ViewSwitcher.getInstance().switchView(View.LOGIN);
    }

    public void changeErrorLabel(String message) {
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
        errorLabel.setText(message);
    }
}
