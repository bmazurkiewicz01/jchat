package com.bmazurkiewicz01.client.model;

import javafx.concurrent.Task;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class RoomConnectionTask extends Task<Void> {
    private final ObjectOutputStream output;
    private final String message;

    public RoomConnectionTask(ObjectOutputStream output, String message) {
        this.output = output;
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
