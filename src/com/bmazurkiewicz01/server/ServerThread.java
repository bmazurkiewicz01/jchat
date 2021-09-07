package com.bmazurkiewicz01.server;

import com.bmazurkiewicz01.server.database.User;
import com.bmazurkiewicz01.server.database.UserDatasource;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

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

                if (initMessage.startsWith("conn:getusers")) {
                    ObjectOutputStream outputObject = new ObjectOutputStream(clientSocket.getOutputStream());
                    List<String> users = JchatServer.getInstance().getConnectedUsers();
                    outputObject.writeObject(users);
                    outputObject.close();
                    continue;
                }

                PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
                String[] data = initMessage.replaceFirst("login:", "").replaceFirst("register:", "").split("\t");
                String name = data[0];
                String password = data[1];
                User user = new User(name, password);

                if (initMessage.startsWith("login:")) {
                    if (!userDatasource.searchUser(user)) {
                        output.println("conn:invalid");
                        clientSocket.close();
                    } else if (JchatServer.getInstance().searchClient(user.getName())) {
                        output.println("conn:isalready");
                        clientSocket.close();
                    } else {
                        ClientThread newClient = new ClientThread(clientSocket, name);
                        System.out.println(newClient.getClientName() + " connected to server.");
                        output.println("conn:accepted");
                        JchatServer.getInstance().sendMessage(newClient.getClientName() + " connected to server.");
                        JchatServer.getInstance().addClient(newClient);
                        newClient.start();
                    }
                }
                else if (initMessage.startsWith("register:")) {
                    if (userDatasource.searchUserByName(name)) {
                        output.println("conn:taken");
                    } else {
                        if (userDatasource.insertUser(user)) output.println("conn:success");
                        else output.println("conn:failed");
                    }
                    clientSocket.close();
                }
            } while (!serverSocket.isClosed());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
