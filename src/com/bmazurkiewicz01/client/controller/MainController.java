package com.bmazurkiewicz01.client.controller;

import com.bmazurkiewicz01.client.Room;
import com.bmazurkiewicz01.client.View;
import com.bmazurkiewicz01.client.ViewSwitcher;
import com.bmazurkiewicz01.client.model.ServerConnection;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


public class MainController {
    @FXML
    public GridPane mainPane;

    @FXML
    public Button logoutButton;
    @FXML
    public Button addRoomButton;

    @FXML
    public TableView<Room> roomTableView;
    @FXML
    public TableColumn<Room, String> nameColumn;
    @FXML
    public TableColumn<Room, String> ownerColumn;
    @FXML
    public TableColumn<Room, Integer> connectedColumn;

    public void initialize() {
        ServerConnection.getInstance().updateRooms();
        ServerConnection.getInstance().setMainControllerInInputThread(this);
    }

    @FXML
    public void handleLogoutButton() {
        ServerConnection.getInstance().setMainControllerInInputThread(null);
        closeConnection();
        ViewSwitcher.getInstance().switchView(View.LOGIN, false);
    }

    @FXML
    public void handleAddRoomButton() {
        Dialog<ButtonType> addRoomDialog = new Dialog<>();
        addRoomDialog.initOwner(mainPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addRoomDialog.fxml"));

        try {
            addRoomDialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        AddRoomDialogController controller = fxmlLoader.getController();
        addRoomDialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        addRoomDialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        addRoomDialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(
                Bindings.createBooleanBinding(() -> controller.getNameField().getText().isBlank(), controller.getNameField().textProperty()));
        addRoomDialog.setTitle("Adding new room");

        Optional<ButtonType> result = addRoomDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            controller.processResults();
        }
    }

    @FXML
    public void handleRowClick(MouseEvent mouseEvent) {
        Room room = roomTableView.getSelectionModel().getSelectedItem();
        if (room != null && mouseEvent.getClickCount() == 2) {
            String result = ServerConnection.getInstance().connectToRoom(room.getName(), room.getOwner());
            System.out.println(result);
            if (result != null && result.equals("conn:roomconnected")) {
                ServerConnection.getInstance().setMainControllerInInputThread(null);
                ViewSwitcher.getInstance().switchView(View.ROOM, false);
            }
            else {
                System.out.println("we fucked up");
            }
        }
    }

    public void closeConnection() {
        ServerConnection.getInstance().close();
    }

    public void setRooms(List<Room> newRooms) {
        newRooms.forEach(System.out::println);
        roomTableView.setItems(FXCollections.observableList(newRooms));
    }

}
