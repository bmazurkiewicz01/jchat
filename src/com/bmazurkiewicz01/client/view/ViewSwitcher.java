package com.bmazurkiewicz01.client.view;

import com.bmazurkiewicz01.client.controller.MainController;
import com.bmazurkiewicz01.client.controller.RoomController;
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
    private MainController mainController;

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

    public void switchView(View view) {
        Parent root = null;

        try {
            root = FXMLLoader.load(Objects.requireNonNull(ViewSwitcher.class.getResource(view.getFileName())));
            cache.put(view, root);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        if (root != null) scene.setRoot(root);
        else System.out.println("ViewSwitcher: root was null");
    }

    public void joinRoomAndSetLabels(String room, String owner, boolean isOwner) {
        FXMLLoader fxmlLoader = new FXMLLoader(ViewSwitcher.class.getResource(View.ROOM.getFileName()));
        Parent root = null;
        try {
            root = fxmlLoader.load();
            cache.put(View.ROOM, root);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        RoomController roomController = fxmlLoader.getController();
        roomController.setRoomAndOwnerText(room, owner);
        if (isOwner) roomController.setUpOwnerRoom();

        if (root != null) scene.setRoot(root);
        else System.out.println("ViewSwitcher: root was null");
    }

    public MainController getMainController() {
        return mainController;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
