package com.bmazurkiewicz01.server;

import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {
    private final Socket socket;
    private final String clientName;
    private final ObjectOutputStream output;
    private final ObjectInputStream input;

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
                System.out.println(message);

                if (message.startsWith("addroom:\t")) {
                    String[] data = message.split("\t");
                    JchatServer.getInstance().addRoom(new ServerRoom(data[1], data[2]));
                    JchatServer.getInstance().sendRooms();
                    continue;
                }

                if (!message.isBlank()) {
                    JchatServer.getInstance().sendMessage(message, this);
                }
            } while (!socket.isClosed());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("ClientThread: " + e.getMessage());
        } finally {
            try {
                JchatServer.getInstance().removeClient(this);
                JchatServer.getInstance().sendMessage(clientName + " left.");
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
}
