package com.bmazurkiewicz01.server;

import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {
    private final Socket socket;
    private final String clientName;
    private final BufferedReader input;
    private final PrintWriter output;

    public ClientThread(Socket socket, String clientName) throws IOException {
        this.socket = socket;
        this.clientName = clientName;
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.output = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            String message;
            do {
                message = input.readLine();
                if (message == null) break;

                if (!message.isBlank()) {
                    JchatServer.getInstance().sendMessage(message, this);
                }
            } while (!socket.isClosed());
        } catch (IOException e) {
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

    public PrintWriter getOutput() {
        return output;
    }
}
