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
    public AnchorPane leftPane;
    @FXML
    public ImageView closeButton;
    @FXML
    public ImageView minimizeButton;

    @FXML
    public Button hamburgerButton;
    @FXML
    public Button removeRoomButton;
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
        ViewSwitcher.getInstance().setUpLeftPaneAnimation(root, leftPane, hamburgerButton);
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
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        if (message.isBlank()) {
            handleError("You cannot send blank message.", false);
        } else {
            if (ServerConnection.getInstance().isConnected()) {
                ServerConnection.getInstance().sendMessage(message);
                messageField.clear();
            } else {
                handleError("Connection error. Please restart the application.", false);
            }
        }
    }

    @FXML
    public void handleRemoveRoomButton() {
        ServerConnection.getInstance().deleteRoom();
        leaveRoom();
        ViewSwitcher.getInstance().getMainController().handleError("Room was successfully deleted.");
    }

    @FXML
    public void handleBackButton() {
        leaveRoom();
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
        removeRoomButton.setManaged(true);
        removeRoomButton.setVisible(true);

        usersListView.setCellFactory(stringListView -> {
            ListCell<String> cell = new ListCell<>();
            ContextMenu menu = new ContextMenu();

            MenuItem kickUser = new MenuItem();
            kickUser.textProperty().bind(Bindings.format("Kick %s", cell.itemProperty()));
            kickUser.setOnAction(e -> {
                String userName = cell.getItem();
                ServerConnection.getInstance().kickUser(userName);
            });

            MenuItem banUser = new MenuItem();
            banUser.textProperty().bind(Bindings.format("Ban %s", cell.itemProperty()));
            banUser.setOnAction(e -> {
                String userName = cell.getItem();
                ServerConnection.getInstance().banUser(userName);
            });

            menu.getItems().addAll(kickUser, banUser);
            cell.textProperty().bind(cell.itemProperty());
            cell.itemProperty().addListener((observableValue, s, t1) -> {
                if (t1 != null) {
                    String userName = ServerConnection.getInstance().getUserName();
                    kickUser.setDisable(t1.equals(userName));
                    banUser.setDisable(t1.equals(userName));
                }
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
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
        errorLabel.setText(message);
        if (fatal) {
            messageField.setDisable(true);
            sendMessageButton.setDisable(true);
        }
    }

    private void leaveRoom() {
        ServerConnection.getInstance().leaveRoom();
        ServerConnection.getInstance().setMainControllerInInputThread(ViewSwitcher.getInstance().getMainController());
        ServerConnection.getInstance().setRoomControllerInInputThread(null);

        ViewSwitcher.getInstance().switchView(View.MAIN, true);
    }

}
