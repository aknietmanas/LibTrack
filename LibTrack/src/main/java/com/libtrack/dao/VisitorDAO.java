package com.libtrack.dao;

import com.libtrack.model.Visitor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;

/**
 * DAO для работы с посетителями
 */
public class VisitorDAO {

    /**
     * Получить всех посетителей
     */
    public ObservableList<Visitor> getAllVisitors() {
        ObservableList<Visitor> visitors = FXCollections.observableArrayList();
        String sql = "SELECT * FROM visitors ORDER BY last_name, first_name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                visitors.add(extractVisitorFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка получения посетителей: " + e.getMessage());
            e.printStackTrace();
        }

        return visitors;
    }

    /**
     * Получить посетителя по ID
     */
    public Visitor getVisitorById(int visitorId) {
        String sql = "SELECT * FROM visitors WHERE visitor_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, visitorId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractVisitorFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка получения посетителя: " + e.getMessage());
        }

        return null;
    }

    /**
     * Добавить посетителя
     */
    public boolean addVisitor(Visitor visitor) {
        String sql = "INSERT INTO visitors (first_name, last_name, email, phone, address, " +
                "registration_date, birth_date, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, visitor.getFirstName());
            stmt.setString(2, visitor.getLastName());
            stmt.setString(3, visitor.getEmail());
            stmt.setString(4, visitor.getPhone());
            stmt.setString(5, visitor.getAddress());
            stmt.setDate(6, visitor.getRegistrationDate() != null ?
                    Date.valueOf(visitor.getRegistrationDate()) : Date.valueOf(LocalDate.now()));
            stmt.setDate(7, visitor.getBirthDate() != null ?
                    Date.valueOf(visitor.getBirthDate()) : null);
            stmt.setString(8, visitor.getStatus());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    visitor.setVisitorId(generatedKeys.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Ошибка добавления посетителя: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Обновить посетителя
     */
    public boolean updateVisitor(Visitor visitor) {
        String sql = "UPDATE visitors SET first_name = ?, last_name = ?, email = ?, " +
                "phone = ?, address = ?, birth_date = ?, status = ? WHERE visitor_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, visitor.getFirstName());
            stmt.setString(2, visitor.getLastName());
            stmt.setString(3, visitor.getEmail());
            stmt.setString(4, visitor.getPhone());
            stmt.setString(5, visitor.getAddress());
            stmt.setDate(6, visitor.getBirthDate() != null ?
                    Date.valueOf(visitor.getBirthDate()) : null);
            stmt.setString(7, visitor.getStatus());
            stmt.setInt(8, visitor.getVisitorId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка обновления посетителя: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Удалить посетителя
     */
    public boolean deleteVisitor(int visitorId) {
        String sql = "DELETE FROM visitors WHERE visitor_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, visitorId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка удаления посетителя: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Поиск посетителей
     */
    public ObservableList<Visitor> searchVisitors(String keyword) {
        ObservableList<Visitor> visitors = FXCollections.observableArrayList();
        String sql = "SELECT * FROM visitors WHERE " +
                "first_name LIKE ? OR last_name LIKE ? OR email LIKE ? OR phone LIKE ? " +
                "ORDER BY last_name, first_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                visitors.add(extractVisitorFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка поиска посетителей: " + e.getMessage());
        }

        return visitors;
    }

    /**
     * Получить активных посетителей
     */
    public ObservableList<Visitor> getActiveVisitors() {
        ObservableList<Visitor> visitors = FXCollections.observableArrayList();
        String sql = "SELECT * FROM visitors WHERE status = 'active' ORDER BY last_name, first_name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                visitors.add(extractVisitorFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка получения активных посетителей: " + e.getMessage());
        }

        return visitors;
    }

    /**
     * Создать объект Visitor из ResultSet
     */
    private Visitor extractVisitorFromResultSet(ResultSet rs) throws SQLException {
        Date regDate = rs.getDate("registration_date");
        Date birthDate = rs.getDate("birth_date");

        return new Visitor(
                rs.getInt("visitor_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("address"),
                regDate != null ? regDate.toLocalDate() : null,
                birthDate != null ? birthDate.toLocalDate() : null,
                rs.getString("status")
        );
    }
}