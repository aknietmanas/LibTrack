package com.libtrack;

import com.libtrack.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        // ГЛАВНОЕ: установить stage в SceneManager
        SceneManager.setPrimaryStage(stage);

        // Настроить окно
        stage.setTitle("LibTrack - Система управления библиотекой");
        stage.setMinWidth(800);
        stage.setMinHeight(600);

        // Показать экран входа через SceneManager
        SceneManager.showLogin();

        // ВАЖНО: Убедитесь, что CSS загружен
        // После создания сцены в SceneManager, попробуйте добавить CSS
        if (stage.getScene() != null) {
            String css = getClass().getResource("/css/style.css").toExternalForm();
            stage.getScene().getStylesheets().add(css);
            System.out.println("CSS загружен: " + css);
        }

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}