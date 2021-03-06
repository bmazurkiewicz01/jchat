package com.bmazurkiewicz01.client.controller;

import com.bmazurkiewicz01.client.model.ServerConnection;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class AddRoomDialogController {
    @FXML
    public AnchorPane root;
    @FXML
    public ImageView closeButton;
    @FXML
    public TextField nameField;
    @FXML
    public Label errorLabel;
    @FXML
    public Button addRoomButton;
    @FXML
    public Button cancelButton;

    public void initialize() {
        closeButton.setOnMouseClicked(this::closeStage);
    }

    @FXML
    public void handleAddRoomButton(Event actionEvent) {
        String roomName = nameField.getText();
        if (roomName.isBlank()) {
            handleError("Name cannot be blank", false);
        }
        else {
            String result = ServerConnection.getInstance().addRoom(roomName);
            if (result == null) {
                handleError("Add room error. Please try again.", false);
            } else if (result.equals("conn:roomadded")) {
                closeStage(actionEvent);
            } else if (result.equals("conn:roomerror")) {
                handleError(roomName + " is taken. Try another name.", false);
            }
        }
    }

    @FXML
    public void handleCancelButton(Event actionEvent) {
        closeStage(actionEvent);
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
        if (text != null) nameField.setText(text);
    }

    public void closeStage(Event actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
