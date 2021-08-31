package com.bmazurkiewicz01.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public final class ChatServer {
    private static List<ClientThread> clients;
    private static volatile ChatServer instance;

    private static final int PORT = 5555;

    private ChatServer() {
        if (instance != null) throw new IllegalStateException("Can't create new instance.");
        clients = new ArrayList<>();
    }

    public static ChatServer getInstance() {
        if (instance == null) {
            synchronized (ChatServer.class) {
                if (instance == null) instance = new ChatServer();
            }
        }
        return instance;
    }

    public static void main(String[] args) {
        try {
            new CommandThread().start();
            new ClientManagerThread(PORT).start();

        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    public void sendMessage(String message, ClientThread excludeClient) {
        if (message == null || message.isBlank()) {
            return;
        }
        System.out.println(excludeClient.getClientName() + ": " + message);

        for (ClientThread client : clients) {
            if (client == excludeClient) {
                client.getOutput().println("Me: " + message);
            } else {
                client.getOutput().println(excludeClient.getClientName() + ": " + message);
            }
        }
    }

    public void sendMessage(String message) {
        if (message == null || message.isBlank()) return;
        for (ClientThread client : clients) {
            client.getOutput().println(message);
        }
    }

    public void addClient(ClientThread clientThread) {
        if (clientThread != null) clients.add(clientThread);
    }

    public void removeClient(ClientThread clientThread) {
        if (clientThread != null) clients.remove(clientThread);
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
