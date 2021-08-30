package com.bmazurkiewicz01.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {
    private static Map<String, ClientThread> clients;
    private static ChatServer chatServer;

    private static final int PORT = 5555;

    private ChatServer() {
        clients = new HashMap<>();
    }

    public static ChatServer getInstance() {
        if (chatServer == null) chatServer = new ChatServer();
        return chatServer;
    }

    public static void main(String[] args) {
        if (chatServer == null) chatServer = new ChatServer();
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            new CommandThread().start();
            while (!serverSocket.isClosed()) {
                new Thread(() -> {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        String name = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
                        if (name.startsWith("init:")) {
                            ClientThread newClient = new ClientThread(clientSocket, name.substring(5));
                            System.out.println(newClient.getClientName() + " connected to server.");
                            getInstance().sendMessage(newClient.getClientName() + " connected to server.");
                            clients.put(newClient.getClientName(), newClient);
                            newClient.start();
                        }
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }).start();
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    public void sendMessage(String message, ClientThread excludeClient) {
        if (message == null || message.isBlank()) {
            return;
        }
        System.out.println(excludeClient.getClientName() + ": " + message);

        for (String name : clients.keySet()) {
            ClientThread client = clients.get(name);
            if (client == excludeClient) {
                client.getOutput().println("Me: " + message);
            } else {
                client.getOutput().println(excludeClient.getClientName() + ": " + message);
            }
        }
    }

    public void sendMessage(String message) {
        if (message == null || message.isBlank()) return;
        for (ClientThread client : clients.values()) {
            client.getOutput().println(message);
        }
    }

    public void removeClient(ClientThread clientThread) {
        if (clientThread != null) {
            clients.remove(clientThread.getClientName());
        }
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
