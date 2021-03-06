package com.bmazurkiewicz01.server;

import com.bmazurkiewicz01.server.database.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientThread extends Thread {
    private final Socket socket;
    private String clientName;
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
                        boolean isRoomAdded = JchatServer.getInstance().addRoom(new ServerRoom(data[1], clientName));
                        if (isRoomAdded) {
                            JchatServer.getInstance().sendRooms();
                            output.writeObject("conn:roomadded");
                        } else {
                            output.writeObject("conn:roomerror");
                        }
                        output.flush();
                    } else if (message.startsWith("connectroom:\t")) {
                        ServerRoom room = JchatServer.getInstance().getSingleRoom(data[1], data[2]);

                        if (room == null) {
                            output.writeObject("conn:roomfailed");
                        }
                        else if (room.isClientBanned(this)) {
                            output.writeObject("conn:banned");
                        }
                        else {
                            currentRoom = room;
                            boolean isOwner = JchatServer.getInstance().addClientToRoom(this, currentRoom);
                            String result = isOwner ? "conn:ownerperm" : "conn:guestperm";
                            output.writeObject(result);
                            JchatServer.getInstance().sendRooms();
                            try {
                                Thread.sleep(100);
                                JchatServer.getInstance().sendConnectedUsers(currentRoom);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        output.flush();
                    } else if (message.startsWith("validate:\t")) {
                        String password = data[1];
                        if (JchatServer.getInstance().validateUser(new User(clientName, password))) {
                            output.writeObject("validate:success");
                        } else {
                            output.writeObject("validate:failed");
                        }
                        output.flush();
                    } else if (message.startsWith("changename:\t")) {
                        String name = data[1];
                        String newName = data[2];

                        if (JchatServer.getInstance().changeUserName(name, newName)) {
                            output.writeObject("changename:success");
                            clientName = newName;
                            JchatServer.getInstance().changeOwnersInRooms(name, newName);
                        } else {
                            output.writeObject("changename:failed");
                        }
                        output.flush();
                    } else if (message.startsWith("changepassword:\t")) {
                        String name = data[1];
                        String newPassword = data[2];

                        if (JchatServer.getInstance().changeUserPassword(name, newPassword)) {
                            output.writeObject("changepassword:success");
                        } else {
                            output.writeObject("changepassword:failed");
                        }
                        output.flush();
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
                            System.out.println(userName + " has been kicked from " + currentRoom + ".");
                            JchatServer.getInstance().kickClientFromRoom(userName, currentRoom);
                        }
                    }
                    else if (message.startsWith("ban:\t")) {
                        if (currentRoom.getOwner().equals(clientName)) {
                            String userName = message.split("\t")[1];
                            System.out.println(userName + " has been banned from " + currentRoom + ".");
                            JchatServer.getInstance().banClientFromRoom(userName, currentRoom);
                        }
                    } else if (message.startsWith("deleteroom:\t")) {
                        String userName = message.split("\t")[1];
                        String roomName = message.split("\t")[2];

                        if (userName.equals(this.clientName) && JchatServer.getInstance().isOwner(this, currentRoom)) {
                            if (currentRoom.getName().equals(roomName)) {
                                JchatServer.getInstance().kickAllFromRoom(currentRoom, this);
                                JchatServer.getInstance().removeRoom(currentRoom);
                                currentRoom = null;
                                JchatServer.getInstance().sendRooms();
                            }
                        }

                    } else if (!message.isBlank()) {
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
