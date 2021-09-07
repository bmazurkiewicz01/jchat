package com.bmazurkiewicz01.client.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class UpdateActiveUsersTask extends Task<ObservableList<String>> {
    private final Socket socket;

    public UpdateActiveUsersTask() throws IOException {
        this.socket = new Socket(ServerConnection.HOST, ServerConnection.PORT);
    }

    @Override
    protected ObservableList<String> call() {
        while (ServerConnection.getInstance().isConnected()) {
            try {
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                output.println("conn:getusers");
                ObjectInputStream inputObject = new ObjectInputStream(socket.getInputStream());
                List<String> usersList = (List<String>) inputObject.readObject();
                for (String name : usersList) {
                    System.out.println(name);
                }
                return FXCollections.observableArrayList(usersList);
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
        return null;
    }
}
