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
    public void handleLoginButton(ActionEvent event) {
        String name = loginField.getText();
        if (name.isBlank()) {
            errorLabel.setVisible(true);
            errorLabel.setText("Please fill all the fields.");
        } else {
            if (ServerConnection.getInstance().connect(name)) {
                ViewSwitcher.getInstance().switchView(View.MAIN);
            }
        }
    }
}
