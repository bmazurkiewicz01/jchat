package com.bmazurkiewicz01.client.model;

import com.bmazurkiewicz01.client.controller.MainController;
import javafx.concurrent.Task;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InputThread extends Thread {
    private final BufferedReader input;
    private final MainController mainController;

    public InputThread(BufferedReader input, MainController mainController) {
        this.input = input;
        this.mainController = mainController;
    }

    @Override
    public void run() {
        while (!ServerConnection.getInstance().isClosed()) {
            try {
                String message = input.readLine();
                if (message == null) break;
                mainController.updateTextArea(message + "\n");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
