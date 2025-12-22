package com.libtrack.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

/**
 * Утилитный класс для работы с диалоговыми окнами
 */
public class AlertHelper {

    /**
     * Показать информационное сообщение
     */
    public static void showInfo(String title, String content) {
        showAlert(Alert.AlertType.INFORMATION, title, content);
    }

    /**
     * Показать сообщение об ошибке
     */
    public static void showError(String title, String content) {
        showAlert(Alert.AlertType.ERROR, title, content);
    }

    /**
     * Показать предупреждение
     */
    public static void showWarning(String title, String content) {
        showAlert(Alert.AlertType.WARNING, title, content);
    }

    /**
     * Показать диалог подтверждения
     * @return true если пользователь нажал OK
     */
    public static boolean showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Показать сообщение об успешной операции
     */
    public static void showSuccess(String operation) {
        showInfo("Успех", operation + " выполнено успешно");
    }

    /**
     * Показать сообщение о неудачной операции
     */
    public static void showFailure(String operation) {
        showError("Ошибка", "Не удалось выполнить: " + operation);
    }

    /**
     * Показать сообщение о необходимости выбора элемента
     */
    public static void showSelectionRequired(String itemType) {
        showWarning("Предупреждение", "Выберите " + itemType + " из таблицы");
    }

    /**
     * Показать сообщение о необходимости заполнения поля
     */
    public static void showFieldRequired(String fieldName) {
        showError("Ошибка валидации", "Поле \"" + fieldName + "\" обязательно для заполнения");
    }

    /**
     * Показать диалог подтверждения удаления
     */
    public static boolean confirmDelete(String itemName) {
        return showConfirmation(
                "Подтверждение удаления",
                "Вы уверены, что хотите удалить?",
                "Элемент: " + itemName + "\n\nЭто действие нельзя отменить."
        );
    }

    /**
     * Базовый метод для показа Alert
     */
    private static void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Показать Alert с пользовательским заголовком
     */
    public static void showAlertWithHeader(Alert.AlertType type, String title,
                                           String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}