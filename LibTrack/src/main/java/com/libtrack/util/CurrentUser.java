package com.libtrack.util;

import com.libtrack.model.User;

/**
 * Singleton для хранения информации о текущем пользователе
 */
public class CurrentUser {

    private static CurrentUser instance;
    private User user;

    private CurrentUser() {
    }

    /**
     * Получить экземпляр CurrentUser
     */
    public static CurrentUser getInstance() {
        if (instance == null) {
            instance = new CurrentUser();
        }
        return instance;
    }

    /**
     * Установить текущего пользователя
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Получить текущего пользователя
     */
    public User getUser() {
        return user;
    }

    /**
     * Проверка, авторизован ли пользователь
     */
    public boolean isLoggedIn() {
        return user != null;
    }

    /**
     * Проверка, является ли пользователь администратором
     */
    public boolean isAdmin() {
        return user != null && user.isAdmin();
    }

    /**
     * Получить ID текущего пользователя
     */
    public int getUserId() {
        return user != null ? user.getUserId() : 0;
    }

    /**
     * Получить имя текущего пользователя
     */
    public String getFullName() {
        return user != null ? user.getFullName() : "Гость";
    }

    /**
     * Получить логин текущего пользователя
     */
    public String getUsername() {
        return user != null ? user.getUsername() : "";
    }

    /**
     * Выход пользователя
     */
    public void logout() {
        this.user = null;
    }

    /**
     * Очистить сессию
     */
    public static void clear() {
        if (instance != null) {
            instance.logout();
        }
    }
}