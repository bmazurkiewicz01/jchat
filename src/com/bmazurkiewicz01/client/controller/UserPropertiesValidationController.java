package com.bmazurkiewicz01.client.controller;

import com.bmazurkiewicz01.client.model.ServerConnection;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class UserPropertiesValidationController {
    @FXML
    public AnchorPane root;
    @FXML
    public ImageView closeButton;
    @FXML
    public Button cancelButton;
    @FXML
    public Button confirmButton;
    @FXML
    public PasswordField passwordField;
    @FXML
    public Label errorLabel;

    public void initialize() {
        closeButton.setOnMouseClicked(this::closeStage);
        cancelButton.setOnAction(this::closeStage);
    }

    @FXML
    public void handleConfirmButton(ActionEvent actionEvent) {
        errorLabel.setManaged(false);
        errorLabel.setVisible(false);
        String password = passwordField.getText();
        if (password.isBlank()) handleError("Password cannot be blank.");
        else if (ServerConnection.getInstance().validatePassword(password)) {
            System.out.println("success");
            closeStage(actionEvent);
        } else handleError("Invalid password.");
    }

    public void closeStage(Event actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void handleError(String text) {
        errorLabel.setManaged(true);
        errorLabel.setVisible(true);
        errorLabel.setText(text);
    }
}
