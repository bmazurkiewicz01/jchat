package com.bmazurkiewicz01.client.model;

import javafx.concurrent.Task;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ConnectionTask extends Task<String> {
    private final String connectionString;
    private final ObjectOutputStream output;
    private final ObjectInputStream input;

    public ConnectionTask(String connectionString, ObjectOutputStream output, ObjectInputStream input) {
        this.connectionString = connectionString;
        this.output = output;
        this.input = input;
    }

    @Override
    protected String call() throws IOException, ClassNotFoundException {
        output.writeObject(connectionString);
        output.flush();
        return (String) input.readObject();
    }
}
