package com.libtrack.service;

import com.libtrack.dao.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Сервис для сбора и анализа статистики библиотеки
 */
public class StatisticsService {

    /**
     * Класс для хранения статистических данных
     */
    public static class StatData {
        private String label;
        private int value;

        public StatData(String label, int value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() { return label; }
        public int getValue() { return value; }
    }

    /**
     * Получить общую статистику библиотеки
     */
    public Map<String, Integer> getGeneralStatistics() {
        Map<String, Integer> stats = new HashMap<>();

        try (Connection conn = DatabaseConnection.getConnection()) {

            // Общее количество книг
            stats.put("totalBooks", getCount(conn, "SELECT COUNT(*) FROM books"));

            // Общее количество экземпляров
            stats.put("totalCopies", getSum(conn, "SELECT SUM(copies_total) FROM books"));

            // Доступных экземпляров
            stats.put("availableCopies", getSum(conn, "SELECT SUM(copies_available) FROM books"));

            // Всего авторов
            stats.put("totalAuthors", getCount(conn, "SELECT COUNT(*) FROM authors"));

            // Всего читателей
            stats.put("totalVisitors", getCount(conn, "SELECT COUNT(*) FROM visitors"));

            // Активных читателей
            stats.put("activeVisitors", getCount(conn,
                    "SELECT COUNT(*) FROM visitors WHERE status = 'active'"));

            // Активных выдач
            stats.put("activeLoans", getCount(conn,
                    "SELECT COUNT(*) FROM loans WHERE status = 'active'"));

            // Просроченных выдач
            stats.put("overdueLoans", getCount(conn,
                    "SELECT COUNT(*) FROM loans WHERE status = 'active' AND due_date < CURDATE()"));

            // Всего выдач за всё время
            stats.put("totalLoans", getCount(conn, "SELECT COUNT(*) FROM loans"));

            // Возвращенных книг
            stats.put("returnedLoans", getCount(conn,
                    "SELECT COUNT(*) FROM loans WHERE status = 'returned'"));

        } catch (SQLException e) {
            System.err.println("Ошибка получения статистики: " + e.getMessage());
            e.printStackTrace();
        }

        return stats;
    }

    /**
     * Получить топ-10 популярных книг
     */
    public ObservableList<StatData> getPopularBooks() {
        ObservableList<StatData> data = FXCollections.observableArrayList();

        String sql = "SELECT b.title, COUNT(l.loan_id) as loan_count " +
                "FROM books b " +
                "LEFT JOIN loans l ON b.book_id = l.book_id " +
                "GROUP BY b.book_id, b.title " +
                "ORDER BY loan_count DESC " +
                "LIMIT 10";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                data.add(new StatData(
                        rs.getString("title"),
                        rs.getInt("loan_count")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка получения популярных книг: " + e.getMessage());
        }

        return data;
    }

    /**
     * Получить статистику по жанрам
     */
    public ObservableList<StatData> getBooksByGenre() {
        ObservableList<StatData> data = FXCollections.observableArrayList();

        String sql = "SELECT genre, COUNT(*) as count " +
                "FROM books " +
                "WHERE genre IS NOT NULL AND genre != '' " +
                "GROUP BY genre " +
                "ORDER BY count DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                data.add(new StatData(
                        rs.getString("genre"),
                        rs.getInt("count")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка получения статистики по жанрам: " + e.getMessage());
        }

        return data;
    }

    /**
     * Получить выдачи по месяцам (последние 12 месяцев)
     */
    public ObservableList<StatData> getLoansByMonth() {
        ObservableList<StatData> data = FXCollections.observableArrayList();

        String sql = "SELECT DATE_FORMAT(loan_date, '%Y-%m') as month, " +
                "COUNT(*) as count " +
                "FROM loans " +
                "WHERE loan_date >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH) " +
                "GROUP BY month " +
                "ORDER BY month";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                data.add(new StatData(
                        rs.getString("month"),
                        rs.getInt("count")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка получения статистики по месяцам: " + e.getMessage());
        }

        return data;
    }

    /**
     * Получить топ-10 активных читателей
     */
    public ObservableList<StatData> getMostActiveVisitors() {
        ObservableList<StatData> data = FXCollections.observableArrayList();

        String sql = "SELECT CONCAT(v.first_name, ' ', v.last_name) as name, " +
                "COUNT(l.loan_id) as loan_count " +
                "FROM visitors v " +
                "LEFT JOIN loans l ON v.visitor_id = l.visitor_id " +
                "GROUP BY v.visitor_id, name " +
                "ORDER BY loan_count DESC " +
                "LIMIT 10";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                data.add(new StatData(
                        rs.getString("name"),
                        rs.getInt("loan_count")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка получения активных читателей: " + e.getMessage());
        }

        return data;
    }

    /**
     * Получить статистику возвратов (вовремя vs просрочка)
     */
    public Map<String, Integer> getReturnStatistics() {
        Map<String, Integer> stats = new HashMap<>();

        try (Connection conn = DatabaseConnection.getConnection()) {

            // Возвращено вовремя
            stats.put("onTime", getCount(conn,
                    "SELECT COUNT(*) FROM loans " +
                            "WHERE status = 'returned' AND return_date <= due_date"));

            // Возвращено с просрочкой
            stats.put("late", getCount(conn,
                    "SELECT COUNT(*) FROM loans " +
                            "WHERE status = 'returned' AND return_date > due_date"));

            // Текущие просрочки
            stats.put("currentOverdue", getCount(conn,
                    "SELECT COUNT(*) FROM loans " +
                            "WHERE status = 'active' AND due_date < CURDATE()"));

        } catch (SQLException e) {
            System.err.println("Ошибка получения статистики возвратов: " + e.getMessage());
        }

        return stats;
    }

    /**
     * Вспомогательный метод: получить количество записей
     */
    private int getCount(Connection conn, String sql) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Вспомогательный метод: получить сумму
     */
    private int getSum(Connection conn, String sql) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int result = rs.getInt(1);
                return rs.wasNull() ? 0 : result;
            }
        }
        return 0;
    }
}