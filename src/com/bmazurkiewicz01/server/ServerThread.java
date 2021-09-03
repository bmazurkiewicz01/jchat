package com.bmazurkiewicz01.server;

import com.bmazurkiewicz01.server.database.User;
import com.bmazurkiewicz01.server.database.UserDatasource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread {
    private final ServerSocket serverSocket;
    private final UserDatasource userDatasource;

    public ServerThread(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        userDatasource = new UserDatasource();
    }

    @Override
    public void run() {
        try {
            do {
                Socket clientSocket = serverSocket.accept();
                String initMessage = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
                if (initMessage.startsWith("login:")) {
                    String name = initMessage.replace("login:", "").split("\t")[0];
                    String password = initMessage.replace("login:", "").split("\t")[1];
                    System.out.println(name + ", " + password);
                    if (userDatasource.searchUser(new User(name, password))) {
                        ClientThread newClient = new ClientThread(clientSocket, name);
                        System.out.println(newClient.getClientName() + " connected to server.");
                        JchatServer.getInstance().sendMessage(newClient.getClientName() + " connected to server.");
                        JchatServer.getInstance().addClient(newClient);
                        newClient.start();
                    } else {
                        clientSocket.close();
                    }
                }

            } while (!serverSocket.isClosed());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
