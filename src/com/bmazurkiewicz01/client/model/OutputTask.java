package com.bmazurkiewicz01.client.model;

import javafx.concurrent.Task;

import java.io.OutputStream;
import java.io.PrintWriter;

public class OutputTask extends Task<Void> {
    private final OutputStream outputStream;
    private String message;

    public OutputTask(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    protected Void call() {
        PrintWriter printWriter = new PrintWriter(outputStream, true);
        printWriter.println(message);
        return null;
    }
}
