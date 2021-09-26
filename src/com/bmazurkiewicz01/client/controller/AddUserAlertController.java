package com.bmazurkiewicz01.client.controller;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class AddUserAlertController {
    @FXML
    public AnchorPane root;
    @FXML
    public ImageView closeButton;
    @FXML
    public Label mainLabel;
    @FXML
    public Button confirmButton;

    public void initialize() {
        closeButton.setOnMouseClicked(this::closeStage);
        confirmButton.setOnAction(this::closeStage);
    }

    public void setMainLabel(String text) {
        if (text != null) mainLabel.setText(text);
    }

    public void closeStage(Event actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
