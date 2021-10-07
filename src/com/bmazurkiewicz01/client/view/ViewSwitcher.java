package com.bmazurkiewicz01.client.view;

import com.bmazurkiewicz01.client.controller.MainController;
import com.bmazurkiewicz01.client.controller.RoomController;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ViewSwitcher {
    private static Scene scene;
    private static Map<View, Parent> cache;
    private MainController mainController;
    private Stage stage;

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

    public void switchToMain(String helloText) {
        FXMLLoader fxmlLoader = new FXMLLoader(ViewSwitcher.class.getResource(View.MAIN.getFileName()));
        Parent root = null;
        try {
            root = fxmlLoader.load();
            cache.put(View.MAIN, root);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        MainController mainController = fxmlLoader.getController();
        mainController.setHelloLabel(helloText);

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

    public void setUpLeftPaneAnimation(Pane root, Pane leftPane, Button hamburgerButton) {
        TranslateTransition openPane = new TranslateTransition(new Duration(550), leftPane);
        TranslateTransition closePane = new TranslateTransition(new Duration(550), leftPane);
        openPane.setToX(0);

        hamburgerButton.setOnAction(e -> {
            if (leftPane.getTranslateX() != 0) {
                openPane.play();
            } else {
                closePane.setToX(-(leftPane.getWidth()));
                closePane.play();
            }
        });

        leftPane.setOnMouseExited(e -> {
            closePane.setToX(-(leftPane.getWidth()));
            closePane.play();
        });
        root.setOnMouseMoved(e -> {
            if (leftPane.getWidth() < e.getX()) {
                if (leftPane.getTranslateX() == 0) {
                    closePane.setToX(-(leftPane.getWidth()));
                    closePane.play();
                }
            }
        });
    }

    public void setUpTitleBarButtons(ImageView closeButton, ImageView minimizeButton) {
        closeButton.setOnMouseClicked(e -> ViewSwitcher.getInstance().getStage().close());
        minimizeButton.setOnMouseClicked(e -> ViewSwitcher.getInstance().getStage().setIconified(true));
    }


    public MainController getMainController() {
        return mainController;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
