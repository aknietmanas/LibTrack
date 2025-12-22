package com.libtrack.service;

import com.libtrack.dao.AuthorDAO;
import com.libtrack.dao.BookDAO;
import com.libtrack.model.Author;
import javafx.collections.ObservableList;

/**
 * Сервис для работы с авторами (бизнес-логика)
 */
public class AuthorService {

    private final AuthorDAO authorDAO;
    private final BookDAO bookDAO;

    public AuthorService() {
        this.authorDAO = new AuthorDAO();
        this.bookDAO = new BookDAO();
    }

    /**
     * Получить всех авторов
     */
    public ObservableList<Author> getAllAuthors() {
        return authorDAO.getAllAuthors();
    }

    /**
     * Получить автора по ID
     */
    public Author getAuthorById(int authorId) {
        return authorDAO.getAuthorById(authorId);
    }

    /**
     * Добавить автора с валидацией
     */
    public boolean addAuthor(Author author) {
        // Валидация имени
        if (author.getFirstName() == null || author.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("Имя автора обязательно");
        }

        // Валидация фамилии
        if (author.getLastName() == null || author.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Фамилия автора обязательна");
        }

        // Проверка года рождения
        if (author.getBirthYear() != null && author.getBirthYear() > 0) {
            int currentYear = java.time.Year.now().getValue();
            if (author.getBirthYear() < 1000 || author.getBirthYear() > currentYear) {
                throw new IllegalArgumentException(
                        "Год рождения должен быть между 1000 и " + currentYear
                );
            }
        }

        // Проверка на дубликаты (тот же автор)
        if (isDuplicate(author)) {
            throw new IllegalStateException(
                    "Автор с таким именем уже существует в базе"
            );
        }

        return authorDAO.addAuthor(author);
    }

    /**
     * Обновить автора
     */
    public boolean updateAuthor(Author author) {
        if (author.getAuthorId() <= 0) {
            throw new IllegalArgumentException("Некорректный ID автора");
        }

        // Та же валидация что и при добавлении
        if (author.getFirstName() == null || author.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("Имя автора обязательно");
        }

        if (author.getLastName() == null || author.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Фамилия автора обязательна");
        }

        return authorDAO.updateAuthor(author);
    }

    /**
     * Удалить автора
     */
    public boolean deleteAuthor(int authorId) {
        // Проверка: есть ли книги этого автора
        if (hasBooks(authorId)) {
            throw new IllegalStateException(
                    "Невозможно удалить автора: есть связанные книги. " +
                            "Сначала удалите все книги этого автора."
            );
        }

        return authorDAO.deleteAuthor(authorId);
    }

    /**
     * Удалить автора вместе с книгами (каскадное удаление)
     */
    public boolean deleteAuthorWithBooks(int authorId) {
        // Каскадное удаление выполняется на уровне БД
        // через FOREIGN KEY ... ON DELETE CASCADE
        return authorDAO.deleteAuthor(authorId);
    }

    /**
     * Поиск авторов
     */
    public ObservableList<Author> searchAuthors(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllAuthors();
        }
        return authorDAO.searchAuthors(keyword);
    }

    /**
     * Проверить, есть ли у автора книги
     */
    public boolean hasBooks(int authorId) {
        ObservableList<com.libtrack.model.Book> books = bookDAO.getAllBooks();
        return books.stream()
                .anyMatch(book -> book.getAuthorId() == authorId);
    }

    /**
     * Получить количество книг автора
     */
    public int getBooksCount(int authorId) {
        ObservableList<com.libtrack.model.Book> books = bookDAO.getAllBooks();
        return (int) books.stream()
                .filter(book -> book.getAuthorId() == authorId)
                .count();
    }

    /**
     * Проверить на дубликаты (автор с таким же именем)
     */
    private boolean isDuplicate(Author author) {
        ObservableList<Author> allAuthors = authorDAO.getAllAuthors();

        return allAuthors.stream()
                .filter(a -> a.getAuthorId() != author.getAuthorId()) // Исключить самого себя при обновлении
                .anyMatch(a ->
                        a.getFirstName().equalsIgnoreCase(author.getFirstName()) &&
                                a.getLastName().equalsIgnoreCase(author.getLastName())
                );
    }

    /**
     * Получить полное имя автора
     */
    public String getFullName(int authorId) {
        Author author = authorDAO.getAuthorById(authorId);
        return author != null ? author.getFullName() : "Неизвестный автор";
    }

    /**
     * Получить авторов по стране
     */
    public ObservableList<Author> getAuthorsByCountry(String country) {
        ObservableList<Author> allAuthors = getAllAuthors();
        return allAuthors.filtered(author ->
                country.equalsIgnoreCase(author.getCountry())
        );
    }

    /**
     * Получить самых популярных авторов (по количеству книг)
     */
    public ObservableList<Author> getMostPopularAuthors(int limit) {
        ObservableList<Author> authors = getAllAuthors();

        // Сортировка по количеству книг
        authors.sort((a1, a2) -> {
            int count1 = getBooksCount(a1.getAuthorId());
            int count2 = getBooksCount(a2.getAuthorId());
            return Integer.compare(count2, count1); // От большего к меньшему
        });

        // Ограничить количество
        if (authors.size() > limit) {
            return javafx.collections.FXCollections.observableArrayList(
                    authors.subList(0, limit)
            );
        }

        return authors;
    }
}