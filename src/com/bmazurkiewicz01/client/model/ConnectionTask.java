package com.bmazurkiewicz01.client.model;

import javafx.concurrent.Task;

import java.io.*;
import java.net.Socket;

public class ConnectionTask extends Task<String> {
    private final Socket socket;
    private final String connectionString;

    public ConnectionTask(Socket socket, String connectionString) {
        this.socket = socket;
        this.connectionString = connectionString;
    }

    @Override
    protected String call() throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        new PrintWriter(socket.getOutputStream(), true).println(connectionString);
        return input.readLine();
    }
}
