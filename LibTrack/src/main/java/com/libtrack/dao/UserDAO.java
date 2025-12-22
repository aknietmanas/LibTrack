package com.libtrack.dao;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.libtrack.model.User;
import com.libtrack.util.CurrentUser;
import java.sql.*;
import java.time.LocalDateTime;

public class UserDAO {

    /**
     * Аутентификация пользователя с BCrypt
     */
    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");

                // Проверка пароля через BCrypt
                BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), storedHash);

                if (result.verified) {
                    User user = extractUserFromResultSet(rs);

                    // Сохранить пользователя в CurrentUser
                    CurrentUser.getInstance().setUser(user);

                    // Обновить время последнего входа
                    updateLastLogin(user.getUserId());

                    System.out.println("✓ Аутентификация успешна: " + user.getFullName());
                    return user;
                } else {
                    System.out.println("✗ Неверный пароль");
                }
            } else {
                System.out.println("✗ Пользователь не найден");
            }

        } catch (SQLException e) {
            System.err.println("✗ Ошибка аутентификации: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Получить пользователя по ID
     */
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

    /**
     * Обновить время последнего входа
     */
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

    /**
     * Извлечь пользователя из ResultSet
     */
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

    /**
     * Создать BCrypt хеш для пароля
     */
    public String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }
}