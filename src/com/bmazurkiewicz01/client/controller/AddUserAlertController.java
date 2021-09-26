package com.bmazurkiewicz01.client.controller;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class AddRoomAlertController {
    @FXML
    public AnchorPane root;
    @FXML
    public ImageView closeButton;

    public void initialize() {
        closeButton.setOnMouseClicked(this::closeStage);
    }

    public void closeStage(Event actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
