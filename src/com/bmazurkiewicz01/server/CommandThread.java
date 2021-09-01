package com.bmazurkiewicz01.server;

import java.io.IOException;
import java.util.Scanner;

public class CommandThread extends Thread {
    @Override
    public void run() {
        try (Scanner input = new Scanner(System.in)) {
            String command;
            do {
                command = input.nextLine();
                JchatServer.getInstance().processCommand(command);
            } while (!command.equals("exit"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
