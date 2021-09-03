package com.bmazurkiewicz01.client;

public enum View {
    LOGIN("login.fxml"),
    MAIN("main.fxml"),
    REGISTER("register.fxml");

    String fileName;

    View(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
