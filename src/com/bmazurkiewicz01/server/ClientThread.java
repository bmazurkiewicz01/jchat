package com.bmazurkiewicz01.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {
    private final Socket socket;
    private final String clientName;
    private BufferedReader input;
    private PrintWriter output;

    public ClientThread(Socket socket, String clientName) throws IOException {
        this.socket = socket;
        this.clientName = clientName;
        if (socket != null) {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
        }
    }

    @Override
    public void run() {
        try {
            while (socket.isConnected()) {
                String message = input.readLine();
                if (message == null) break;

                if (!message.isBlank()) {
                    ChatServer.getInstance().sendMessage(message, this);
                }
            }
        } catch (IOException e) {
            System.out.println("ClientThread: " + e.getMessage());
        } finally {
            try {
                ChatServer.getInstance().removeClient(this);
                ChatServer.getInstance().sendMessage(clientName + " left.");
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

    public PrintWriter getOutput() {
        return output;
    }
}
