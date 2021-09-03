package com.bmazurkiewicz01.server;

import com.bmazurkiewicz01.server.database.User;
import com.bmazurkiewicz01.server.database.UserDatasource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.PreparedStatement;

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
                PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);

                String initMessage = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
                String[] data = initMessage.replaceFirst("login:", "").replaceFirst("register:", "").split("\t");
                String name = data[0];
                String password = data[1];
                User user = new User(name, password);

                if (initMessage.startsWith("login:")) {
                    if (userDatasource.searchUser(user) && !JchatServer.getInstance().searchClient(user.getName())) {
                        ClientThread newClient = new ClientThread(clientSocket, name);
                        System.out.println(newClient.getClientName() + " connected to server.");
                        output.println("conn:accepted");
                        JchatServer.getInstance().sendMessage(newClient.getClientName() + " connected to server.");
                        JchatServer.getInstance().addClient(newClient);
                        newClient.start();
                    } else {
                        output.println("conn:rejected");
                        clientSocket.close();
                    }
                } else if (initMessage.startsWith("register:")) {
                    if (!userDatasource.searchUser(user)) {
                        if (userDatasource.insertUser(user)) output.println("conn:success");
                        else output.println("conn:failed");
                    } else {
                        output.println("conn:failed");
                    }
                    clientSocket.close();
                } else {
                    output.println("conn:error");
                    clientSocket.close();
                }

            } while (!serverSocket.isClosed());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
