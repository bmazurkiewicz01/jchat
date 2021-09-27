package com.bmazurkiewicz01.client.controller;

import com.bmazurkiewicz01.client.model.ServerConnection;
import com.bmazurkiewicz01.client.view.View;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

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
            final Stage userPropertiesDialog = new Stage();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(View.USER_PROPERTIES.getFileName()));
            Parent root;
            try {
                root = fxmlLoader.load();

                userPropertiesDialog.setScene(new Scene(root));
                userPropertiesDialog.getScene().setFill(Color.TRANSPARENT);
                userPropertiesDialog.initModality(Modality.APPLICATION_MODAL);
                userPropertiesDialog.initOwner(this.root.getScene().getWindow());
                userPropertiesDialog.initStyle(StageStyle.TRANSPARENT);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            userPropertiesDialog.showAndWait();
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
