package com.bmazurkiewicz01.client.controller;

import com.bmazurkiewicz01.client.View;
import com.bmazurkiewicz01.client.ViewSwitcher;
import com.bmazurkiewicz01.client.model.ServerConnection;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;


public class MainController {
    @FXML
    public TextArea chatTextArea;
    @FXML
    public Button sendMessageButton;
    @FXML
    public Button logoutButton;
    @FXML
    public TextField messageField;
    @FXML
    public Label errorLabel;
    @FXML
    public Button refreshButton;
    @FXML
    public ListView<String> usersListView;

    public void initialize() {
        ServerConnection.getInstance().updateMessage(this);
    }

    @FXML
    public void handleSendMessageButton() {
        String message = messageField.getText();
        if (message.isBlank()) {
            errorLabel.setVisible(true);
            errorLabel.setText("You cannot send blank message.");
        } else {
            if (ServerConnection.getInstance().isConnected()) {
                errorLabel.setVisible(false);
                ServerConnection.getInstance().sendMessage(message);
                messageField.clear();
            } else {
                errorLabel.setVisible(true);
                errorLabel.setText("Connection error. Please restart the application.\n");
            }
        }
    }

    @FXML
    public void handleLogoutButton() {
        ServerConnection.getInstance().close();
        ViewSwitcher.getInstance().switchView(View.LOGIN);
    }

    @FXML
    public void handleRefreshButton() {

    }

    public void updateTextArea(String message) {
        chatTextArea.appendText(message);
    }

    public void updateListView(ObservableList<String> activeUsers) {
        usersListView.setItems(activeUsers);
    }

    public void handleError(String message) {
        errorLabel.setVisible(true);
        messageField.setDisable(true);
        sendMessageButton.setDisable(true);
        updateTextArea(message);
    }

    public void closeConnection() {
        ServerConnection.getInstance().close();
    }

}
