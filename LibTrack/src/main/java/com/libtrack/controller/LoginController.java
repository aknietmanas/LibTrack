package com.libtrack.controller;

import com.libtrack.dao.UserDAO;
import com.libtrack.model.User;
import com.libtrack.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox rememberMeCheckbox;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    private UserDAO userDAO;

    @FXML
    public void initialize() {
        userDAO = new UserDAO();

        usernameField.setOnKeyPressed(this::handleKeyPress);
        passwordField.setOnKeyPressed(this::handleKeyPress);

        usernameField.textProperty().addListener((obs, old, newVal) -> hideError());
        passwordField.textProperty().addListener((obs, old, newVal) -> hideError());
    }

    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleLogin();
        }
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Валидация полей
        if (username.isEmpty() || password.isEmpty()) {
            showError("Пожалуйста, заполните все поля");
            return;
        }

        // Блокировка кнопки во время проверки
        loginButton.setDisable(true);

        try {
            // Аутентификация через UserDAO
            User user = userDAO.authenticate(username, password);

            if (user != null) {
                // Успешный вход - переход на главный экран
                SceneManager.showDashboard();
            } else {
                // Неудачный вход - показать ошибку
                showError("Неверный логин или пароль");
                passwordField.clear();
                passwordField.requestFocus();
            }

        } catch (Exception e) {
            showError("Ошибка подключения к базе данных");
            e.printStackTrace();
        } finally {
            loginButton.setDisable(false);
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        shakeNode(errorLabel);
    }

    private void hideError() {
        errorLabel.setVisible(false);
    }

    private void shakeNode(javafx.scene.Node node) {
        javafx.animation.TranslateTransition tt = new javafx.animation.TranslateTransition(
                javafx.util.Duration.millis(70), node
        );
        tt.setFromX(0);
        tt.setByX(10);
        tt.setCycleCount(4);
        tt.setAutoReverse(true);
        tt.play();
    }
}