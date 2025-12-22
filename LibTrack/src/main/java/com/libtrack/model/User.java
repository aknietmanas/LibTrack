package com.libtrack.model;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class User {

    private final IntegerProperty userId;
    private final StringProperty username;
    private final StringProperty passwordHash;
    private final StringProperty fullName;
    private final StringProperty role;
    private final ObjectProperty<LocalDateTime> createdAt;
    private final ObjectProperty<LocalDateTime> lastLogin;

    public User() {
        this(0, "", "", "", "librarian", null, null);
    }

    public User(int userId, String username, String passwordHash,
                String fullName, String role,
                LocalDateTime createdAt, LocalDateTime lastLogin) {
        this.userId = new SimpleIntegerProperty(userId);
        this.username = new SimpleStringProperty(username);
        this.passwordHash = new SimpleStringProperty(passwordHash);
        this.fullName = new SimpleStringProperty(fullName);
        this.role = new SimpleStringProperty(role);
        this.createdAt = new SimpleObjectProperty<>(createdAt);
        this.lastLogin = new SimpleObjectProperty<>(lastLogin);
    }

    public int getUserId() { return userId.get(); }
    public void setUserId(int value) { userId.set(value); }
    public IntegerProperty userIdProperty() { return userId; }

    public String getUsername() { return username.get(); }
    public void setUsername(String value) { username.set(value); }
    public StringProperty usernameProperty() { return username; }

    public String getPasswordHash() { return passwordHash.get(); }
    public void setPasswordHash(String value) { passwordHash.set(value); }
    public StringProperty passwordHashProperty() { return passwordHash; }

    public String getFullName() { return fullName.get(); }
    public void setFullName(String value) { fullName.set(value); }
    public StringProperty fullNameProperty() { return fullName; }

    public String getRole() { return role.get(); }
    public void setRole(String value) { role.set(value); }
    public StringProperty roleProperty() { return role; }

    public LocalDateTime getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(LocalDateTime value) { createdAt.set(value); }
    public ObjectProperty<LocalDateTime> createdAtProperty() { return createdAt; }

    public LocalDateTime getLastLogin() { return lastLogin.get(); }
    public void setLastLogin(LocalDateTime value) { lastLogin.set(value); }
    public ObjectProperty<LocalDateTime> lastLoginProperty() { return lastLogin; }

    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(role.get());
    }

    @Override
    public String toString() {
        return fullName.get() + " (" + username.get() + ")";
    }
}
