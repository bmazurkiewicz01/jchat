package com.bmazurkiewicz01.client.controller;

import com.bmazurkiewicz01.client.model.ServerConnection;
import com.bmazurkiewicz01.client.view.View;
import com.bmazurkiewicz01.client.view.ViewSwitcher;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class RegisterController {
    @FXML
    private AnchorPane root;
    @FXML
    public ImageView minimizeButton;
    @FXML
    public ImageView closeButton;

    @FXML
    public TextField loginField;
    @FXML
    public TextField passwordField;

    @FXML
    public Button registerButton;
    @FXML
    public Button cancelButton;
    @FXML
    public Label errorLabel;

    private double x, y;

    public void initialize() {
        root.setOnMousePressed(e -> {
            x = e.getSceneX();
            y = e.getSceneY();
        });
        root.setOnMouseDragged(e -> {
            ViewSwitcher.getInstance().getStage().setX(e.getScreenX() - this.x);
            ViewSwitcher.getInstance().getStage().setY(e.getScreenY() - this.y);
        });

        closeButton.setOnMouseClicked(e -> {
            ViewSwitcher.getInstance().getStage().close();
        });
        minimizeButton.setOnMouseClicked(e -> {
            ViewSwitcher.getInstance().getStage().setIconified(true);
        });
    }

    @FXML
    public void handleRegisterButton() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        String name = loginField.getText();
        String password = passwordField.getText();
        if (name.isBlank() && password.isBlank()) {
            changeErrorLabel("Name and Password cannot be empty.");
        } else if (name.length() < 5) {
            changeErrorLabel("Name must have at least 5 characters.");
        } else if (name.length() > 15) {
            changeErrorLabel("Name cannot have more than 15 characters.");
        } else if (password.length() < 8) {
            changeErrorLabel("Password must have at least 8 characters.");
        } else if (password.length() > 20) {
            changeErrorLabel("Password cannot have more than 20 characters.");
        } else if (!password.matches("(.*[a-z].*)")) {
            changeErrorLabel("Password must have lower case character.");
        } else if (!password.matches("(.*[A-Z].*)")) {
            changeErrorLabel("Password must have upper case character.");
        } else if (!password.matches("(.*[0-9].*)")) {
            changeErrorLabel("Password must have at least one number.");
        } else if (!password.matches("(.*[@,#,$,%,^,&,*,(,)].*)")) {
            changeErrorLabel("Password must have at least one special character.");
        } else {
            String result = ServerConnection.getInstance().register(name, password);
            if (result == null || result.equals("conn:failed")) {
                changeErrorLabel("Register failed. Please try again.");
            } else if (result.equals("conn:taken")) {
                changeErrorLabel("Name is taken. Try another one.");
            } else {
                final Stage alert = new Stage();

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(View.ADD_USER_ALERT.getFileName()));
                Parent root;
                try {
                    root = fxmlLoader.load();

                    alert.setScene(new Scene(root));
                    alert.getScene().setFill(Color.TRANSPARENT);
                    alert.initModality(Modality.APPLICATION_MODAL);
                    alert.initOwner(this.root.getScene().getWindow());
                    alert.initStyle(StageStyle.TRANSPARENT);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }

                AddUserAlertController controller = fxmlLoader.getController();
                controller.setMainLabel(name + " has been registered successfully.");

                alert.showAndWait();
                ViewSwitcher.getInstance().switchView(View.LOGIN, true);
            }
        }
    }

    @FXML
    public void handleCancelButton() {
        ViewSwitcher.getInstance().switchView(View.LOGIN, true);
    }

    public void changeErrorLabel(String message) {
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
        errorLabel.setText(message);
    }
}
