package com.bmazurkiewicz01.server;

import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {
    private final Socket socket;
    private final String clientName;
    private final ObjectOutputStream output;
    private final ObjectInputStream input;
    private ServerRoom currentRoom;

    public ClientThread(Socket socket, String clientName, ObjectOutputStream output, ObjectInputStream input) throws IOException {
        this.socket = socket;
        this.clientName = clientName;
        this.output = output;
        this.input = input;
    }

    @Override
    public void run() {
        try {
            String message;
            do {
                message = (String) input.readObject();
                if (message == null) break;

                if (currentRoom == null) {
                    String[] data = message.split("\t");
                    if (message.startsWith("addroom:\t")) {
                        JchatServer.getInstance().addRoom(new ServerRoom(data[1], clientName));
                        JchatServer.getInstance().sendRooms();
                    } else if (message.startsWith("connectroom:\t")) {
                        currentRoom = JchatServer.getInstance().getSingleRoom(data[1], data[2]);
                        if (currentRoom != null) {
                            boolean isOwner = JchatServer.getInstance().addClientToRoom(this, currentRoom);
                            String result = isOwner ? "conn:ownerperm" : "conn:guestperm";
                            output.writeObject(result);
                            JchatServer.getInstance().sendRooms();
                        }
                        else output.writeObject("conn:roomfailed");
                        output.flush();
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        JchatServer.getInstance().sendConnectedUsers(currentRoom);
                    }
                } else {
                    if (message.startsWith("conn:roomleft")) {
                        JchatServer.getInstance().removeClientFromRoom(this, currentRoom);
                        JchatServer.getInstance().sendConnectedUsers(currentRoom);
                        currentRoom = null;
                        JchatServer.getInstance().sendRooms();
                    }
                    else if (message.startsWith("kick:\t")) {
                        if (currentRoom.getOwner().equals(clientName)) {
                            String userName = message.split("\t")[1];
                            System.out.println(userName + " has to be kicked");
                            JchatServer.getInstance().kickClientFromRoom(userName, currentRoom);
                        }
                    }
                    else if (!message.isBlank()) {
                        JchatServer.getInstance().sendMessage(message, this, currentRoom);
                    }
                }
            } while (!socket.isClosed());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("ClientThread: " + e.getMessage());
        } finally {
            try {
                JchatServer.getInstance().removeClientFromRoom(this, currentRoom);
                JchatServer.getInstance().removeClient(this);
                System.out.println(clientName + " left.");
                input.close();
                output.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getClientName() {
        return clientName;
    }

    public ObjectOutputStream getOutput() {
        return output;
    }

    public Socket getSocket() {
        return socket;
    }

    public ServerRoom getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(ServerRoom currentRoom) {
        this.currentRoom = currentRoom;
    }
}
