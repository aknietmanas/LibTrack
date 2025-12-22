package com.libtrack.service;

import com.libtrack.dao.BookDAO;
import com.libtrack.dao.LoanDAO;
import com.libtrack.dao.VisitorDAO;
import com.libtrack.model.Book;
import com.libtrack.model.Loan;
import com.libtrack.model.Visitor;
import javafx.collections.ObservableList;
import java.time.LocalDate;

/**
 * Сервис для работы с выдачей книг (бизнес-логика)
 */
public class LoanService {

    private final LoanDAO loanDAO;
    private final BookDAO bookDAO;
    private final VisitorDAO visitorDAO;

    // Константы
    private static final int DEFAULT_LOAN_DAYS = 14;
    private static final int MAX_BOOKS_PER_VISITOR = 5;
    private static final double FINE_PER_DAY = 100.0; // 100 тенге за день просрочки

    public LoanService() {
        this.loanDAO = new LoanDAO();
        this.bookDAO = new BookDAO();
        this.visitorDAO = new VisitorDAO();
    }

    /**
     * Выдать книгу с проверками
     */
    public boolean issueBook(int bookId, int visitorId, int days, int issuedBy) {
        // Проверка книги
        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("Книга не найдена");
        }

        if (book.getCopiesAvailable() <= 0) {
            throw new IllegalStateException("Книга недоступна для выдачи");
        }

        // Проверка посетителя
        Visitor visitor = visitorDAO.getVisitorById(visitorId);
        if (visitor == null) {
            throw new IllegalArgumentException("Читатель не найден");
        }

        if (!"active".equals(visitor.getStatus())) {
            throw new IllegalStateException("Читатель заблокирован или неактивен");
        }

        // Проверка количества книг на руках
        int activeLoans = getActiveLoansCountByVisitor(visitorId);
        if (activeLoans >= MAX_BOOKS_PER_VISITOR) {
            throw new IllegalStateException("Читатель уже взял максимальное количество книг (" + MAX_BOOKS_PER_VISITOR + ")");
        }

        // Расчет дат
        LocalDate loanDate = LocalDate.now();
        LocalDate dueDate = loanDate.plusDays(days > 0 ? days : DEFAULT_LOAN_DAYS);

        // Выдача
        return loanDAO.issueLoan(bookId, visitorId, loanDate, dueDate, issuedBy);
    }

    /**
     * Вернуть книгу с расчетом штрафа
     */
    public double returnBook(int loanId) {
        Loan loan = getLoanById(loanId);
        if (loan == null) {
            throw new IllegalArgumentException("Выдача не найдена");
        }

        if (!"active".equals(loan.getStatus())) {
            throw new IllegalStateException("Книга уже возвращена");
        }

        LocalDate returnDate = LocalDate.now();
        double fine = 0.0;

        // Расчет штрафа за просрочку
        if (returnDate.isAfter(loan.getDueDate())) {
            long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(
                    loan.getDueDate(), returnDate
            );
            fine = overdueDays * FINE_PER_DAY;

            // Обновить штраф в БД
            loanDAO.updateFine(loanId, fine);
        }

        // Вернуть книгу
        loanDAO.returnLoan(loanId, returnDate);

        return fine;
    }

    /**
     * Получить все выдачи
     */
    public ObservableList<Loan> getAllLoans() {
        return loanDAO.getAllLoans();
    }

    /**
     * Получить активные выдачи
     */
    public ObservableList<Loan> getActiveLoans() {
        return loanDAO.getActiveLoans();
    }

    /**
     * Получить просроченные выдачи
     */
    public ObservableList<Loan> getOverdueLoans() {
        return loanDAO.getOverdueLoans();
    }

    /**
     * Получить выдачу по ID
     */
    public Loan getLoanById(int loanId) {
        ObservableList<Loan> loans = getAllLoans();
        return loans.stream()
                .filter(l -> l.getLoanId() == loanId)
                .findFirst()
                .orElse(null);
    }

    /**
     * Получить выдачи по посетителю
     */
    public ObservableList<Loan> getLoansByVisitor(int visitorId) {
        return loanDAO.getLoansByVisitor(visitorId);
    }

    /**
     * Получить выдачи по книге
     */
    public ObservableList<Loan> getLoansByBook(int bookId) {
        return loanDAO.getLoansByBook(bookId);
    }

    /**
     * Получить количество активных выдач у читателя
     */
    public int getActiveLoansCountByVisitor(int visitorId) {
        ObservableList<Loan> loans = loanDAO.getLoansByVisitor(visitorId);
        return (int) loans.stream()
                .filter(loan -> "active".equals(loan.getStatus()))
                .count();
    }

    /**
     * Проверить, может ли читатель взять еще книги
     */
    public boolean canVisitorTakeMoreBooks(int visitorId) {
        return getActiveLoansCountByVisitor(visitorId) < MAX_BOOKS_PER_VISITOR;
    }

    /**
     * Проверить, есть ли у читателя активные выдачи
     */
    public boolean hasActiveLoans(int visitorId) {
        return getActiveLoansCountByVisitor(visitorId) > 0;
    }

    /**
     * Рассчитать штраф за просрочку
     */
    public double calculateFine(Loan loan) {
        if (loan.getReturnDate() != null) {
            // Уже возвращено
            return loan.getFineAmount();
        }

        LocalDate dueDate = loan.getDueDate();
        LocalDate today = LocalDate.now();

        if (today.isAfter(dueDate)) {
            long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(dueDate, today);
            return overdueDays * FINE_PER_DAY;
        }

        return 0.0;
    }

    /**
     * Получить общую сумму штрафов посетителя
     */
    public double getTotalFinesByVisitor(int visitorId) {
        ObservableList<Loan> loans = loanDAO.getLoansByVisitor(visitorId);
        return loans.stream()
                .mapToDouble(this::calculateFine)
                .sum();
    }

    /**
     * Продлить срок выдачи
     */
    public boolean extendLoan(int loanId, int additionalDays) {
        Loan loan = getLoanById(loanId);
        if (loan == null) {
            throw new IllegalArgumentException("Выдача не найдена");
        }

        if (!"active".equals(loan.getStatus())) {
            throw new IllegalStateException("Можно продлить только активную выдачу");
        }

        if (loan.isOverdue()) {
            throw new IllegalStateException("Нельзя продлить просроченную выдачу");
        }

        // Обновить срок возврата
        LocalDate newDueDate = loan.getDueDate().plusDays(additionalDays);

        // Здесь нужно добавить метод в LoanDAO для обновления due_date
        // Временно возвращаем true
        return true;
    }

    /**
     * Получить статистику выдач
     */
    public LoanStatistics getStatistics() {
        ObservableList<Loan> allLoans = getAllLoans();

        int total = allLoans.size();
        int active = (int) allLoans.stream()
                .filter(l -> "active".equals(l.getStatus()))
                .count();
        int returned = (int) allLoans.stream()
                .filter(l -> "returned".equals(l.getStatus()))
                .count();
        int overdue = (int) allLoans.stream()
                .filter(Loan::isOverdue)
                .count();

        return new LoanStatistics(total, active, returned, overdue);
    }

    /**
     * Вложенный класс для статистики выдач
     */
    public static class LoanStatistics {
        private final int total;
        private final int active;
        private final int returned;
        private final int overdue;

        public LoanStatistics(int total, int active, int returned, int overdue) {
            this.total = total;
            this.active = active;
            this.returned = returned;
            this.overdue = overdue;
        }

        public int getTotal() { return total; }
        public int getActive() { return active; }
        public int getReturned() { return returned; }
        public int getOverdue() { return overdue; }
    }
}