package com.bmazurkiewicz01.client.controller;

import com.bmazurkiewicz01.client.view.View;
import com.bmazurkiewicz01.client.view.ViewSwitcher;
import com.bmazurkiewicz01.client.model.ServerConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

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

    private double x,y;

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
        } else if (name.isBlank()) {
            changeErrorLabel("Name cannot be empty.");
        } else if (password.isBlank()) {
            changeErrorLabel("Password cannot be empty");
        } else {
            String result = ServerConnection.getInstance().register(name, password);
            if (result == null || result.equals("conn:failed")) {
                changeErrorLabel("Register failed. Please try again.");
            } else if (result.equals("conn:taken")) {
                changeErrorLabel("Name is taken. Try another one.");
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, name + " has been registered successfully.");
                alert.setTitle("Register success");
                alert.showAndWait();
                ViewSwitcher.getInstance().switchView(View.LOGIN);
            }
        }
    }

    @FXML
    public void handleCancelButton() {
        ViewSwitcher.getInstance().switchView(View.LOGIN);
    }

    public void changeErrorLabel(String message) {
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
        errorLabel.setText(message);
    }
}
