package com.bmazurkiewicz01.client.controller;

import com.bmazurkiewicz01.client.model.ServerConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AddRoomDialogController {
    @FXML
    public TextField nameField;
    @FXML
    public Label errorLabel;

    public void processResults() {
        ServerConnection.getInstance().addRoom(nameField.getText());
    }

    public void handleError(String message, boolean fatal) {
        if (fatal) {
            nameField.setDisable(true);
        }
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
        errorLabel.setText(message);
    }

    public void setNameField(String text) {
        if (text != null) nameField.setPromptText(text);
    }

    public TextField getNameField() {
        return nameField;
    }

}
