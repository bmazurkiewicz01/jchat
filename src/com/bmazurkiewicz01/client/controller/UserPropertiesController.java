package com.bmazurkiewicz01.client.controller;

import com.bmazurkiewicz01.client.model.ServerConnection;
import com.bmazurkiewicz01.client.view.View;
import com.bmazurkiewicz01.client.view.ViewSwitcher;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class UserPropertiesController {
    @FXML
    public AnchorPane root;
    @FXML
    public ImageView minimizeButton;
    @FXML
    public ImageView closeButton;
    @FXML
    public TextField nameField;
    @FXML
    public Button changeNameButton;
    @FXML
    public Button changePasswordButton;

    public void initialize() {
        nameField.setText(ServerConnection.getInstance().getUserName());
        closeButton.setOnMouseClicked(this::closeStage);
        minimizeButton.setOnMouseClicked(e -> ViewSwitcher.getInstance().getStage().setIconified(true));
    }

    @FXML
    public void handleChangeNameButton(ActionEvent actionEvent) {
        final Stage changeNameDialog = new Stage();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(View.CHANGE_NAME_DIALOG.getFileName()));
        Parent root;
        try {
            root = fxmlLoader.load();

            changeNameDialog.setScene(new Scene(root));
            changeNameDialog.getScene().setFill(Color.TRANSPARENT);
            changeNameDialog.initModality(Modality.APPLICATION_MODAL);
            changeNameDialog.initOwner(this.root.getScene().getWindow());
            changeNameDialog.initStyle(StageStyle.TRANSPARENT);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        ChangeNameController changeNameController = fxmlLoader.getController();
        changeNameController.setUserPropertiesController(this);

        changeNameDialog.showAndWait();
    }

    @FXML
    public void handleChangePasswordButton(ActionEvent actionEvent) {
        final Stage changePasswordDialog = new Stage();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(View.CHANGE_PASSWORD_DIALOG.getFileName()));
        Parent root;
        try {
            root = fxmlLoader.load();

            changePasswordDialog.setScene(new Scene(root));
            changePasswordDialog.getScene().setFill(Color.TRANSPARENT);
            changePasswordDialog.initModality(Modality.APPLICATION_MODAL);
            changePasswordDialog.initOwner(this.root.getScene().getWindow());
            changePasswordDialog.initStyle(StageStyle.TRANSPARENT);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }


        changePasswordDialog.showAndWait();
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
