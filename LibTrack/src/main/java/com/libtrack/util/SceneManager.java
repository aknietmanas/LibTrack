package com.libtrack.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Утилита для управления переходами между экранами
 */
public class SceneManager {

    private static Stage primaryStage;

    // Пути к FXML файлам
    public static final String LOGIN_FXML = "/fxml/login.fxml";
    public static final String DASHBOARD_FXML = "/fxml/dashboard.fxml";
    public static final String BOOKS_FXML = "/fxml/books.fxml";
    public static final String AUTHORS_FXML = "/fxml/authors.fxml";
    public static final String VISITORS_FXML = "/fxml/visitors.fxml";
    public static final String LOANS_FXML = "/fxml/loans.fxml";
    public static final String STATISTICS_FXML = "/fxml/statistics.fxml";

    // Путь к стилям
    private static final String STYLES_CSS = "/css/style.css";

    /**
     * Установить главный Stage
     */
    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Получить главный Stage
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Загрузить сцену с заголовком и размерами
     */
    public static void loadScene(String fxmlPath, String title, int width, int height) throws IOException {
        if (primaryStage == null) {
            throw new IllegalStateException("Primary stage не установлен");
        }

        // Загрузить FXML
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
        Parent root = loader.load();

        // Создать сцену
        Scene scene;
        if (width > 0 && height > 0) {
            scene = new Scene(root, width, height);
        } else {
            scene = new Scene(root);
        }

        // Применить стили
        applyStyles(scene);

        // Установить сцену
        primaryStage.setScene(scene);

        // Установить заголовок
        if (title != null) {
            primaryStage.setTitle("LibTrack - " + title);
        }

        // Центрировать окно
        primaryStage.centerOnScreen();

        System.out.println("✓ Загружена сцена: " + fxmlPath);
    }

    /**
     * Открыть модальное окно с размерами
     */
    public static void openModal(String fxmlPath, String title, int width, int height) throws IOException {
        Stage modalStage = new Stage();

        // Загрузить FXML
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
        Parent root = loader.load();

        // Создать сцену
        Scene scene;
        if (width > 0 && height > 0) {
            scene = new Scene(root, width, height);
        } else {
            scene = new Scene(root);
        }

        // Применить стили
        applyStyles(scene);

        // Настроить модальное окно
        modalStage.setScene(scene);
        modalStage.setTitle("LibTrack - " + title);
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.initOwner(primaryStage);

        modalStage.showAndWait();
    }

    /**
     * Применить стили к сцене
     */
    private static void applyStyles(Scene scene) {
        try {
            String stylesPath = SceneManager.class.getResource(STYLES_CSS).toExternalForm();
            scene.getStylesheets().add(stylesPath);
        } catch (Exception e) {
            System.err.println("⚠ Не удалось загрузить стили: " + e.getMessage());
        }
    }

    /**
     * Загрузить экран входа
     */
    public static void showLogin() {
        try {
            loadScene(LOGIN_FXML, "Вход в систему", 450, 550);
            primaryStage.setMaximized(false);
            primaryStage.setResizable(true);
        } catch (IOException e) {
            System.err.println("✗ Ошибка загрузки экрана входа: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Загрузить главный экран с боковым меню
     */
    public static void showDashboard() {
        try {
            loadScene(DASHBOARD_FXML, "Главная", 1200, 800);
            primaryStage.setMaximized(true);
        } catch (IOException e) {
            System.err.println("✗ Ошибка загрузки главного экрана: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Открыть экран управления книгами
     * @deprecated Теперь загружается внутри Dashboard через DashboardController
     */
    @Deprecated
    public static void showBooks() {
        System.out.println("⚠ showBooks() устарел. Используйте DashboardController.showBooks()");
    }

    /**
     * Открыть экран управления авторами
     * @deprecated Теперь загружается внутри Dashboard через DashboardController
     */
    @Deprecated
    public static void showAuthors() {
        System.out.println("⚠ showAuthors() устарел. Используйте DashboardController.showAuthors()");
    }

    /**
     * Открыть экран управления посетителями
     * @deprecated Теперь загружается внутри Dashboard через DashboardController
     */
    @Deprecated
    public static void showVisitors() {
        System.out.println("⚠ showVisitors() устарел. Используйте DashboardController.showVisitors()");
    }

    /**
     * Открыть экран выдачи книг
     * @deprecated Теперь загружается внутри Dashboard через DashboardController
     */
    @Deprecated
    public static void showLoans() {
        System.out.println("⚠ showLoans() устарел. Используйте DashboardController.showLoans()");
    }

    /**
     * Открыть экран статистики
     * @deprecated Теперь загружается внутри Dashboard через DashboardController
     */
    @Deprecated
    public static void showStatistics() {
        System.out.println("⚠ showStatistics() устарел. Используйте DashboardController.showStatistics()");
    }
}