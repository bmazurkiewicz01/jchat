package com.bmazurkiewicz01.client.controller;

import com.bmazurkiewicz01.client.View;
import com.bmazurkiewicz01.client.ViewSwitcher;
import com.bmazurkiewicz01.client.model.ServerConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class RegisterDialogController {
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
        String name = loginField.getText();
        String password = passwordField.getText();
        if (name.isBlank() || password.isBlank()) {
            errorLabel.setVisible(true);
            errorLabel.setText("Please fill all the fields.");
        } else {
            if (ServerConnection.getInstance().register(name, password)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, name + " has been registered successfully.");
                alert.setTitle("Register success");
                alert.showAndWait();
                ViewSwitcher.getInstance().switchView(View.LOGIN);
            }
            else {
                errorLabel.setVisible(true);
                errorLabel.setText("Register failed. Please try again.");
            }
        }
    }

    @FXML
    public void handleCancelButton() {
        ViewSwitcher.getInstance().switchView(View.LOGIN);
    }
}
