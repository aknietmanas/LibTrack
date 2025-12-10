package com.libtrack.controller;

import com.libtrack.dao.BookDAO;
import com.libtrack.dao.LoanDAO;
import com.libtrack.dao.VisitorDAO;
import com.libtrack.model.Book;
import com.libtrack.model.Loan;
import com.libtrack.model.Visitor;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;


public class LoansController {

    @FXML private TableView<Loan> loansTable;
    @FXML private TableColumn<Loan, Integer> colLoanId;
    @FXML private TableColumn<Loan, String> colBookTitle;
    @FXML private TableColumn<Loan, String> colVisitorName;
    @FXML private TableColumn<Loan, LocalDate> colLoanDate;
    @FXML private TableColumn<Loan, LocalDate> colDueDate;
    @FXML private TableColumn<Loan, LocalDate> colReturnDate;
    @FXML private TableColumn<Loan, String> colStatus;

    @FXML private ComboBox<Book> bookComboBox;
    @FXML private ComboBox<Visitor> visitorComboBox;
    @FXML private DatePicker loanDatePicker;
    @FXML private DatePicker dueDatePicker;
    @FXML private TextField daysField;

    @FXML private RadioButton allLoansRadio;
    @FXML private RadioButton activeLoansRadio;
    @FXML private RadioButton overdueLoansRadio;

    @FXML private Button issueButton;
    @FXML private Button returnButton;

    private LoanDAO loanDAO;
    private BookDAO bookDAO;
    private VisitorDAO visitorDAO;
    private Loan selectedLoan;

    @FXML
    public void initialize() {
        loanDAO = new LoanDAO();
        bookDAO = new BookDAO();
        visitorDAO = new VisitorDAO();

        setupTable();
        loadLoans();
        loadBooksAndVisitors();
        setupDatePickers();


        loansTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    selectedLoan = newSelection;
                    returnButton.setDisable(newSelection == null ||
                            !"active".equals(newSelection.getStatus()));
                }
        );


        ToggleGroup filterGroup = new ToggleGroup();
        allLoansRadio.setToggleGroup(filterGroup);
        activeLoansRadio.setToggleGroup(filterGroup);
        overdueLoansRadio.setToggleGroup(filterGroup);

        allLoansRadio.setSelected(true);

        filterGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == allLoansRadio) {
                loadLoans();
            } else if (newVal == activeLoansRadio) {
                loadActiveLoans();
            } else if (newVal == overdueLoansRadio) {
                loadOverdueLoans();
            }
        });


        daysField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (loanDatePicker.getValue() != null && !newVal.isEmpty()) {
                try {
                    int days = Integer.parseInt(newVal);
                    dueDatePicker.setValue(loanDatePicker.getValue().plusDays(days));
                } catch (NumberFormatException e) {

                }
            }
        });
    }


    private void setupTable() {
        colLoanId.setCellValueFactory(new PropertyValueFactory<>("loanId"));
        colBookTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        colVisitorName.setCellValueFactory(new PropertyValueFactory<>("visitorName"));
        colLoanDate.setCellValueFactory(new PropertyValueFactory<>("loanDate"));
        colDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("displayStatus"));


        colStatus.setCellFactory(column -> new TableCell<Loan, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("Просрочена")) {
                        setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    } else if (item.contains("Возвращена")) {
                        setStyle("-fx-text-fill: #10b981;");
                    } else {
                        setStyle("-fx-text-fill: #2563eb;");
                    }
                }
            }
        });
    }


    private void setupDatePickers() {
        loanDatePicker.setValue(LocalDate.now());
        daysField.setText("14"); // По умолчанию 14 дней
        dueDatePicker.setValue(LocalDate.now().plusDays(14));
    }


    private void loadLoans() {
        ObservableList<Loan> loans = loanDAO.getAllLoans();
        loansTable.setItems(loans);
    }


    private void loadActiveLoans() {
        ObservableList<Loan> loans = loanDAO.getActiveLoans();
        loansTable.setItems(loans);
    }


    private void loadOverdueLoans() {
        ObservableList<Loan> loans = loanDAO.getOverdueLoans();
        loansTable.setItems(loans);
    }


    private void loadBooksAndVisitors() {
        ObservableList<Book> availableBooks = bookDAO.getAvailableBooks();
        bookComboBox.setItems(availableBooks);

        ObservableList<Visitor> activeVisitors = visitorDAO.getActiveVisitors();
        visitorComboBox.setItems(activeVisitors);
    }


    @FXML
    private void handleIssue() {
        if (!validateIssueInput()) {
            return;
        }

        Book book = bookComboBox.getValue();
        Visitor visitor = visitorComboBox.getValue();
        LocalDate loanDate = loanDatePicker.getValue();
        LocalDate dueDate = dueDatePicker.getValue();


        if (book.getCopiesAvailable() <= 0) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Книга недоступна для выдачи");
            return;
        }

        if (loanDAO.issueLoan(book.getBookId(), visitor.getVisitorId(),
                loanDate, dueDate, 1)) { // 1 - ID пользователя (временно)
            showAlert(Alert.AlertType.INFORMATION, "Успех",
                    "Книга \"" + book.getTitle() + "\" выдана читателю " + visitor.getFullName());
            loadLoans();
            loadBooksAndVisitors();
            clearIssueForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось выдать книгу");
        }
    }


    @FXML
    private void handleReturn() {
        if (selectedLoan == null) {
            showAlert(Alert.AlertType.WARNING, "Предупреждение", "Выберите выдачу для возврата");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Подтверждение возврата");
        confirmation.setHeaderText("Вернуть книгу?");
        confirmation.setContentText("Книга: " + selectedLoan.getBookTitle() + "\n" +
                "Читатель: " + selectedLoan.getVisitorName());

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            if (loanDAO.returnLoan(selectedLoan.getLoanId(), LocalDate.now())) {
                showAlert(Alert.AlertType.INFORMATION, "Успех", "Книга успешно возвращена");
                loadLoans();
                loadBooksAndVisitors();
            } else {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось вернуть книгу");
            }
        }
    }


    private void clearIssueForm() {
        bookComboBox.setValue(null);
        visitorComboBox.setValue(null);
        loanDatePicker.setValue(LocalDate.now());
        daysField.setText("14");
        dueDatePicker.setValue(LocalDate.now().plusDays(14));
    }


    private boolean validateIssueInput() {
        if (bookComboBox.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Выберите книгу");
            return false;
        }

        if (visitorComboBox.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Выберите читателя");
            return false;
        }

        if (loanDatePicker.getValue() == null || dueDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Укажите даты выдачи и возврата");
            return false;
        }

        if (dueDatePicker.getValue().isBefore(loanDatePicker.getValue())) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Дата возврата не может быть раньше даты выдачи");
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