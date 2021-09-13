package com.bmazurkiewicz01.client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ViewSwitcher {
    private static Scene scene;
    private static Map<View, Parent> cache;

    private static ViewSwitcher instance;

    private ViewSwitcher() {
        if (instance != null) throw new IllegalStateException("Cannot create new instance.");
        cache = new HashMap<>();
    }

    public static ViewSwitcher getInstance() {
        if (instance == null) instance = new ViewSwitcher();
        return instance;
    }

    public void setScene(Scene scene) {
        if (scene != null) ViewSwitcher.scene = scene;
    }

    public void switchView(View view, boolean useCache) {
        Parent root = null;

        if (useCache && cache.containsKey(view)) root = cache.get(view);
        else {
            try {
                root = FXMLLoader.load(Objects.requireNonNull(ViewSwitcher.class.getResource(view.getFileName())));
                cache.put(view, root);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        if (root != null) scene.setRoot(root);
        else System.out.println("ViewSwitcher: root was null");
    }
}
