package com.libtrack.controller;

import com.libtrack.dao.VisitorDAO;
import com.libtrack.model.Visitor;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;

/**
 * Контроллер управления посетителями
 */
public class VisitorsController {

    @FXML private TableView<Visitor> visitorsTable;
    @FXML private TableColumn<Visitor, Integer> colVisitorId;
    @FXML private TableColumn<Visitor, String> colFirstName;
    @FXML private TableColumn<Visitor, String> colLastName;
    @FXML private TableColumn<Visitor, String> colEmail;
    @FXML private TableColumn<Visitor, String> colPhone;
    @FXML private TableColumn<Visitor, String> colStatus;

    @FXML private TextField searchField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextArea addressArea;
    @FXML private DatePicker birthDatePicker;
    @FXML private ComboBox<String> statusComboBox;

    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Label totalVisitorsLabel;

    private VisitorDAO visitorDAO;
    private Visitor selectedVisitor;

    @FXML
    public void initialize() {
        visitorDAO = new VisitorDAO();

        setupTable();
        setupStatusComboBox();
        loadVisitors();

        // Обработка выбора посетителя
        visitorsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        selectedVisitor = newSelection;
                        fillForm(newSelection);
                    }
                }
        );

        // Поиск
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                loadVisitors();
            } else {
                searchVisitors(newVal.trim());
            }
        });
    }

    /**
     * Настройка таблицы
     */
    private void setupTable() {
        colVisitorId.setCellValueFactory(new PropertyValueFactory<>("visitorId"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Цветная индикация статуса
        colStatus.setCellFactory(column -> new TableCell<Visitor, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("active")) {
                        setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                        setText("Активен");
                    } else if (item.equals("blocked")) {
                        setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                        setText("Заблокирован");
                    } else {
                        setStyle("-fx-text-fill: #64748b;");
                        setText("Неактивен");
                    }
                }
            }
        });
    }

    /**
     * Настройка ComboBox статусов
     */
    private void setupStatusComboBox() {
        statusComboBox.getItems().addAll("active", "inactive", "blocked");
        statusComboBox.setValue("active");
    }

    /**
     * Загрузить всех посетителей
     */
    private void loadVisitors() {
        ObservableList<Visitor> visitors = visitorDAO.getAllVisitors();
        visitorsTable.setItems(visitors);
        totalVisitorsLabel.setText("Всего читателей: " + visitors.size());
    }

    /**
     * Поиск посетителей
     */
    private void searchVisitors(String keyword) {
        ObservableList<Visitor> visitors = visitorDAO.searchVisitors(keyword);
        visitorsTable.setItems(visitors);
        totalVisitorsLabel.setText("Найдено: " + visitors.size());
    }

    /**
     * Заполнить форму данными посетителя
     */
    private void fillForm(Visitor visitor) {
        firstNameField.setText(visitor.getFirstName());
        lastNameField.setText(visitor.getLastName());
        emailField.setText(visitor.getEmail());
        phoneField.setText(visitor.getPhone());
        addressArea.setText(visitor.getAddress());
        birthDatePicker.setValue(visitor.getBirthDate());
        statusComboBox.setValue(visitor.getStatus());
    }

    /**
     * Добавить посетителя
     */
    @FXML
    private void handleAdd() {
        if (!validateInput()) {
            return;
        }

        Visitor visitor = new Visitor();
        fillVisitorFromForm(visitor);
        visitor.setRegistrationDate(LocalDate.now());

        if (visitorDAO.addVisitor(visitor)) {
            showAlert(Alert.AlertType.INFORMATION, "Успех", "Читатель успешно добавлен");
            loadVisitors();
            clearForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось добавить читателя");
        }
    }

    /**
     * Обновить посетителя
     */
    @FXML
    private void handleUpdate() {
        if (selectedVisitor == null) {
            showAlert(Alert.AlertType.WARNING, "Предупреждение", "Выберите читателя для обновления");
            return;
        }

        if (!validateInput()) {
            return;
        }

        fillVisitorFromForm(selectedVisitor);

        if (visitorDAO.updateVisitor(selectedVisitor)) {
            showAlert(Alert.AlertType.INFORMATION, "Успех", "Читатель успешно обновлен");
            loadVisitors();
            clearForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось обновить читателя");
        }
    }

    /**
     * Удалить посетителя
     */
    @FXML
    private void handleDelete() {
        if (selectedVisitor == null) {
            showAlert(Alert.AlertType.WARNING, "Предупреждение", "Выберите читателя для удаления");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Подтверждение");
        confirmation.setHeaderText("Удалить читателя?");
        confirmation.setContentText("Вы уверены, что хотите удалить читателя \"" +
                selectedVisitor.getFullName() + "\"?\n" +
                "История выдач также будет удалена.");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            if (visitorDAO.deleteVisitor(selectedVisitor.getVisitorId())) {
                showAlert(Alert.AlertType.INFORMATION, "Успех", "Читатель успешно удален");
                loadVisitors();
                clearForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Ошибка",
                        "Не удалось удалить читателя. Возможно, есть активные выдачи.");
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
     * Заполнить объект Visitor из формы
     */
    private void fillVisitorFromForm(Visitor visitor) {
        visitor.setFirstName(firstNameField.getText().trim());
        visitor.setLastName(lastNameField.getText().trim());
        visitor.setEmail(emailField.getText().trim());
        visitor.setPhone(phoneField.getText().trim());
        visitor.setAddress(addressArea.getText().trim());
        visitor.setBirthDate(birthDatePicker.getValue());
        visitor.setStatus(statusComboBox.getValue());
    }

    /**
     * Очистить поля формы
     */
    private void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        phoneField.clear();
        addressArea.clear();
        birthDatePicker.setValue(null);
        statusComboBox.setValue("active");
        selectedVisitor = null;
        visitorsTable.getSelectionModel().clearSelection();
    }

    /**
     * Валидация ввода
     */
    private boolean validateInput() {
        if (firstNameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Введите имя");
            return false;
        }

        if (lastNameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Введите фамилию");
            return false;
        }

        String email = emailField.getText().trim();
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Введите корректный email");
            return false;
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