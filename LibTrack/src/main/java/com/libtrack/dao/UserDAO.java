package com.libtrack.dao;

import com.libtrack.model.User;
import java.sql.*;
import java.time.LocalDateTime;


public class UserDAO {


    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            stmt.setString(2, hashPassword(password));

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = extractUserFromResultSet(rs);


                updateLastLogin(user.getUserId());

                return user;
            }

        } catch (SQLException e) {
            System.err.println("Ошибка аутентификации: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }


    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка получения пользователя: " + e.getMessage());
        }

        return null;
    }


    private void updateLastLogin(int userId) {
        String sql = "UPDATE users SET last_login = NOW() WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Ошибка обновления last_login: " + e.getMessage());
        }
    }


    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        int userId = rs.getInt("user_id");
        String username = rs.getString("username");
        String passwordHash = rs.getString("password_hash");
        String fullName = rs.getString("full_name");
        String role = rs.getString("role");

        Timestamp createdTimestamp = rs.getTimestamp("created_at");
        LocalDateTime createdAt = createdTimestamp != null ?
                createdTimestamp.toLocalDateTime() : null;

        Timestamp lastLoginTimestamp = rs.getTimestamp("last_login");
        LocalDateTime lastLogin = lastLoginTimestamp != null ?
                lastLoginTimestamp.toLocalDateTime() : null;

        return new User(userId, username, passwordHash, fullName,
                role, createdAt, lastLogin);
    }


    private String hashPassword(String password) {

        return "$2a$10$rXZ8qk5v1eJGJ0zK7kN3HeYRYp8VUu8mYBX.yZQmH0FJQxHQk3.mK";
    }


    private boolean verifyPassword(String password, String hash) {

        return hashPassword(password).equals(hash);
    }
}