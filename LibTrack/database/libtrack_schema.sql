CREATE DATABASE IF NOT EXISTS libtrack CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE libtrack;

CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('admin', 'librarian') DEFAULT 'librarian',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL
);

CREATE TABLE authors (
    author_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    biography TEXT,
    birth_year INT,
    country VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE books (
    book_id INT PRIMARY KEY AUTO_INCREMENT,
    isbn VARCHAR(20) UNIQUE,
    title VARCHAR(200) NOT NULL,
    author_id INT NOT NULL,
    genre VARCHAR(50),
    publisher VARCHAR(100),
    publication_year INT,
    pages INT,
    copies_total INT DEFAULT 1,
    copies_available INT DEFAULT 1,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES authors(author_id) ON DELETE CASCADE,
    INDEX idx_title (title),
    INDEX idx_genre (genre),
    INDEX idx_author (author_id)
);

CREATE TABLE visitors (
    visitor_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    address TEXT,
    registration_date DATE DEFAULT (CURRENT_DATE),
    birth_date DATE,
    status ENUM('active', 'blocked', 'inactive') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_status (status)
);

CREATE TABLE loans (
    loan_id INT PRIMARY KEY AUTO_INCREMENT,
    book_id INT NOT NULL,
    visitor_id INT NOT NULL,
    loan_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    status ENUM('active', 'returned', 'overdue') DEFAULT 'active',
    fine_amount DECIMAL(10, 2) DEFAULT 0.00,
    notes TEXT,
    issued_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
    FOREIGN KEY (visitor_id) REFERENCES visitors(visitor_id) ON DELETE CASCADE,
    FOREIGN KEY (issued_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_status (status),
    INDEX idx_loan_date (loan_date),
    INDEX idx_visitor (visitor_id),
    INDEX idx_book (book_id)
);

-- Триггер для уменьшения количества доступных книг при выдаче
DELIMITER //
CREATE TRIGGER after_loan_insert
AFTER INSERT ON loans
FOR EACH ROW
BEGIN
    IF NEW.status = 'active' THEN
        UPDATE books
        SET copies_available = copies_available - 1
        WHERE book_id = NEW.book_id;
    END IF;
END//

-- Триггер для увеличения количества доступных книг при возврате
CREATE TRIGGER after_loan_update
AFTER UPDATE ON loans
FOR EACH ROW
BEGIN
    IF OLD.status = 'active' AND NEW.status = 'returned' THEN
        UPDATE books
        SET copies_available = copies_available + 1
        WHERE book_id = NEW.book_id;
    END IF;
END//
DELIMITER ;

-- Пользователи с правильным BCrypt хешем (пароль: admin123)
INSERT INTO users (username, password_hash, full_name, role) VALUES
('admin', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIRq8S8/KG', 'Администратор Системы', 'admin'),
('librarian1', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIRq8S8/KG', 'Sovetbek Mansur', 'librarian');

-- Авторы
INSERT INTO authors (first_name, last_name, biography, birth_year, country) VALUES
('Фёдор', 'Достоевский', 'Великий русский писатель, мыслитель', 1821, 'Россия'),
('Лев', 'Толстой', 'Русский писатель, публицист, мыслитель', 1828, 'Россия'),
('Антон', 'Чехов', 'Русский писатель, драматург', 1860, 'Россия'),
('Александр', 'Пушкин', 'Величайший русский поэт', 1799, 'Россия'),
('Михаил', 'Булгаков', 'Русский писатель советского периода', 1891, 'Россия'),
('George', 'Orwell', 'English novelist and essayist', 1903, 'England'),
('J.K.', 'Rowling', 'British author and philanthropist', 1965, 'England'),
('Агата', 'Кристи', 'Английская писательница детективов', 1890, 'England');

-- Книги
INSERT INTO books (isbn, title, author_id, genre, publisher, publication_year, pages, copies_total, copies_available) VALUES
('978-5-17-123456-7', 'Преступление и наказание', 1, 'Роман', 'АСТ', 2020, 608, 3, 3),
('978-5-17-123457-4', 'Идиот', 1, 'Роман', 'АСТ', 2019, 640, 2, 2),
('978-5-17-123458-1', 'Война и мир', 2, 'Роман', 'Эксмо', 2018, 1300, 4, 4),
('978-5-17-123459-8', 'Анна Каренина', 2, 'Роман', 'Эксмо', 2019, 864, 2, 2),
('978-5-17-123460-4', 'Вишнёвый сад', 3, 'Пьеса', 'Азбука', 2020, 256, 2, 2),
('978-5-17-123461-1', 'Евгений Онегин', 4, 'Поэма', 'Азбука', 2021, 352, 3, 3),
('978-5-17-123462-8', 'Мастер и Маргарита', 5, 'Роман', 'АСТ', 2020, 512, 5, 5),
('978-0-14-103614-4', '1984', 6, 'Дистопиа', 'Penguin', 2003, 328, 3, 3),
('978-0-14-303943-3', 'Animal Farm', 6, 'Сатира', 'Penguin', 2008, 144, 2, 2),
('978-0-7475-3269-9', 'Harry Potter and the Philosophers Stone', 7, 'Фэнтези', 'Bloomsbury', 1997, 223, 4, 4),
('978-0-00-712243-6', 'Murder on the Orient Express', 8, 'Дэтектив', 'HarperCollins', 2010, 256, 2, 2);

-- Посетители
INSERT INTO visitors (first_name, last_name, email, phone, address, birth_date, status) VALUES
('Иван', 'Петров', 'ivan.petrov@email.com', '+7-777-123-4567', 'ул. Абая, д. 10, кв. 25', '1990-05-15', 'active'),
('Анна', 'Сидорова', 'anna.sidorova@email.com', '+7-777-234-5678', 'ул. Назарбаева, д. 45', '1985-08-22', 'active'),
('Петр', 'Смирнов', 'petr.smirnov@email.com', '+7-777-345-6789', 'пр. Республики, д. 78, кв. 12', '1995-03-10', 'active'),
('Мария', 'Кузнецова', 'maria.kuznetsova@email.com', '+7-777-456-7890', 'ул. Желтоксан, д. 33', '1988-11-30', 'active'),
('Дмитрий', 'Волков', 'dmitry.volkov@email.com', '+7-777-567-8901', 'ул. Сатпаева, д. 90А', '1992-07-18', 'active'),
('Екатерина', 'Новикова', 'ekaterina.novikova@email.com', '+7-777-678-9012', 'ул. Толе би, д. 56', '1993-02-14', 'active');

-- Выдачи книг
INSERT INTO loans (book_id, visitor_id, loan_date, due_date, status, issued_by) VALUES
(1, 1, '2024-11-15', '2024-12-15', 'active', 1),
(3, 2, '2024-11-20', '2024-12-20', 'active', 1),
(7, 3, '2024-10-10', '2024-11-10', 'returned', 2),
(10, 4, '2024-11-25', '2024-12-25', 'active', 1);

-- Обновление return_date для возвращённой книги
UPDATE loans SET return_date = '2024-11-08' WHERE loan_id = 3;

-- Представления (Views)

-- Популярные книги
CREATE VIEW v_popular_books AS
SELECT
    b.book_id,
    b.title,
    CONCAT(a.first_name, ' ', a.last_name) AS author_name,
    COUNT(l.loan_id) AS loan_count
FROM books b
JOIN authors a ON b.author_id = a.author_id
LEFT JOIN loans l ON b.book_id = l.book_id
GROUP BY b.book_id, b.title, author_name
ORDER BY loan_count DESC;

-- Активные читатели
CREATE VIEW v_active_visitors AS
SELECT
    v.visitor_id,
    CONCAT(v.first_name, ' ', v.last_name) AS visitor_name,
    v.email,
    COUNT(l.loan_id) AS total_loans,
    SUM(CASE WHEN l.status = 'active' THEN 1 ELSE 0 END) AS active_loans
FROM visitors v
LEFT JOIN loans l ON v.visitor_id = l.visitor_id
GROUP BY v.visitor_id, visitor_name, v.email
ORDER BY total_loans DESC;

-- Статистика по месяцам
CREATE VIEW v_monthly_statistics AS
SELECT
    DATE_FORMAT(loan_date, '%Y-%m') AS month,
    COUNT(*) AS loans_count,
    COUNT(DISTINCT visitor_id) AS unique_visitors
FROM loans
GROUP BY month
ORDER BY month DESC;