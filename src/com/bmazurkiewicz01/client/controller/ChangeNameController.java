package com.bmazurkiewicz01.client.controller;

import com.bmazurkiewicz01.client.model.ServerConnection;
import com.bmazurkiewicz01.client.view.ViewSwitcher;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ChangeNameController {
    @FXML
    public AnchorPane root;
    @FXML
    public ImageView closeButton;
    @FXML
    public Button changeButton;
    @FXML
    public Button cancelButton;
    @FXML
    public TextField textField;
    @FXML
    public Label errorLabel;

    private UserPropertiesController userPropertiesController;

    public void initialize() {
        textField.setText(ServerConnection.getInstance().getUserName());
        closeButton.setOnMouseClicked(this::closeStage);
        cancelButton.setOnMouseClicked(this::closeStage);
    }

    @FXML
    public void handleChangeNameButton(ActionEvent actionEvent) {
        String newName = textField.getText();

        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        if (newName.length() < 5) {
            changeErrorLabel("Name must have at least 5 characters.");
        } else if (newName.length() > 15) {
            changeErrorLabel("Name cannot have more than 15 characters.");
        } else if (newName.equals(ServerConnection.getInstance().getUserName())){
            changeErrorLabel("New name cannot be same as old name.");
        } else if (ServerConnection.getInstance().changeUserName(newName)) {
            System.out.println("Name has been changed.");

            ServerConnection.getInstance().setUserName(newName);
            userPropertiesController.setNameField(newName);
            ViewSwitcher.getInstance().getMainController().setHelloLabel("Hello, " + newName + "!");

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

    public void setUserPropertiesController(UserPropertiesController userPropertiesController) {
        this.userPropertiesController = userPropertiesController;
    }
}
