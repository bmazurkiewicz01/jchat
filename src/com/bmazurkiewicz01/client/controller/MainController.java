package com.bmazurkiewicz01.client.controller;

import com.bmazurkiewicz01.client.model.ServerConnection;
import com.bmazurkiewicz01.client.view.Room;
import com.bmazurkiewicz01.client.view.View;
import com.bmazurkiewicz01.client.view.ViewSwitcher;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;


public class MainController {
    @FXML
    public AnchorPane root;
    @FXML
    public ImageView closeButton;
    @FXML
    public ImageView minimizeButton;


    @FXML
    public Button hamburgerButton;
    @FXML
    public Button logoutButton;
    @FXML
    public Button addRoomButton;
    @FXML
    public Button userPropertiesButton;

    @FXML
    public TableView<Room> roomTableView;
    @FXML
    public TableColumn<Room, String> nameColumn;
    @FXML
    public TableColumn<Room, String> ownerColumn;
    @FXML
    public TableColumn<Room, Integer> connectedColumn;

    @FXML
    public Label helloLabel;
    @FXML
    public Label errorLabel;
    @FXML
    public AnchorPane leftPane;

    private double x, y;

    public void initialize() {
        setUpLeftPaneAnimation();
        ServerConnection.getInstance().createInputThread();
        ServerConnection.getInstance().setMainControllerInInputThread(this);
        ViewSwitcher.getInstance().setMainController(this);

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
    public void handleLogoutButton() {
        ServerConnection.getInstance().setMainControllerInInputThread(null);
        closeConnection();
        ViewSwitcher.getInstance().switchView(View.LOGIN);
    }

    @FXML
    public void handleAddRoomButton() {
        final Stage addRoomDialog = new Stage();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(View.ADD_ROOM_DIALOG.getFileName()));
        Parent root;
        try {
            root = fxmlLoader.load();

            addRoomDialog.setScene(new Scene(root));
            addRoomDialog.getScene().setFill(Color.TRANSPARENT);
            addRoomDialog.initModality(Modality.APPLICATION_MODAL);
            addRoomDialog.initOwner(this.root.getScene().getWindow());
            addRoomDialog.initStyle(StageStyle.TRANSPARENT);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        AddRoomDialogController controller = fxmlLoader.getController();
        controller.setNameField(String.format("%s's Room", ServerConnection.getInstance().getUserName()));

        addRoomDialog.showAndWait();
    }

    @FXML
    public void handleRowClick(MouseEvent mouseEvent) {
        Room room = roomTableView.getSelectionModel().getSelectedItem();
        if (room != null && mouseEvent.getClickCount() == 2) {
            String result = ServerConnection.getInstance().connectToRoom(room.getName(), room.getOwner());
            if (result != null) {
                errorLabel.setVisible(false);
                errorLabel.setManaged(false);
                switch (result) {
                    case "conn:banned":
                        handleError("You are banned from " + room.getName() + ".");
                        ServerConnection.getInstance().setCurrentRoom(null);
                        break;
                    case "conn:guestperm":
                        ViewSwitcher.getInstance().joinRoomAndSetLabels(room.getName(), room.getOwner(), false);
                        break;
                    case "conn:ownerperm":
                        ViewSwitcher.getInstance().joinRoomAndSetLabels(room.getName(), "You", true);
                        break;
                }
            } else {
                handleError("Room connection error. Please try again.");
            }
        }
    }

    @FXML
    public void handleUserPropertiesButton(ActionEvent actionEvent) {
        final Stage userPropertiesDialog = new Stage();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(View.USER_PROPERTIES_VALIDATION.getFileName()));
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
    }

    public void handleError(String message) {
        if (message != null) {
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
            errorLabel.setText(message);
        }
    }

    public void setHelloLabel(String text) {
        if (text != null) helloLabel.setText(text);
    }

    public void setRooms(List<Room> newRooms) {
        roomTableView.setItems(FXCollections.observableList(newRooms));
    }

    private void setUpLeftPaneAnimation() {
        TranslateTransition openPane = new TranslateTransition(new Duration(550), leftPane);
        TranslateTransition closePane = new TranslateTransition(new Duration(550), leftPane);
        openPane.setToX(0);

        hamburgerButton.setOnAction( e -> {
            if (leftPane.getTranslateX() != 0) {
                openPane.play();
            } else {
                closePane.setToX(-(leftPane.getWidth()));
                closePane.play();
            }
        });
    }

    public void closeConnection() {
        ServerConnection.getInstance().close();
    }
}
