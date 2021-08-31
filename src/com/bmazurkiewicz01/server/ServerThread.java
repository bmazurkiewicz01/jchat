package com.bmazurkiewicz01.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread {
    private final ServerSocket serverSocket;

    public ServerThread(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        try {
            do {
                Socket clientSocket = serverSocket.accept();
                String name = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
                ClientThread newClient = new ClientThread(clientSocket, name);
                System.out.println(newClient.getClientName() + " connected to server.");
                JchatServer.getInstance().sendMessage(newClient.getClientName() + " connected to server.");
                JchatServer.getInstance().addClient(newClient);
                newClient.start();
            } while (!serverSocket.isClosed());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
