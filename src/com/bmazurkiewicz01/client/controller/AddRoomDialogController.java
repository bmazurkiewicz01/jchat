package com.bmazurkiewicz01.client.controller;

import com.bmazurkiewicz01.client.Room;
import com.bmazurkiewicz01.client.model.ServerConnection;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class AddRoomDialogController {
    @FXML
    public TextField nameField;
    @FXML
    public TextField ownerField;

    public void processResults() {
        ServerConnection.getInstance().addRoom(new Room(nameField.getText(), ownerField.getText(), 0));
    }
}
