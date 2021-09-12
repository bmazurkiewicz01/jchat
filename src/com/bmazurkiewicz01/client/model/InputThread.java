package com.bmazurkiewicz01.client.model;

import com.bmazurkiewicz01.client.Room;
import com.bmazurkiewicz01.client.controller.RoomController;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InputThread extends Thread {
    private final ObjectInputStream input;
    private final RoomController roomController;

    public InputThread(ObjectInputStream input, RoomController roomController) {
        this.input = input;
        this.roomController = roomController;
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
                else if (message instanceof String) {
                    roomController.updateTextArea(message + "\n");
                } else if (message instanceof List) {
                    if (((List<?>) message).contains("\t")) {
                        List<Room> newRooms = new ArrayList<>();
                        for (String room : (ArrayList<String>) message) {
                            String[] data = room.split("\t");
                            Arrays.stream(data).forEach(System.out::println);
                            newRooms.add(new Room(data[0], data[1], Integer.valueOf(data[2])));
                        }
                        //Platform.runLater(() -> MainController.setRooms(newRooms));
                    }
                    else {
                        Platform.runLater(() -> roomController.updateListView((List<String>) message));
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e.getMessage());
                roomController.handleError("Connection error. Please logout or restart the application.\n", true);
                break;
            }
        }
    }
}
