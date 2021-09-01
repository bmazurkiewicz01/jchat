package com.bmazurkiewicz01.client.model;

import com.bmazurkiewicz01.client.controller.MainController;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

    public boolean connect(String clientName) {
        Task<Boolean> connectTask = new Task<>() {
            @Override
            protected Boolean call() throws IOException {
                socket = new Socket(HOST, PORT);
                new PrintWriter(socket.getOutputStream(), true).println(clientName);
                return true;
            }
        };
        new Thread(connectTask).start();
        try {
            return connectTask.get();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println(e.getMessage());
            return false;
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

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    public boolean isClosed() {
        return socket.isClosed();
    }
}
