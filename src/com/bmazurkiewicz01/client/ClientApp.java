package com.bmazurkiewicz01.client;

import com.bmazurkiewicz01.client.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Objects;


public class ClientApp extends Application {

    MainController mainController = new MainController();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(new Pane(), 800, 600);
        ViewSwitcher.getInstance().setScene(scene);
        ViewSwitcher.getInstance().switchView(View.LOGIN);

        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/logo.png"))));
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