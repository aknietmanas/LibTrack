package com.libtrack.controller;

import com.libtrack.dao.DatabaseConnection;
import com.libtrack.util.CurrentUser;
import com.libtrack.util.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Контроллер главного экрана с боковым меню
 */
public class DashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private VBox dashboardContent;

    @FXML
    private StackPane contentArea;

    @FXML
    private Button btnDashboard;
    @FXML
    private Button btnBooks;
    @FXML
    private Button btnAuthors;
    @FXML
    private Button btnVisitors;
    @FXML
    private Button btnLoans;
    @FXML
    private Button btnStatistics;

    // Лейблы для статистики Dashboard
    @FXML
    private Label totalBooksLabel;
    @FXML
    private Label activeLoanLabel;
    @FXML
    private Label totalVisitorsLabel;
    @FXML
    private Label totalAuthorsLabel;

    @FXML
    public void initialize() {
        // Установить приветствие
        CurrentUser currentUser = CurrentUser.getInstance();
        if (currentUser.isLoggedIn()) {
            welcomeLabel.setText("Добро пожаловать, " + currentUser.getFullName() + "!");
        }

        // Загрузить статистику Dashboard
        loadStatistics();
    }

    /**
     * Загрузить контент в центральную область
     */
    private void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();

            // Скрыть Dashboard, показать contentArea
            dashboardContent.setVisible(false);
            dashboardContent.setManaged(false);
            contentArea.setVisible(true);
            contentArea.setManaged(true);

            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);

            System.out.println("✓ Загружен контент: " + fxmlPath);
        } catch (IOException e) {
            System.err.println("✗ Ошибка загрузки контента: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Убрать активный класс со всех кнопок меню
     */
    private void resetMenuButtons() {
        if (btnDashboard != null) btnDashboard.getStyleClass().remove("nav-button-active");
        if (btnBooks != null) btnBooks.getStyleClass().remove("nav-button-active");
        if (btnAuthors != null) btnAuthors.getStyleClass().remove("nav-button-active");
        if (btnVisitors != null) btnVisitors.getStyleClass().remove("nav-button-active");
        if (btnLoans != null) btnLoans.getStyleClass().remove("nav-button-active");
        if (btnStatistics != null) btnStatistics.getStyleClass().remove("nav-button-active");
    }

    /**
     * Установить активную кнопку меню
     */
    private void setActiveButton(Button button) {
        resetMenuButtons();
        if (button != null && !button.getStyleClass().contains("nav-button-active")) {
            button.getStyleClass().add("nav-button-active");
        }
    }

    /**
     * Показать Dashboard
     */
    @FXML
    private void showDashboard() {
        // Показать Dashboard, скрыть contentArea
        dashboardContent.setVisible(true);
        dashboardContent.setManaged(true);
        contentArea.setVisible(false);
        contentArea.setManaged(false);
        contentArea.getChildren().clear();

        setActiveButton(btnDashboard);
        loadStatistics();
    }

    /**
     * Загрузить статистику Dashboard
     */
    private void loadStatistics() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Подсчет книг
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM books");
            if (rs.next() && totalBooksLabel != null) {
                totalBooksLabel.setText(String.valueOf(rs.getInt("count")));
            }

            // Подсчет активных выдач
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM loans WHERE status = 'active'");
            if (rs.next() && activeLoanLabel != null) {
                activeLoanLabel.setText(String.valueOf(rs.getInt("count")));
            }

            // Подсчет посетителей
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM visitors");
            if (rs.next() && totalVisitorsLabel != null) {
                totalVisitorsLabel.setText(String.valueOf(rs.getInt("count")));
            }

            // Подсчет авторов
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM authors");
            if (rs.next() && totalAuthorsLabel != null) {
                totalAuthorsLabel.setText(String.valueOf(rs.getInt("count")));
            }

        } catch (Exception e) {
            System.err.println("Ошибка загрузки статистики: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Показать раздел "Книги"
     */
    @FXML
    private void showBooks() {
        loadContent("/fxml/books.fxml");
        setActiveButton(btnBooks);
    }

    /**
     * Показать раздел "Авторы"
     */
    @FXML
    private void showAuthors() {
        loadContent("/fxml/authors.fxml");
        setActiveButton(btnAuthors);
    }

    /**
     * Показать раздел "Посетители"
     */
    @FXML
    private void showVisitors() {
        loadContent("/fxml/visitors.fxml");
        setActiveButton(btnVisitors);
    }

    /**
     * Показать раздел "Выдача книг"
     */
    @FXML
    private void showLoans() {
        loadContent("/fxml/loans.fxml");
        setActiveButton(btnLoans);
    }

    /**
     * Показать раздел "Статистика"
     */
    @FXML
    private void showStatistics() {
        loadContent("/fxml/statistics.fxml");
        setActiveButton(btnStatistics);
    }

    /**
     * Выход из системы
     */
    @FXML
    private void handleLogout() {
        CurrentUser.getInstance().logout();
        SceneManager.showLogin();
    }
}