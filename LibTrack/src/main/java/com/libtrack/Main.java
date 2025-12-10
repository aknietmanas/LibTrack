package com.libtrack;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;


public class Main extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        primaryStage.setTitle("LibTrack - Система управления библиотекой");


        showLoginScreen();

        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }


    public static void showLoginScreen() throws IOException {
        Parent root = FXMLLoader.load(
                Main.class.getResource("/fxml/login.fxml")
        );
        Scene scene = new Scene(root, 400, 500);
        scene.getStylesheets().add(
                Main.class.getResource("/css/style.css").toExternalForm()
        );
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }


    public static void showDashboard() throws IOException {
        Parent root = FXMLLoader.load(
                Main.class.getResource("/fxml/dashboard.fxml")
        );
        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(
                Main.class.getResource("/css/style.css").toExternalForm()
        );
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
    }


    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}