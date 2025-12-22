package com.libtrack.controller;

import com.libtrack.dao.AuthorDAO;
import com.libtrack.model.Author;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Контроллер управления авторами
 */
public class AuthorsController {

    @FXML private TableView<Author> authorsTable;
    @FXML private TableColumn<Author, Integer> colAuthorId;
    @FXML private TableColumn<Author, String> colFirstName;
    @FXML private TableColumn<Author, String> colLastName;
    @FXML private TableColumn<Author, String> colCountry;
    @FXML private TableColumn<Author, Integer> colBirthYear;

    @FXML private TextField searchField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField countryField;
    @FXML private TextField birthYearField;
    @FXML private TextArea biographyArea;

    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Label totalAuthorsLabel;

    private AuthorDAO authorDAO;
    private Author selectedAuthor;

    @FXML
    public void initialize() {
        authorDAO = new AuthorDAO();

        setupTable();
        loadAuthors();

        // Обработка выбора автора
        authorsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        selectedAuthor = newSelection;
                        fillForm(newSelection);
                    }
                }
        );

        // Поиск
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                loadAuthors();
            } else {
                searchAuthors(newVal.trim());
            }
        });
    }

    /**
     * Настройка таблицы
     */
    private void setupTable() {
        colAuthorId.setCellValueFactory(new PropertyValueFactory<>("authorId"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colCountry.setCellValueFactory(new PropertyValueFactory<>("country"));
        colBirthYear.setCellValueFactory(new PropertyValueFactory<>("birthYear"));
    }

    /**
     * Загрузить всех авторов
     */
    private void loadAuthors() {
        ObservableList<Author> authors = authorDAO.getAllAuthors();
        authorsTable.setItems(authors);
        totalAuthorsLabel.setText("Всего авторов: " + authors.size());
    }

    /**
     * Поиск авторов
     */
    private void searchAuthors(String keyword) {
        ObservableList<Author> authors = authorDAO.searchAuthors(keyword);
        authorsTable.setItems(authors);
        totalAuthorsLabel.setText("Найдено: " + authors.size());
    }

    /**
     * Заполнить форму данными автора
     */
    private void fillForm(Author author) {
        firstNameField.setText(author.getFirstName());
        lastNameField.setText(author.getLastName());
        countryField.setText(author.getCountry());
        birthYearField.setText(String.valueOf(author.getBirthYear()));
        biographyArea.setText(author.getBiography());
    }

    /**
     * Добавить автора
     */
    @FXML
    private void handleAdd() {
        if (!validateInput()) {
            return;
        }

        Author author = new Author();
        fillAuthorFromForm(author);

        if (authorDAO.addAuthor(author)) {
            showAlert(Alert.AlertType.INFORMATION, "Успех", "Автор успешно добавлен");
            loadAuthors();
            clearForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось добавить автора");
        }
    }

    /**
     * Обновить автора
     */
    @FXML
    private void handleUpdate() {
        if (selectedAuthor == null) {
            showAlert(Alert.AlertType.WARNING, "Предупреждение", "Выберите автора для обновления");
            return;
        }

        if (!validateInput()) {
            return;
        }

        fillAuthorFromForm(selectedAuthor);

        if (authorDAO.updateAuthor(selectedAuthor)) {
            showAlert(Alert.AlertType.INFORMATION, "Успех", "Автор успешно обновлен");
            loadAuthors();
            clearForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось обновить автора");
        }
    }

    /**
     * Удалить автора
     */
    @FXML
    private void handleDelete() {
        if (selectedAuthor == null) {
            showAlert(Alert.AlertType.WARNING, "Предупреждение", "Выберите автора для удаления");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Подтверждение");
        confirmation.setHeaderText("Удалить автора?");
        confirmation.setContentText("Вы уверены, что хотите удалить автора \"" +
                selectedAuthor.getFullName() + "\"?\n" +
                "Все книги этого автора также будут удалены.");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            if (authorDAO.deleteAuthor(selectedAuthor.getAuthorId())) {
                showAlert(Alert.AlertType.INFORMATION, "Успех", "Автор успешно удален");
                loadAuthors();
                clearForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Ошибка",
                        "Не удалось удалить автора. Возможно, есть связанные книги.");
            }
        }
    }

    /**
     * Очистить форму
     */
    @FXML
    private void handleClear() {
        clearForm();
    }

    /**
     * Заполнить объект Author из формы
     */
    private void fillAuthorFromForm(Author author) {
        author.setFirstName(firstNameField.getText().trim());
        author.setLastName(lastNameField.getText().trim());
        author.setCountry(countryField.getText().trim());

        try {
            author.setBirthYear(Integer.parseInt(birthYearField.getText().trim()));
        } catch (NumberFormatException e) {
            author.setBirthYear(0);
        }

        author.setBiography(biographyArea.getText().trim());
    }

    /**
     * Очистить поля формы
     */
    private void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
        countryField.clear();
        birthYearField.clear();
        biographyArea.clear();
        selectedAuthor = null;
        authorsTable.getSelectionModel().clearSelection();
    }

    /**
     * Валидация ввода
     */
    private boolean validateInput() {
        if (firstNameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Введите имя автора");
            return false;
        }

        if (lastNameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Введите фамилию автора");
            return false;
        }

        if (!birthYearField.getText().trim().isEmpty()) {
            try {
                int year = Integer.parseInt(birthYearField.getText().trim());
                if (year < 1000 || year > 2100) {
                    showAlert(Alert.AlertType.ERROR, "Ошибка", "Год рождения должен быть между 1000 и 2100");
                    return false;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Год рождения должен быть числом");
                return false;
            }
        }

        return true;
    }

    /**
     * Показать сообщение
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}