package com.bmazurkiewicz01.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class JchatServer {
    private static List<ClientThread> clients;
    private static List<ServerRoom> rooms;
    private static volatile JchatServer instance;

    private static final int PORT = 5555;

    private JchatServer() {
        if (instance != null) throw new IllegalStateException("Can't create new instance.");
        clients = new ArrayList<>();
    }

    public static JchatServer getInstance() {
        if (instance == null) {
            synchronized (JchatServer.class) {
                if (instance == null) instance = new JchatServer();
            }
        }
        return instance;
    }

    public static void main(String[] args) {
        try {
            rooms = new ArrayList<>();
            rooms.add(new ServerRoom("All Chat", "Admin"));
            new CommandThread().start();
            new ServerThread(PORT).start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendMessage(String message, ClientThread excludeClient, ServerRoom room) throws IOException {
        if (message == null || message.isBlank()) {
            return;
        }
        System.out.println("[" + room.getName() + "]" + excludeClient.getClientName() + ": " + message);

        List<ClientThread> roomClients = room.getClientList();

        for (ClientThread client : roomClients) {
            if (client == excludeClient) {
                client.getOutput().writeObject("Me: " + message);
            } else {
                client.getOutput().writeObject(excludeClient.getClientName() + ": " + message);
            }
            client.getOutput().flush();
        }
    }

    public void sendMessage(String message) throws IOException {
        if (message == null || message.isBlank()) return;
        for (ClientThread client : clients) {
            client.getOutput().writeObject(message);
            client.getOutput().flush();
        }
    }

    public void addClient(ClientThread clientThread) {
        if (clientThread != null) clients.add(clientThread);
    }

    public void removeClient(ClientThread clientThread) throws IOException {
        if (clientThread != null) {
            clients.remove(clientThread);
        }
    }

    public boolean searchClient(String clientName) {
        if (clients == null || clients.isEmpty()) return false;

        for(ClientThread client : clients) {
            if (client.getClientName().equals(clientName)) return true;
        }
        return false;
    }

    public void kickClientFromRoom(String clientName, ServerRoom room) throws IOException {
        for (ClientThread client : room.getClientList()) {
            if (client.getClientName().equals(clientName)) {
                client.getOutput().writeObject("conn:\tkicked");
                client.getOutput().flush();
                break;
            }
        }
    }

    private boolean kickClient(String clientName) {
        if (clients == null || clients.isEmpty()) return false;

        for(ClientThread client : clients) {
            if (client.getClientName().equals(clientName)) {
                try {
                    removeClientFromRoom(client, client.getCurrentRoom());
                    removeClient(client);
                    client.getSocket().close();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    public void sendConnectedUsers(ServerRoom room) throws IOException {
        List<String> users = getConnectedUsers(room);
        List<ClientThread> roomClients = room.getClientList();
        for (ClientThread client : roomClients) {
            System.out.print(client.getClientName() + ", ");
            client.getOutput().writeObject(users);
        }
        System.out.println();
    }

    private List<String> getConnectedUsers(ServerRoom room) {
        List<String> users = new ArrayList<>();
        List<ClientThread> roomClients = room.getClientList();
        for (ClientThread client : roomClients) {
            users.add(client.getClientName());
        }
        return users;
    }

    private List<String> getAllConnectedUsers() {
        List<String> users = new ArrayList<>();
        for (ClientThread client : clients) {
            users.add(client.getClientName());
        }
        return users;
    }

    public void addRoom(ServerRoom room) {
        if (room != null) rooms.add(room);
    }

    public void removeRoom(ServerRoom room) {
        if (room != null) {
            rooms.remove(room);
        }
    }

    public ServerRoom getSingleRoom(String name, String owner) {
        for (ServerRoom room : rooms) {
            if (room.getName().equals(name) && room.getOwner().equals(owner)) return room;
        }
        return null;
    }

    public boolean addClientToRoom(ClientThread client, ServerRoom room) {
        if (client != null && room != null) {
            for (ServerRoom serverRoom : rooms) {
                if (serverRoom.equals(room)) {
                    serverRoom.addClient(client);
                    return serverRoom.getOwner().equals(client.getClientName());
                }
            }
        }
        return false;
    }

    public void removeClientFromRoom(ClientThread client, ServerRoom room) {
        if (client != null && room != null) {
            for (ServerRoom serverRoom : rooms) {
                if (serverRoom.equals(room)) {
                    serverRoom.removeClient(client);
                }
            }
        }
    }

    public void sendRooms() throws IOException {
        List<String> rooms = getRoomsToString();
        for (ClientThread client : clients) {
            if (client.getCurrentRoom() == null) client.getOutput().writeObject(rooms);
        }
    }

    private List<String> getRoomsToString() {
        List<String> roomsToString = new ArrayList<>();
        for (ServerRoom room : rooms) {
            roomsToString.add(room.toString());
        }
        return roomsToString;
    }

    public void processCommand(String command) throws IOException {
       if (command.equals("exit")) {
           System.exit(0);
       } else if (command.startsWith("say ")) {
           sendMessage("Server: " + command.replaceFirst("say ", ""));
       } else if (command.startsWith("kick ")) {
           String clientName = command.replaceFirst("kick ", "");
           if (kickClient(clientName)) System.out.println(clientName + " successfully kicked out of the server");
           else System.out.println("Kicking out " + clientName + " failed.");
       } else if (command.equals("printUsers")) {
           getAllConnectedUsers().forEach(System.out::println);
       }
    }
}
