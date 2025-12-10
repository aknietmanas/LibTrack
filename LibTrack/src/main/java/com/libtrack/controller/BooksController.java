package com.libtrack.controller;

import com.libtrack.dao.AuthorDAO;
import com.libtrack.dao.BookDAO;
import com.libtrack.model.Author;
import com.libtrack.model.Book;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;


public class BooksController {

    @FXML private TableView<Book> booksTable;
    @FXML private TableColumn<Book, Integer> colBookId;
    @FXML private TableColumn<Book, String> colIsbn;
    @FXML private TableColumn<Book, String> colTitle;
    @FXML private TableColumn<Book, String> colAuthor;
    @FXML private TableColumn<Book, String> colGenre;
    @FXML private TableColumn<Book, Integer> colYear;
    @FXML private TableColumn<Book, Integer> colAvailable;

    @FXML private TextField searchField;
    @FXML private TextField titleField;
    @FXML private TextField isbnField;
    @FXML private ComboBox<Author> authorComboBox;
    @FXML private TextField genreField;
    @FXML private TextField publisherField;
    @FXML private TextField yearField;
    @FXML private TextField pagesField;
    @FXML private TextField copiesTotalField;
    @FXML private TextField copiesAvailableField;
    @FXML private TextArea descriptionArea;

    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button clearButton;

    private BookDAO bookDAO;
    private AuthorDAO authorDAO;
    private Book selectedBook;

    @FXML
    public void initialize() {
        bookDAO = new BookDAO();
        authorDAO = new AuthorDAO();

        setupTable();
        loadBooks();
        loadAuthors();


        booksTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        selectedBook = newSelection;
                        fillForm(newSelection);
                    }
                }
        );


        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                loadBooks();
            } else {
                searchBooks(newVal.trim());
            }
        });
    }


    private void setupTable() {
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("authorName"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("publicationYear"));
        colAvailable.setCellValueFactory(new PropertyValueFactory<>("copiesAvailable"));
    }


    private void loadBooks() {
        ObservableList<Book> books = bookDAO.getAllBooks();
        booksTable.setItems(books);
    }


    private void loadAuthors() {
        ObservableList<Author> authors = authorDAO.getAllAuthors();
        authorComboBox.setItems(authors);
    }


    private void searchBooks(String keyword) {
        ObservableList<Book> books = bookDAO.searchBooks(keyword);
        booksTable.setItems(books);
    }


    private void fillForm(Book book) {
        titleField.setText(book.getTitle());
        isbnField.setText(book.getIsbn());
        genreField.setText(book.getGenre());
        publisherField.setText(book.getPublisher());
        yearField.setText(String.valueOf(book.getPublicationYear()));
        pagesField.setText(String.valueOf(book.getPages()));
        copiesTotalField.setText(String.valueOf(book.getCopiesTotal()));
        copiesAvailableField.setText(String.valueOf(book.getCopiesAvailable()));
        descriptionArea.setText(book.getDescription());


        for (Author author : authorComboBox.getItems()) {
            if (author.getAuthorId() == book.getAuthorId()) {
                authorComboBox.setValue(author);
                break;
            }
        }
    }


    @FXML
    private void handleAdd() {
        if (!validateInput()) {
            return;
        }

        Book book = new Book();
        fillBookFromForm(book);

        if (bookDAO.addBook(book)) {
            showAlert(Alert.AlertType.INFORMATION, "Успех", "Книга успешно добавлена");
            loadBooks();
            clearForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось добавить книгу");
        }
    }


    @FXML
    private void handleUpdate() {
        if (selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "Предупреждение", "Выберите книгу для обновления");
            return;
        }

        if (!validateInput()) {
            return;
        }

        fillBookFromForm(selectedBook);

        if (bookDAO.updateBook(selectedBook)) {
            showAlert(Alert.AlertType.INFORMATION, "Успех", "Книга успешно обновлена");
            loadBooks();
            clearForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось обновить книгу");
        }
    }


    @FXML
    private void handleDelete() {
        if (selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "Предупреждение", "Выберите книгу для удаления");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Подтверждение");
        confirmation.setHeaderText("Удалить книгу?");
        confirmation.setContentText("Вы уверены, что хотите удалить книгу \"" + selectedBook.getTitle() + "\"?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            if (bookDAO.deleteBook(selectedBook.getBookId())) {
                showAlert(Alert.AlertType.INFORMATION, "Успех", "Книга успешно удалена");
                loadBooks();
                clearForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось удалить книгу");
            }
        }
    }


    @FXML
    private void handleClear() {
        clearForm();
    }


    private void fillBookFromForm(Book book) {
        book.setTitle(titleField.getText().trim());
        book.setIsbn(isbnField.getText().trim());
        book.setAuthorId(authorComboBox.getValue().getAuthorId());
        book.setGenre(genreField.getText().trim());
        book.setPublisher(publisherField.getText().trim());
        book.setPublicationYear(Integer.parseInt(yearField.getText().trim()));
        book.setPages(Integer.parseInt(pagesField.getText().trim()));
        book.setCopiesTotal(Integer.parseInt(copiesTotalField.getText().trim()));
        book.setCopiesAvailable(Integer.parseInt(copiesAvailableField.getText().trim()));
        book.setDescription(descriptionArea.getText().trim());
    }


    private void clearForm() {
        titleField.clear();
        isbnField.clear();
        genreField.clear();
        publisherField.clear();
        yearField.clear();
        pagesField.clear();
        copiesTotalField.clear();
        copiesAvailableField.clear();
        descriptionArea.clear();
        authorComboBox.setValue(null);
        selectedBook = null;
        booksTable.getSelectionModel().clearSelection();
    }


    private boolean validateInput() {
        if (titleField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Введите название книги");
            return false;
        }

        if (authorComboBox.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Выберите автора");
            return false;
        }

        try {
            Integer.parseInt(yearField.getText().trim());
            Integer.parseInt(pagesField.getText().trim());
            Integer.parseInt(copiesTotalField.getText().trim());
            Integer.parseInt(copiesAvailableField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Год, страницы и количество должны быть числами");
            return false;
        }

        return true;
    }


    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}