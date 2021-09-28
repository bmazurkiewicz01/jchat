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

public class ChangePasswordController {
    @FXML
    public AnchorPane root;
    @FXML
    public Button changeButton;
    @FXML
    public PasswordField changeField;
    @FXML
    public Button cancelButton;
    @FXML
    public Label errorLabel;
    @FXML
    public ImageView closeButton;

    public void initialize() {
        changeField.setText(ServerConnection.getInstance().getUserName());
        closeButton.setOnMouseClicked(this::closeStage);
        cancelButton.setOnMouseClicked(this::closeStage);
    }

    @FXML
    public void handleChangePasswordButton(ActionEvent actionEvent) {
        String newPassword = changeField.getText();

        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        if (newPassword.length() < 8) {
            changeErrorLabel("Name must have at least 8 characters.");
        } else if (newPassword.length() > 20) {
            changeErrorLabel("Name cannot have more than 20 characters.");
        } else if (!newPassword.matches("(.*[a-z].*)")) {
            changeErrorLabel("Password must have lower case character.");
        } else if (!newPassword.matches("(.*[A-Z].*)")) {
            changeErrorLabel("Password must have upper case character.");
        } else if (!newPassword.matches("(.*[0-9].*)")) {
            changeErrorLabel("Password must have at least one number.");
        } else if (!newPassword.matches("(.*[@,#,$,%,^,&,*,(,)].*)")) {
            changeErrorLabel("Password must have at least one special character.");
        } else if (newPassword.equals(ServerConnection.getInstance().getUserName())){
            changeErrorLabel("New name cannot be same as old name.");
        } else if (ServerConnection.getInstance().changeUserPassword(newPassword)) {
            closeStage(actionEvent);
        } else {
            changeErrorLabel("Connection error. Please try again.");
        }
    }

    public void closeStage(Event actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void changeErrorLabel(String message) {
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
        errorLabel.setText(message);
    }
}
