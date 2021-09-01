package com.bmazurkiewicz01.client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ViewSwitcher {
    private Scene scene;

    private static ViewSwitcher instance;

    private ViewSwitcher() {
        if (instance != null) throw new IllegalStateException("Cannot create new instance.");
    }

    public static ViewSwitcher getInstance() {
        if (instance == null) instance = new ViewSwitcher();
        return instance;
    }

    public void setScene(Scene scene) {
        if (scene != null) this.scene = scene;
    }

    public void switchView(View view) {
        Parent root;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(ViewSwitcher.class.getResource(view.getFileName())));
            scene.setRoot(root);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
}
