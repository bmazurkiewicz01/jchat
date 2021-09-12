package com.bmazurkiewicz01.client;

public enum View {
    LOGIN("login.fxml"),
    REGISTER("register.fxml"),
    MAIN("main.fxml"),
    ROOM("room.fxml");


    private final String fileName;

    View(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
