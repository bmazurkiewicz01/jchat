package com.bmazurkiewicz01.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class JchatServer {
    private static List<ClientThread> clients;
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
            new CommandThread().start();
            new ServerThread(PORT).start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendMessage(String message, ClientThread excludeClient) throws IOException {
        if (message == null || message.isBlank()) {
            return;
        }
        System.out.println(excludeClient.getClientName() + ": " + message);

        for (ClientThread client : clients) {
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
            sendConnectedUsers();
        }
    }

    public boolean searchClient(String clientName) {
        if (clients == null || clients.isEmpty()) return false;

        for(ClientThread client : clients) {
            if (client.getClientName().equals(clientName)) return true;
        }
        return false;
    }

    public void sendConnectedUsers() throws IOException {
        List<String> users = getConnectedUsers();
        for (ClientThread client : clients) {
            client.getOutput().writeObject(users);
        }
    }

    public List<String> getConnectedUsers() {
        List<String> users = new ArrayList<>();
        for (ClientThread clientThread : clients) {
            users.add(clientThread.getClientName());
        }
        return users;
    }

    public void processCommand(String command) throws IOException {
        switch (command) {
            case "exit":
                System.exit(0);
                break;
            case "sayHi":
                sendMessage("Server: Hello Everyone!");
                break;
        }
    }
}
