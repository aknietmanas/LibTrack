package com.libtrack.controller;

import com.libtrack.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.libtrack.dao.DatabaseConnection;


public class DashboardController {

    @FXML
    private Label welcomeLabel;

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
        loadStatistics();
    }


    private void loadStatistics() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM books");
            if (rs.next()) {
                totalBooksLabel.setText(String.valueOf(rs.getInt("count")));
            }

            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM loans WHERE status = 'active'");
            if (rs.next()) {
                activeLoanLabel.setText(String.valueOf(rs.getInt("count")));
            }

            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM visitors");
            if (rs.next()) {
                totalVisitorsLabel.setText(String.valueOf(rs.getInt("count")));
            }

            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM authors");
            if (rs.next()) {
                totalAuthorsLabel.setText(String.valueOf(rs.getInt("count")));
            }

        } catch (Exception e) {
            System.err.println("Ошибка загрузки статистики: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void showDashboard() {
        loadStatistics();
    }


    @FXML
    private void showBooks() {
        showAlert("Раздел 'Книги' в разработке", "Этот раздел будет реализован в следующей версии.");
    }


    @FXML
    private void showAuthors() {
        showAlert("Раздел 'Авторы' в разработке", "Этот раздел будет реализован в следующей версии.");
    }


    @FXML
    private void showVisitors() {
        showAlert("Раздел 'Посетители' в разработке", "Этот раздел будет реализован в следующей версии.");
    }


    @FXML
    private void showLoans() {
        showAlert("Раздел 'Выдача книг' в разработке", "Этот раздел будет реализован в следующей версии.");
    }


    @FXML
    private void showStatistics() {
        showAlert("Раздел 'Статистика' в разработке", "Этот раздел будет реализован в следующей версии.");
    }


    @FXML
    private void handleLogout() {
        try {
            Main.showLoginScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}