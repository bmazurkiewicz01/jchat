package com.bmazurkiewicz01.client;

import com.bmazurkiewicz01.client.controller.MainController;
import com.bmazurkiewicz01.client.view.View;
import com.bmazurkiewicz01.client.view.ViewSwitcher;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;


public class ClientApp extends Application {

    MainController mainController = new MainController();

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(new Pane(), 800, 600);
        scene.setFill(Color.TRANSPARENT);

        ViewSwitcher.getInstance().setScene(scene);
        ViewSwitcher.getInstance().switchView(View.LOGIN);
        ViewSwitcher.getInstance().setStage(primaryStage);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/logo.png"))));
        primaryStage.setTitle("JChat");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        mainController.closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}