package com.bmazurkiewicz01.client.model;

import com.bmazurkiewicz01.client.controller.MainController;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

public final class ServerConnection {
    private Socket socket;
    private static volatile ServerConnection instance;

    public static final String HOST = "localhost";
    public static final int PORT = 5555;

    private ServerConnection() throws IOException {
        if (instance != null) throw new IllegalStateException("Cannot create new instance.");
    }

    public static ServerConnection getInstance() {
        try {
            if (instance == null) {
                synchronized (ServerConnection.class) {
                    if (instance == null) instance = new ServerConnection();
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return instance;
    }

    public String connect(String name, String password) {
        try {
            socket = new Socket(HOST, PORT);
            ConnectionTask connectionTask = new ConnectionTask(socket, String.format("login:%s\t%s", name, password));
            new Thread(connectionTask).start();
            return connectionTask.get();
        } catch (IOException | ExecutionException | InterruptedException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public String register(String name, String password) {
        try {
            socket = new Socket(HOST, PORT);
            ConnectionTask connectionTask = new ConnectionTask(socket, String.format("register:%s\t%s", name, password));
            new Thread(connectionTask).start();
            return connectionTask.get();
        } catch (IOException | ExecutionException | InterruptedException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void sendMessage(String message) {
        try {
            OutputTask outputTask = new OutputTask(socket.getOutputStream());
            outputTask.setMessage(message);
            new Thread(outputTask).start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateMessage(MainController mainController) {
        try {
            InputThread inputThread = new InputThread(new BufferedReader(new InputStreamReader(socket.getInputStream())), mainController);
            inputThread.start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    public ObservableList<String> getActiveUsers() {
        try {
            UpdateActiveUsersTask activeUsersTask = new UpdateActiveUsersTask();
            new Thread(activeUsersTask).start();
            return activeUsersTask.get();
        } catch (InterruptedException | ExecutionException | IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void close() {
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean isConnected() {
        return !socket.isClosed();
    }
}
