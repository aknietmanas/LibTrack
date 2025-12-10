package com.libtrack.dao;

import com.libtrack.model.Author;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

/**
 * DAO для работы с авторами
 */
public class AuthorDAO {

    /**
     * Получить всех авторов
     */
    public ObservableList<Author> getAllAuthors() {
        ObservableList<Author> authors = FXCollections.observableArrayList();
        String sql = "SELECT * FROM authors ORDER BY last_name, first_name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                authors.add(extractAuthorFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка получения авторов: " + e.getMessage());
            e.printStackTrace();
        }

        return authors;
    }

    /**
     * Получить автора по ID
     */
    public Author getAuthorById(int authorId) {
        String sql = "SELECT * FROM authors WHERE author_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, authorId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractAuthorFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка получения автора: " + e.getMessage());
        }

        return null;
    }

    /**
     * Добавить автора
     */
    public boolean addAuthor(Author author) {
        String sql = "INSERT INTO authors (first_name, last_name, biography, birth_year, country) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, author.getFirstName());
            stmt.setString(2, author.getLastName());
            stmt.setString(3, author.getBiography());
            stmt.setInt(4, author.getBirthYear());
            stmt.setString(5, author.getCountry());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    author.setAuthorId(generatedKeys.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Ошибка добавления автора: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Обновить автора
     */
    public boolean updateAuthor(Author author) {
        String sql = "UPDATE authors SET first_name = ?, last_name = ?, biography = ?, " +
                "birth_year = ?, country = ? WHERE author_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, author.getFirstName());
            stmt.setString(2, author.getLastName());
            stmt.setString(3, author.getBiography());
            stmt.setInt(4, author.getBirthYear());
            stmt.setString(5, author.getCountry());
            stmt.setInt(6, author.getAuthorId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка обновления автора: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Удалить автора
     */
    public boolean deleteAuthor(int authorId) {
        String sql = "DELETE FROM authors WHERE author_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, authorId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка удаления автора: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Поиск авторов
     */
    public ObservableList<Author> searchAuthors(String keyword) {
        ObservableList<Author> authors = FXCollections.observableArrayList();
        String sql = "SELECT * FROM authors WHERE " +
                "first_name LIKE ? OR last_name LIKE ? OR country LIKE ? " +
                "ORDER BY last_name, first_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                authors.add(extractAuthorFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка поиска авторов: " + e.getMessage());
        }

        return authors;
    }

    /**
     * Создать объект Author из ResultSet
     */
    private Author extractAuthorFromResultSet(ResultSet rs) throws SQLException {
        return new Author(
                rs.getInt("author_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("biography"),
                rs.getInt("birth_year"),
                rs.getString("country")
        );
    }
}