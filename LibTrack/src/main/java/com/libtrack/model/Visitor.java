package com.libtrack.model;

import javafx.beans.property.*;
import java.time.LocalDate;

/**
 * Модель посетителя (читателя)
 */
public class Visitor {

    private final IntegerProperty visitorId;
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty email;
    private final StringProperty phone;
    private final StringProperty address;
    private final ObjectProperty<LocalDate> registrationDate;
    private final ObjectProperty<LocalDate> birthDate;
    private final StringProperty status;

    public Visitor() {
        this(0, "", "", "", "", "", null, null, "active");
    }

    public Visitor(int visitorId, String firstName, String lastName,
                   String email, String phone, String address,
                   LocalDate registrationDate, LocalDate birthDate, String status) {
        this.visitorId = new SimpleIntegerProperty(visitorId);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.email = new SimpleStringProperty(email);
        this.phone = new SimpleStringProperty(phone);
        this.address = new SimpleStringProperty(address);
        this.registrationDate = new SimpleObjectProperty<>(registrationDate);
        this.birthDate = new SimpleObjectProperty<>(birthDate);
        this.status = new SimpleStringProperty(status);
    }

    // Геттеры и сеттеры

    public int getVisitorId() { return visitorId.get(); }
    public void setVisitorId(int value) { visitorId.set(value); }
    public IntegerProperty visitorIdProperty() { return visitorId; }

    public String getFirstName() { return firstName.get(); }
    public void setFirstName(String value) { firstName.set(value); }
    public StringProperty firstNameProperty() { return firstName; }

    public String getLastName() { return lastName.get(); }
    public void setLastName(String value) { lastName.set(value); }
    public StringProperty lastNameProperty() { return lastName; }

    public String getEmail() { return email.get(); }
    public void setEmail(String value) { email.set(value); }
    public StringProperty emailProperty() { return email; }

    public String getPhone() { return phone.get(); }
    public void setPhone(String value) { phone.set(value); }
    public StringProperty phoneProperty() { return phone; }

    public String getAddress() { return address.get(); }
    public void setAddress(String value) { address.set(value); }
    public StringProperty addressProperty() { return address; }

    public LocalDate getRegistrationDate() { return registrationDate.get(); }
    public void setRegistrationDate(LocalDate value) { registrationDate.set(value); }
    public ObjectProperty<LocalDate> registrationDateProperty() { return registrationDate; }

    public LocalDate getBirthDate() { return birthDate.get(); }
    public void setBirthDate(LocalDate value) { birthDate.set(value); }
    public ObjectProperty<LocalDate> birthDateProperty() { return birthDate; }

    public String getStatus() { return status.get(); }
    public void setStatus(String value) { status.set(value); }
    public StringProperty statusProperty() { return status; }

    /**
     * Полное имя посетителя
     */
    public String getFullName() {
        return firstName.get() + " " + lastName.get();
    }

    /**
     * Проверка активности
     */
    public boolean isActive() {
        return "active".equalsIgnoreCase(status.get());
    }

    @Override
    public String toString() {
        return getFullName() + " (" + email.get() + ")";
    }
}