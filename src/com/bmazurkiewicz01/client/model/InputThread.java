package com.bmazurkiewicz01.client.model;

import com.bmazurkiewicz01.client.controller.MainController;
import javafx.collections.FXCollections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

public class InputThread extends Thread {
    private final ObjectInputStream input;
    private final MainController mainController;

    public InputThread(ObjectInputStream input, MainController mainController) {
        this.input = input;
        this.mainController = mainController;
    }

    @Override
    public void run() {
        while (ServerConnection.getInstance().isConnected()) {
            try {
                Object message = input.readObject();
                if (message == null) break;
                else if (message instanceof String) {
                    mainController.updateTextArea(message + "\n");
                } else if (message instanceof List) {
                    mainController.updateListView(FXCollections.observableArrayList((List<String>) message));
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e.getMessage());
                mainController.handleError("Connection error. Please logout or restart the application.\n");
                break;
            }
        }
    }
}
