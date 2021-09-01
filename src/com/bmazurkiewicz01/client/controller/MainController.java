package com.bmazurkiewicz01.client.controller;

import com.bmazurkiewicz01.client.model.ServerConnection;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainController {
    @FXML
    public TextArea chatTextArea;
    @FXML
    public Button sendMessageButton;
    @FXML
    public TextField messageField;
    @FXML
    public Button refreshButton;

    public void initialize() {
        ServerConnection.getInstance().updateMessage(this);
    }

    @FXML
    public void handleSendMessageButton(ActionEvent event) {
        String message = messageField.getText();
        if (message.isBlank()) {

        } else {
            if (ServerConnection.getInstance().isConnected()) {
                ServerConnection.getInstance().sendMessage(message);
                messageField.clear();
            } else {

            }
        }
    }

    @FXML
    public void handleRefreshButton(ActionEvent event) {

    }

    public void updateTextArea(String message) {
        Platform.runLater(() -> chatTextArea.appendText(message));
    }

    public void closeConnection() {
        if (ServerConnection.getInstance() != null && ServerConnection.getInstance().isConnected()) {
            ServerConnection.getInstance().close();
        }
    }
}
