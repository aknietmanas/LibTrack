-- SQL скрипт для создания базы данных
-- Создание схемы (только для MySQL)
CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'user'
);

CREATE TABLE IF NOT EXISTS authors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    country VARCHAR(255),
    birth_year INT
);

CREATE TABLE IF NOT EXISTS books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author_id INT NOT NULL,
    genre VARCHAR(100),
    year INT,
    available BOOLEAN DEFAULT TRUE,

    CONSTRAINT fk_books_authors
        FOREIGN KEY (author_id) REFERENCES authors(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS visitors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS borrowings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    visitor_id INT NOT NULL,
    borrow_date DATE NOT NULL,
    return_date DATE NULL,

    CONSTRAINT fk_borrowings_books
        FOREIGN KEY (book_id) REFERENCES books(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_borrowings_visitors
        FOREIGN KEY (visitor_id) REFERENCES visitors(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
