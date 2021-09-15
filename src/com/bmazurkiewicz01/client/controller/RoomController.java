package com.bmazurkiewicz01.client.controller;

import com.bmazurkiewicz01.client.View;
import com.bmazurkiewicz01.client.ViewSwitcher;
import com.bmazurkiewicz01.client.model.ServerConnection;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class RoomController {
    @FXML
    public Label roomLabel;
    @FXML
    public Label ownerLabel;
    @FXML
    public TextArea chatTextArea;
    @FXML
    public Button sendMessageButton;
    @FXML
    public Button logoutButton;
    @FXML
    public Button backButton;
    @FXML
    public TextField messageField;
    @FXML
    public Label errorLabel;
    @FXML
    public ListView<String> usersListView;

    public void initialize() {
        ServerConnection.getInstance().updateMessage();
        ServerConnection.getInstance().setRoomControllerInInputThread(this);
    }

    @FXML
    public void handleSendMessageButton() {
        String message = messageField.getText();
        if (message.isBlank()) {
            handleError("You cannot send blank message.", false);
        } else {
            if (ServerConnection.getInstance().isConnected()) {
                errorLabel.setVisible(false);
                errorLabel.setManaged(false);
                ServerConnection.getInstance().sendMessage(message);
                messageField.clear();
            } else {
                handleError("Connection error. Please restart the application.", false);
            }
        }
    }

    @FXML
    public void handleBackButton() {
        ServerConnection.getInstance().leaveRoom();
        ServerConnection.getInstance().setRoomControllerInInputThread(null);
        ServerConnection.getInstance().setMainControllerInInputThread(ViewSwitcher.getInstance().getMainController());

        ViewSwitcher.getInstance().switchView(View.MAIN, true);
    }

    @FXML
    public void handleLogoutButton() {
        ServerConnection.getInstance().leaveRoom();
        ServerConnection.getInstance().setMainControllerInInputThread(null);
        ServerConnection.getInstance().setRoomControllerInInputThread(null);
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new MainController().closeConnection();
        ViewSwitcher.getInstance().switchView(View.LOGIN);

    }

    public void setUpOwnerRoom() {
        usersListView.setCellFactory(stringListView -> {
            ListCell<String> cell = new ListCell<>();
            ContextMenu menu = new ContextMenu();
            MenuItem kickUser = new MenuItem();
            kickUser.textProperty().bind(Bindings.format("Kick %s", cell.itemProperty()));
            kickUser.setOnAction(e -> {
                String userName = cell.getItem();
                ServerConnection.getInstance().kickUser(userName);
            });

            menu.getItems().add(kickUser);

            cell.textProperty().bind(cell.itemProperty());
            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(menu);
                }
            });
            return cell;
        });
    }

    public void updateTextArea(String message) {
        chatTextArea.appendText(message);
    }

    public void updateListView(List<String> activeUsers) {
        usersListView.setItems(FXCollections.observableList(activeUsers));
    }

    public void setRoomAndOwnerText(String room, String owner) {
        roomLabel.setText(room);
        ownerLabel.setText(owner);
    }

    public void handleError(String message, boolean fatal) {
        if (fatal) {
            messageField.setDisable(true);
            sendMessageButton.setDisable(true);
        }
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
        updateTextArea(message);
    }

}
