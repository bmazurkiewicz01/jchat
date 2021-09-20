package com.bmazurkiewicz01.client.model;

import com.bmazurkiewicz01.client.view.Room;
import com.bmazurkiewicz01.client.view.View;
import com.bmazurkiewicz01.client.view.ViewSwitcher;
import com.bmazurkiewicz01.client.controller.MainController;
import com.bmazurkiewicz01.client.controller.RoomController;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InputThread extends Thread {
    private final ObjectInputStream input;
    private RoomController roomController;
    private MainController mainController;

    public InputThread(ObjectInputStream input) {
        this.input = input;
    }

    @Override
    public void run() {
        while (ServerConnection.getInstance().isConnected()) {
            try {
                Object message;
                synchronized (input) {
                    message = input.readObject();
                }
                if (message == null) break;

                if (roomController != null) {
                    if (message instanceof String) {
                        if (message.equals("conn:\tkicked")) {
                            ServerConnection.getInstance().leaveRoom();
                            ViewSwitcher.getInstance().getMainController().handleError("You have been kicked from "
                                    + ServerConnection.getInstance().getCurrentRoom() + ".");
                            ServerConnection.getInstance().setRoomControllerInInputThread(null);
                            ServerConnection.getInstance().setMainControllerInInputThread(ViewSwitcher.getInstance().getMainController());

                            ViewSwitcher.getInstance().switchView(View.MAIN, true);
                        } else {
                            roomController.updateTextArea(message + "\n");
                        }
                    } else if (message instanceof List) {
                        List<String> messages = (List<String>) message;
                        if (messages.get(0).contains("\t")) continue;
                        Platform.runLater(() -> roomController.updateListView(messages));
                    }
                } else if (mainController != null) {
                    if (message instanceof String) {
                        ServerConnection.getInstance().setInputMessage((String) message);
                    }
                    else if (message instanceof List) {
                        List<String> rooms = (List<String>) message;
                        if (rooms.isEmpty()) Platform.runLater(() -> mainController.setRooms(new ArrayList<>()));
                        if (rooms.get(0).contains("\t")) {
                            List<Room> newRooms = new ArrayList<>();
                            for (String room : rooms) {
                                String[] data = room.split("\t");
                                Arrays.stream(data).forEach(System.out::println);
                                newRooms.add(new Room(data[0], data[1], Integer.parseInt(data[2])));
                            }
                            Platform.runLater(() -> mainController.setRooms(newRooms));
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e.getMessage());
                if (roomController != null) roomController.handleError("Connection error. Please logout or restart the application.\n", true);
                break;
            }
        }
    }

    public void setRoomController(RoomController roomController) {
        this.roomController = roomController;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
