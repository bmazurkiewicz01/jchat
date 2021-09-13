package com.bmazurkiewicz01.client.model;

import com.bmazurkiewicz01.client.controller.MainController;
import com.bmazurkiewicz01.client.controller.RoomController;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

public final class ServerConnection {
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private InputThread inputThread;
    private static volatile ServerConnection instance;
    private String inputMessage;

    private static final String HOST = "localhost";
    private static final int PORT = 5555;

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

    public String connectToRoom(String name, String owner) {
        String message = String.format("connectroom:\t%s\t%s", name, owner);
        RoomConnectionTask roomConnectionTask = new RoomConnectionTask(output, input, message);
        new Thread(roomConnectionTask).start();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        return inputMessage;
    }

    public void sendMessage(String message) {
        OutputTask outputTask = new OutputTask(output);
        outputTask.setMessage(message);
        new Thread(outputTask).start();
    }

    public void updateMessage() {
        if (inputThread == null) inputThread = new InputThread(input);
        if (!inputThread.isAlive()) inputThread.start();

    }

    public void setMainControllerInInputThread(MainController mainController) {
        inputThread.setMainController(mainController);
    }

    public void setRoomControllerInInputThread(RoomController roomController) {
        inputThread.setRoomController(roomController);
    }

    public void addRoom(String roomName) {
        if (!roomName.isBlank()) {
            OutputTask outputTask = new OutputTask(output);
            outputTask.setMessage(String.format("addroom:\t%s", roomName));
            new Thread(outputTask).start();
        }
    }

    public void leaveRoom() {
        OutputTask outputTask = new OutputTask(output);
        outputTask.setMessage("conn:roomleft");
        new Thread(outputTask).start();
    }

    public void updateRooms() {
        inputThread = new InputThread(input);
        if (!inputThread.isAlive()) inputThread.start();
    }

    public void setInputMessage(String inputMessage) {
        this.inputMessage = inputMessage;
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
