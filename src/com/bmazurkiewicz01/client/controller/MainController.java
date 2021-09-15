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
        ViewSwitcher.getInstance().setMainController(this);
    }

    @FXML
    public void handleLogoutButton() {
        ServerConnection.getInstance().setMainControllerInInputThread(null);
        closeConnection();
        ViewSwitcher.getInstance().switchView(View.LOGIN);
    }

    @FXML
    public void handleAddRoomButton() {
        Dialog<ButtonType> addRoomDialog = new Dialog<>();
        addRoomDialog.initOwner(mainPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(View.ADD_ROOM_DIALOG.getFileName()));

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
        controller.setNameField(String.format("%s's Room", ServerConnection.getInstance().getUserName()));

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
            if (result != null) {
                if (result.equals("conn:guestperm")) {
                    ViewSwitcher.getInstance().joinRoomAndSetLabels(room.getName(), room.getOwner(), false);
                } else if (result.equals("conn:ownerperm")) {
                    ViewSwitcher.getInstance().joinRoomAndSetLabels(room.getName(), "You", true);
                }
            } else {
                // TODO: 14/09/2021  
                System.out.println("we fucked up");
            }
        }
    }

    public void setRooms(List<Room> newRooms) {
        newRooms.forEach(System.out::println);
        roomTableView.setItems(FXCollections.observableList(newRooms));
    }

    public void closeConnection() {
        ServerConnection.getInstance().close();
    }

}
