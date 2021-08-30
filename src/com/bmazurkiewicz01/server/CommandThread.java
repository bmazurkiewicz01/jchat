package com.bmazurkiewicz01.server;

import java.io.IOException;
import java.util.Scanner;

public class CommandThread extends Thread {
    @Override
    public void run() {
        try (Scanner input = new Scanner(System.in)) {
            while (true) {
                String command = input.nextLine();
                synchronized (ChatServer.getInstance()) {
                    try {
                        ChatServer.getInstance().processCommand(command);
                    } catch (IOException e) {
                        System.out.println("CommandThread: " + e.getMessage());
                    }
                }
            }
        }
    }
}
