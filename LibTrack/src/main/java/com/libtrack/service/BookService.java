package com.libtrack.service;

import com.libtrack.dao.BookDAO;
import com.libtrack.model.Book;
import javafx.collections.ObservableList;

/**
 * Сервис для работы с книгами (бизнес-логика)
 */
public class BookService {

    private final BookDAO bookDAO;

    public BookService() {
        this.bookDAO = new BookDAO();
    }

    /**
     * Получить все книги
     */
    public ObservableList<Book> getAllBooks() {
        return bookDAO.getAllBooks();
    }

    /**
     * Получить книгу по ID
     */
    public Book getBookById(int bookId) {
        return bookDAO.getBookById(bookId);
    }

    /**
     * Добавить книгу с валидацией
     */
    public boolean addBook(Book book) {
        // Бизнес-правила
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Название книги обязательно");
        }

        if (book.getAuthorId() <= 0) {
            throw new IllegalArgumentException("Необходимо выбрать автора");
        }

        if (book.getCopiesTotal() < 0) {
            throw new IllegalArgumentException("Количество экземпляров не может быть отрицательным");
        }

        if (book.getCopiesAvailable() > book.getCopiesTotal()) {
            throw new IllegalArgumentException("Доступных экземпляров не может быть больше общего количества");
        }

        return bookDAO.addBook(book);
    }

    /**
     * Обновить книгу
     */
    public boolean updateBook(Book book) {
        if (book.getBookId() <= 0) {
            throw new IllegalArgumentException("Некорректный ID книги");
        }

        return bookDAO.updateBook(book);
    }

    /**
     * Удалить книгу
     */
    public boolean deleteBook(int bookId) {
        return bookDAO.deleteBook(bookId);
    }

    /**
     * Поиск книг
     */
    public ObservableList<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllBooks();
        }
        return bookDAO.searchBooks(keyword);
    }

    /**
     * Получить доступные книги
     */
    public ObservableList<Book> getAvailableBooks() {
        return bookDAO.getAvailableBooks();
    }

    /**
     * Проверить доступность книги
     */
    public boolean isBookAvailable(int bookId) {
        Book book = bookDAO.getBookById(bookId);
        return book != null && book.getCopiesAvailable() > 0;
    }

    /**
     * Получить количество книг по жанру
     */
    public int getBookCountByGenre(String genre) {
        ObservableList<Book> allBooks = getAllBooks();
        return (int) allBooks.stream()
                .filter(book -> genre.equalsIgnoreCase(book.getGenre()))
                .count();
    }
}