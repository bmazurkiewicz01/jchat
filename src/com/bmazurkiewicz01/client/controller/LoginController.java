package com.bmazurkiewicz01.client.controller;

import com.bmazurkiewicz01.client.model.ServerConnection;
import com.bmazurkiewicz01.client.view.View;
import com.bmazurkiewicz01.client.view.ViewSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;


public class LoginController {
    @FXML
    public AnchorPane root;
    @FXML
    public ImageView closeButton;
    @FXML
    public ImageView minimizeButton;

    @FXML
    public TextField loginField;
    @FXML
    public TextField passwordField;

    @FXML
    public Button loginButton;
    @FXML
    public Button registerButton;
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

        closeButton.setOnMouseClicked(e -> ViewSwitcher.getInstance().getStage().close());
        minimizeButton.setOnMouseClicked(e -> ViewSwitcher.getInstance().getStage().setIconified(true));
    }

    @FXML
    public void handleLoginButton() {
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
            String result = ServerConnection.getInstance().connect(name, password);
            if (result == null) {
                changeErrorLabel("Connection refused. Try again later.");
            }
            else if (result.equals("conn:invalid")) {
                changeErrorLabel("Invalid name or password.");
            }
            else if (result.equals("conn:isalready")) {
                changeErrorLabel(String.format("%s is already on the server", name));
            }
            else {
                ServerConnection.getInstance().setUserName(name);
                ViewSwitcher.getInstance().switchView(View.MAIN);
            }
        }
    }

    @FXML
    public void handleRegisterButton() {
        ViewSwitcher.getInstance().switchView(View.REGISTER);
    }

    public void changeErrorLabel(String message) {
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
        errorLabel.setText(message);
    }
}
