package com.bmazurkiewicz01.client.model;

import com.bmazurkiewicz01.client.controller.MainController;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

public final class ServerConnection {
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
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
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            ConnectionTask connectionTask = new ConnectionTask(String.format("login:%s\t%s", name, password), output, input);
            new Thread(connectionTask).start();
            return connectionTask.get();
        } catch (IOException | ExecutionException | InterruptedException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public String register(String name, String password) {
        try {
            Socket socket = new Socket(HOST, PORT);
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            ConnectionTask connectionTask = new ConnectionTask(String.format("register:%s\t%s", name, password), output, input);
            new Thread(connectionTask).start();
            return connectionTask.get();
        } catch (IOException | ExecutionException | InterruptedException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void sendMessage(String message) {
        OutputTask outputTask = new OutputTask(output);
        outputTask.setMessage(message);
        new Thread(outputTask).start();
    }

    public void updateMessage(MainController mainController) {
        InputThread inputThread = new InputThread(input, mainController);
        inputThread.start();

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
