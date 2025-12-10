package com.libtrack.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//Класс для управления подключением к базе данных
public class DatabaseConnection {


    private static final String URL = "jdbc:mysql://localhost:3306/libtrack?useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Masek040407";

    private static Connection connection = null;


    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {

                Class.forName("com.mysql.cj.jdbc.Driver");


                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

                System.out.println("Успешное подключение к базе данных MySQL");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Драйвер MySQL не найден!");
            System.err.println("Убедитесь, что в pom.xml есть зависимость mysql-connector-j");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к БД: " + e.getMessage());
            System.err.println("Проверьте:");

            e.printStackTrace();
        }
        return connection;
    }


    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Соединение с БД закрыто");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при закрытии соединения: " + e.getMessage());
        }
    }
}