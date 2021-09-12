package com.bmazurkiewicz01.client.controller;

import com.bmazurkiewicz01.client.View;
import com.bmazurkiewicz01.client.ViewSwitcher;
import com.bmazurkiewicz01.client.model.ServerConnection;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class RoomController {
    @FXML
    public TextArea chatTextArea;
    @FXML
    public Button sendMessageButton;
    @FXML
    public Button backButton;
    @FXML
    public TextField messageField;
    @FXML
    public Label errorLabel;
    @FXML
    public ListView<String> usersListView;

    public void initialize() {
        ServerConnection.getInstance().updateMessage(this);
    }

    @FXML
    public void handleSendMessageButton() {
        String message = messageField.getText();
        if (message.isBlank()) {
            handleError("You cannot send blank message.", false);
        } else {
            if (ServerConnection.getInstance().isConnected()) {
                errorLabel.setVisible(false);
                ServerConnection.getInstance().sendMessage(message);
                messageField.clear();
            } else {
                handleError("Connection error. Please restart the application.", false);
            }
        }
    }

    @FXML
    public void handleBackButton() {
        ViewSwitcher.getInstance().switchView(View.MAIN);
    }

    public void updateTextArea(String message) {
        chatTextArea.appendText(message);
    }

    public void updateListView(List<String> activeUsers) {
        usersListView.setItems(FXCollections.observableList(activeUsers));
    }

    public void handleError(String message, boolean fatal) {
        if (fatal) {
            messageField.setDisable(true);
            sendMessageButton.setDisable(true);
        }
        errorLabel.setVisible(true);
        updateTextArea(message);
    }
}
