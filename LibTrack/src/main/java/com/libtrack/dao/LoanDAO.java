package com.libtrack.dao;

import com.libtrack.model.Loan;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;

/**
 * DAO для работы с выдачей книг
 */
public class LoanDAO {

    /**
     * Получить все выдачи с информацией о книге и посетителе
     */
    public ObservableList<Loan> getAllLoans() {
        ObservableList<Loan> loans = FXCollections.observableArrayList();
        String sql = "SELECT l.*, b.title as book_title, " +
                "CONCAT(v.first_name, ' ', v.last_name) as visitor_name " +
                "FROM loans l " +
                "JOIN books b ON l.book_id = b.book_id " +
                "JOIN visitors v ON l.visitor_id = v.visitor_id " +
                "ORDER BY l.loan_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                loans.add(extractLoanFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка получения выдач: " + e.getMessage());
            e.printStackTrace();
        }

        return loans;
    }

    /**
     * Получить активные выдачи
     */
    public ObservableList<Loan> getActiveLoans() {
        ObservableList<Loan> loans = FXCollections.observableArrayList();
        String sql = "SELECT l.*, b.title as book_title, " +
                "CONCAT(v.first_name, ' ', v.last_name) as visitor_name " +
                "FROM loans l " +
                "JOIN books b ON l.book_id = b.book_id " +
                "JOIN visitors v ON l.visitor_id = v.visitor_id " +
                "WHERE l.status = 'active' " +
                "ORDER BY l.due_date";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                loans.add(extractLoanFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка получения активных выдач: " + e.getMessage());
        }

        return loans;
    }

    /**
     * Получить просроченные выдачи
     */
    public ObservableList<Loan> getOverdueLoans() {
        ObservableList<Loan> loans = FXCollections.observableArrayList();
        String sql = "SELECT l.*, b.title as book_title, " +
                "CONCAT(v.first_name, ' ', v.last_name) as visitor_name " +
                "FROM loans l " +
                "JOIN books b ON l.book_id = b.book_id " +
                "JOIN visitors v ON l.visitor_id = v.visitor_id " +
                "WHERE l.status = 'active' AND l.due_date < CURDATE() " +
                "ORDER BY l.due_date";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                loans.add(extractLoanFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка получения просроченных выдач: " + e.getMessage());
        }

        return loans;
    }

    /**
     * Выдать книгу
     */
    public boolean issueLoan(int bookId, int visitorId, LocalDate loanDate, LocalDate dueDate, int issuedBy) {
        String sql = "INSERT INTO loans (book_id, visitor_id, loan_date, due_date, status, issued_by) " +
                "VALUES (?, ?, ?, ?, 'active', ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookId);
            stmt.setInt(2, visitorId);
            stmt.setDate(3, Date.valueOf(loanDate));
            stmt.setDate(4, Date.valueOf(dueDate));
            stmt.setInt(5, issuedBy);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка выдачи книги: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Вернуть книгу
     */
    public boolean returnLoan(int loanId, LocalDate returnDate) {
        String sql = "UPDATE loans SET status = 'returned', return_date = ? WHERE loan_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(returnDate));
            stmt.setInt(2, loanId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка возврата книги: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Обновить штраф
     */
    public boolean updateFine(int loanId, double fineAmount) {
        String sql = "UPDATE loans SET fine_amount = ? WHERE loan_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, fineAmount);
            stmt.setInt(2, loanId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка обновления штрафа: " + e.getMessage());
        }

        return false;
    }

    /**
     * Получить выдачи по посетителю
     */
    public ObservableList<Loan> getLoansByVisitor(int visitorId) {
        ObservableList<Loan> loans = FXCollections.observableArrayList();
        String sql = "SELECT l.*, b.title as book_title, " +
                "CONCAT(v.first_name, ' ', v.last_name) as visitor_name " +
                "FROM loans l " +
                "JOIN books b ON l.book_id = b.book_id " +
                "JOIN visitors v ON l.visitor_id = v.visitor_id " +
                "WHERE l.visitor_id = ? " +
                "ORDER BY l.loan_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, visitorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                loans.add(extractLoanFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка получения выдач посетителя: " + e.getMessage());
        }

        return loans;
    }

    /**
     * Получить выдачи по книге
     */
    public ObservableList<Loan> getLoansByBook(int bookId) {
        ObservableList<Loan> loans = FXCollections.observableArrayList();
        String sql = "SELECT l.*, b.title as book_title, " +
                "CONCAT(v.first_name, ' ', v.last_name) as visitor_name " +
                "FROM loans l " +
                "JOIN books b ON l.book_id = b.book_id " +
                "JOIN visitors v ON l.visitor_id = v.visitor_id " +
                "WHERE l.book_id = ? " +
                "ORDER BY l.loan_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                loans.add(extractLoanFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка получения выдач книги: " + e.getMessage());
        }

        return loans;
    }

    /**
     * Создать объект Loan из ResultSet
     */
    private Loan extractLoanFromResultSet(ResultSet rs) throws SQLException {
        Date loanDate = rs.getDate("loan_date");
        Date dueDate = rs.getDate("due_date");
        Date returnDate = rs.getDate("return_date");

        return new Loan(
                rs.getInt("loan_id"),
                rs.getInt("book_id"),
                rs.getInt("visitor_id"),
                loanDate != null ? loanDate.toLocalDate() : null,
                dueDate != null ? dueDate.toLocalDate() : null,
                returnDate != null ? returnDate.toLocalDate() : null,
                rs.getString("status"),
                rs.getDouble("fine_amount"),
                rs.getString("notes"),
                rs.getString("book_title"),
                rs.getString("visitor_name")
        );
    }
}