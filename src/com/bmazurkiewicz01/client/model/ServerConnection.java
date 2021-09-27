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
    private String userName;
    private String currentRoom;

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
        RoomConnectionTask roomConnectionTask = new RoomConnectionTask(output, message);
        currentRoom = name;
        new Thread(roomConnectionTask).start();
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        return inputMessage;
    }

    public void sendMessage(String message) {
        createOutputTask(message);
    }

    public void createInputThread() {
        inputThread = new InputThread(input);
        if (!inputThread.isAlive()) inputThread.start();

    }

    public void setMainControllerInInputThread(MainController mainController) {
        inputThread.setMainController(mainController);
    }

    public void setRoomControllerInInputThread(RoomController roomController) {
        inputThread.setRoomController(roomController);
    }

    public String addRoom(String roomName) {
        if (!roomName.isBlank()) {
            createOutputTask(String.format("addroom:\t%s", roomName));
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
            return inputMessage;
        }
        return null;
    }

    public void leaveRoom() {
        createOutputTask("conn:roomleft");
    }

    public void kickUser(String userName) {
        createOutputTask("kick:\t" + userName);
    }

    public void banUser(String userName) {
        createOutputTask("ban:\t" + userName);
    }

    public boolean validatePassword(String password) {
        createOutputTask("validate:\t" + password);
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        return inputMessage.equals("validate:success");
    }

    public boolean changeUserName(String newName) {
        createOutputTask("changename:\t" + userName + "\t" + newName);
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        return inputMessage.equals("changename:success");
    }

    public boolean changeUserPassword(String newPassword) {
        createOutputTask("changepassword:\t" + userName + "\t" + newPassword);
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        return inputMessage.equals("changepassword:success");
    }

    private void createOutputTask(String message) {
        OutputTask outputTask = new OutputTask(output);
        outputTask.setMessage(message);
        new Thread(outputTask).start();
    }

    public void setInputMessage(String inputMessage) {
        this.inputMessage = inputMessage;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public String getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(String currentRoom) {
        this.currentRoom = currentRoom;
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
