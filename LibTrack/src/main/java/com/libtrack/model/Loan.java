package com.libtrack.model;

import javafx.beans.property.*;
import java.time.LocalDate;

/**
 * Модель выдачи книги
 */
public class Loan {

    private final IntegerProperty loanId;
    private final IntegerProperty bookId;
    private final IntegerProperty visitorId;
    private final ObjectProperty<LocalDate> loanDate;
    private final ObjectProperty<LocalDate> dueDate;
    private final ObjectProperty<LocalDate> returnDate;
    private final StringProperty status;
    private final DoubleProperty fineAmount;
    private final StringProperty notes;

    // Дополнительные поля для отображения
    private final StringProperty bookTitle;
    private final StringProperty visitorName;

    public Loan() {
        this(0, 0, 0, null, null, null, "active", 0.0, "", "", "");
    }

    public Loan(int loanId, int bookId, int visitorId,
                LocalDate loanDate, LocalDate dueDate, LocalDate returnDate,
                String status, double fineAmount, String notes,
                String bookTitle, String visitorName) {
        this.loanId = new SimpleIntegerProperty(loanId);
        this.bookId = new SimpleIntegerProperty(bookId);
        this.visitorId = new SimpleIntegerProperty(visitorId);
        this.loanDate = new SimpleObjectProperty<>(loanDate);
        this.dueDate = new SimpleObjectProperty<>(dueDate);
        this.returnDate = new SimpleObjectProperty<>(returnDate);
        this.status = new SimpleStringProperty(status);
        this.fineAmount = new SimpleDoubleProperty(fineAmount);
        this.notes = new SimpleStringProperty(notes);
        this.bookTitle = new SimpleStringProperty(bookTitle);
        this.visitorName = new SimpleStringProperty(visitorName);
    }

    // Геттеры и сеттеры

    public int getLoanId() { return loanId.get(); }
    public void setLoanId(int value) { loanId.set(value); }
    public IntegerProperty loanIdProperty() { return loanId; }

    public int getBookId() { return bookId.get(); }
    public void setBookId(int value) { bookId.set(value); }
    public IntegerProperty bookIdProperty() { return bookId; }

    public int getVisitorId() { return visitorId.get(); }
    public void setVisitorId(int value) { visitorId.set(value); }
    public IntegerProperty visitorIdProperty() { return visitorId; }

    public LocalDate getLoanDate() { return loanDate.get(); }
    public void setLoanDate(LocalDate value) { loanDate.set(value); }
    public ObjectProperty<LocalDate> loanDateProperty() { return loanDate; }

    public LocalDate getDueDate() { return dueDate.get(); }
    public void setDueDate(LocalDate value) { dueDate.set(value); }
    public ObjectProperty<LocalDate> dueDateProperty() { return dueDate; }

    public LocalDate getReturnDate() { return returnDate.get(); }
    public void setReturnDate(LocalDate value) { returnDate.set(value); }
    public ObjectProperty<LocalDate> returnDateProperty() { return returnDate; }

    public String getStatus() { return status.get(); }
    public void setStatus(String value) { status.set(value); }
    public StringProperty statusProperty() { return status; }

    public double getFineAmount() { return fineAmount.get(); }
    public void setFineAmount(double value) { fineAmount.set(value); }
    public DoubleProperty fineAmountProperty() { return fineAmount; }

    public String getNotes() { return notes.get(); }
    public void setNotes(String value) { notes.set(value); }
    public StringProperty notesProperty() { return notes; }

    public String getBookTitle() { return bookTitle.get(); }
    public void setBookTitle(String value) { bookTitle.set(value); }
    public StringProperty bookTitleProperty() { return bookTitle; }

    public String getVisitorName() { return visitorName.get(); }
    public void setVisitorName(String value) { visitorName.set(value); }
    public StringProperty visitorNameProperty() { return visitorName; }

    /**
     * Проверка просрочки
     */
    public boolean isOverdue() {
        if (returnDate.get() != null) {
            return false; // Уже возвращено
        }
        LocalDate due = dueDate.get();
        return due != null && LocalDate.now().isAfter(due);
    }

    /**
     * Количество дней просрочки
     */
    public long getOverdueDays() {
        if (!isOverdue()) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(dueDate.get(), LocalDate.now());
    }

    /**
     * Статус для отображения
     */
    public String getDisplayStatus() {
        if ("returned".equals(status.get())) {
            return "Возвращена";
        } else if (isOverdue()) {
            return "Просрочена";
        } else {
            return "Активна";
        }
    }

    @Override
    public String toString() {
        return bookTitle.get() + " - " + visitorName.get() + " (" + getDisplayStatus() + ")";
    }
}