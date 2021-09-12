package com.bmazurkiewicz01.client.model;

import javafx.concurrent.Task;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class OutputTask extends Task<Void> {
    private final ObjectOutputStream output;
    private String message;

    public OutputTask(ObjectOutputStream output) {
        this.output = output;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    protected Void call() throws IOException {
        synchronized (output) {
            output.writeObject(message);
            output.flush();
        }
        return null;
    }
}
