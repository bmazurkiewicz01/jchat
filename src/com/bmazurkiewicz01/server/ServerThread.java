package com.bmazurkiewicz01.server;

import com.bmazurkiewicz01.server.database.User;
import com.bmazurkiewicz01.server.database.UserDatasource;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
                ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());

                String initMessage = (String) input.readObject();
                String[] data = initMessage.replaceFirst("login:", "").replaceFirst("register:", "").split("\t");
                System.out.println(initMessage);
                String name = data[0];
                String password = data[1];
                User user = new User(name, password);

                if (initMessage.startsWith("login:")) {
                    if (!userDatasource.searchUser(user)) {
                        output.writeObject("conn:invalid");
                        output.flush();
                        clientSocket.close();
                    } else if (JchatServer.getInstance().searchClient(user.getName())) {
                        output.writeObject("conn:isalready");
                        output.flush();
                        clientSocket.close();
                    } else {
                        ClientThread newClient = new ClientThread(clientSocket, name, output, input);
                        System.out.println(newClient.getClientName() + " connected to server.");
                        output.writeObject("conn:accepted");
                        output.flush();
                        JchatServer.getInstance().sendMessage(newClient.getClientName() + " connected to server.");
                        JchatServer.getInstance().addClient(newClient);
                        newClient.start();
                        JchatServer.getInstance().sendConnectedUsers();
                    }
                }
                else if (initMessage.startsWith("register:")) {
                    if (userDatasource.searchUserByName(name)) {
                        output.writeObject("conn:taken");
                    } else {
                        if (userDatasource.insertUser(user)) output.writeObject("conn:success");
                        else output.writeObject("conn:failed");
                    }
                    output.flush();
                    clientSocket.close();
                }
            } while (!serverSocket.isClosed());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}
