package com.libtrack.dao;

import com.libtrack.model.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

/**
 * DAO для работы с книгами
 */
public class BookDAO {

    /**
     * Получить все книги с информацией об авторе
     */
    public ObservableList<Book> getAllBooks() {
        ObservableList<Book> books = FXCollections.observableArrayList();
        String sql = "SELECT b.*, CONCAT(a.first_name, ' ', a.last_name) as author_name " +
                "FROM books b " +
                "JOIN authors a ON b.author_id = a.author_id " +
                "ORDER BY b.title";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка получения книг: " + e.getMessage());
            e.printStackTrace();
        }

        return books;
    }

    /**
     * Получить книгу по ID
     */
    public Book getBookById(int bookId) {
        String sql = "SELECT b.*, CONCAT(a.first_name, ' ', a.last_name) as author_name " +
                "FROM books b " +
                "JOIN authors a ON b.author_id = a.author_id " +
                "WHERE b.book_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractBookFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка получения книги: " + e.getMessage());
        }

        return null;
    }

    /**
     * Добавить книгу
     */
    public boolean addBook(Book book) {
        String sql = "INSERT INTO books (isbn, title, author_id, genre, publisher, " +
                "publication_year, pages, copies_total, copies_available, description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setInt(3, book.getAuthorId());
            stmt.setString(4, book.getGenre());
            stmt.setString(5, book.getPublisher());
            stmt.setInt(6, book.getPublicationYear());
            stmt.setInt(7, book.getPages());
            stmt.setInt(8, book.getCopiesTotal());
            stmt.setInt(9, book.getCopiesAvailable());
            stmt.setString(10, book.getDescription());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    book.setBookId(generatedKeys.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Ошибка добавления книги: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Обновить книгу
     */
    public boolean updateBook(Book book) {
        String sql = "UPDATE books SET isbn = ?, title = ?, author_id = ?, genre = ?, " +
                "publisher = ?, publication_year = ?, pages = ?, copies_total = ?, " +
                "copies_available = ?, description = ? WHERE book_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setInt(3, book.getAuthorId());
            stmt.setString(4, book.getGenre());
            stmt.setString(5, book.getPublisher());
            stmt.setInt(6, book.getPublicationYear());
            stmt.setInt(7, book.getPages());
            stmt.setInt(8, book.getCopiesTotal());
            stmt.setInt(9, book.getCopiesAvailable());
            stmt.setString(10, book.getDescription());
            stmt.setInt(11, book.getBookId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка обновления книги: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Удалить книгу
     */
    public boolean deleteBook(int bookId) {
        String sql = "DELETE FROM books WHERE book_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка удаления книги: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Поиск книг
     */
    public ObservableList<Book> searchBooks(String keyword) {
        ObservableList<Book> books = FXCollections.observableArrayList();
        String sql = "SELECT b.*, CONCAT(a.first_name, ' ', a.last_name) as author_name " +
                "FROM books b " +
                "JOIN authors a ON b.author_id = a.author_id " +
                "WHERE b.title LIKE ? OR b.isbn LIKE ? OR b.genre LIKE ? " +
                "OR a.first_name LIKE ? OR a.last_name LIKE ? " +
                "ORDER BY b.title";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            for (int i = 1; i <= 5; i++) {
                stmt.setString(i, searchPattern);
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка поиска книг: " + e.getMessage());
        }

        return books;
    }

    /**
     * Получить доступные книги
     */
    public ObservableList<Book> getAvailableBooks() {
        ObservableList<Book> books = FXCollections.observableArrayList();
        String sql = "SELECT b.*, CONCAT(a.first_name, ' ', a.last_name) as author_name " +
                "FROM books b " +
                "JOIN authors a ON b.author_id = a.author_id " +
                "WHERE b.copies_available > 0 " +
                "ORDER BY b.title";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка получения доступных книг: " + e.getMessage());
        }

        return books;
    }

    /**
     * Создать объект Book из ResultSet
     */
    private Book extractBookFromResultSet(ResultSet rs) throws SQLException {
        return new Book(
                rs.getInt("book_id"),
                rs.getString("isbn"),
                rs.getString("title"),
                rs.getInt("author_id"),
                rs.getString("author_name"),
                rs.getString("genre"),
                rs.getString("publisher"),
                rs.getInt("publication_year"),
                rs.getInt("pages"),
                rs.getInt("copies_total"),
                rs.getInt("copies_available"),
                rs.getString("description")
        );
    }
}