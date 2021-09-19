package com.bmazurkiewicz01.client.controller;

import com.bmazurkiewicz01.client.model.ServerConnection;
import com.bmazurkiewicz01.client.view.View;
import com.bmazurkiewicz01.client.view.ViewSwitcher;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.List;

public class RoomController {
    @FXML
    public AnchorPane root;
    @FXML
    public ImageView closeButton;
    @FXML
    public ImageView minimizeButton;

    @FXML
    public Button logoutButton;
    @FXML
    public Button backButton;

    @FXML
    public Label roomLabel;
    @FXML
    public Label ownerLabel;

    @FXML
    public TextArea chatTextArea;
    @FXML
    public TextField messageField;
    @FXML
    public Button sendMessageButton;
    @FXML
    public ListView<String> usersListView;
    @FXML
    public Label errorLabel;

    private double x,y;

    public void initialize() {
        ServerConnection.getInstance().updateMessage();
        ServerConnection.getInstance().setRoomControllerInInputThread(this);

        root.setOnMousePressed(e -> {
            x = e.getSceneX();
            y = e.getSceneY();
        });
        root.setOnMouseDragged(e -> {
            ViewSwitcher.getInstance().getStage().setX(e.getScreenX() - this.x);
            ViewSwitcher.getInstance().getStage().setY(e.getScreenY() - this.y);
        });

        closeButton.setOnMouseClicked(e -> ViewSwitcher.getInstance().getStage().close());
        minimizeButton.setOnMouseClicked(e -> ViewSwitcher.getInstance().getStage().setIconified(true));
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
            cell.itemProperty().addListener((observableValue, s, t1) -> {
                if (t1 != null) kickUser.setDisable(t1.equals(ServerConnection.getInstance().getUserName()));
            });
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
