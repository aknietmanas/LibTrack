package com.libtrack.service;

import com.libtrack.dao.LoanDAO;
import com.libtrack.dao.VisitorDAO;
import com.libtrack.model.Loan;
import com.libtrack.model.Visitor;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

/**
 * Сервис для работы с посетителями (бизнес-логика)
 */
public class VisitorService {

    private final VisitorDAO visitorDAO;
    private final LoanDAO loanDAO;

    // Регулярное выражение для валидации email
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    // Регулярное выражение для валидации телефона
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[+]?[0-9]{10,15}$"
    );

    // Минимальный возраст для регистрации
    private static final int MIN_AGE = 6;

    public VisitorService() {
        this.visitorDAO = new VisitorDAO();
        this.loanDAO = new LoanDAO();
    }

    /**
     * Получить всех посетителей
     */
    public ObservableList<Visitor> getAllVisitors() {
        return visitorDAO.getAllVisitors();
    }

    /**
     * Получить посетителя по ID
     */
    public Visitor getVisitorById(int visitorId) {
        return visitorDAO.getVisitorById(visitorId);
    }

    /**
     * Добавить посетителя с валидацией
     */
    public boolean addVisitor(Visitor visitor) {
        // Валидация имени
        if (visitor.getFirstName() == null || visitor.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("Имя читателя обязательно");
        }

        // Валидация фамилии
        if (visitor.getLastName() == null || visitor.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Фамилия читателя обязательна");
        }

        // Валидация email
        if (visitor.getEmail() != null && !visitor.getEmail().trim().isEmpty()) {
            if (!isValidEmail(visitor.getEmail())) {
                throw new IllegalArgumentException("Некорректный формат email");
            }

            // Проверка уникальности email
            if (isEmailExists(visitor.getEmail(), visitor.getVisitorId())) {
                throw new IllegalStateException("Читатель с таким email уже зарегистрирован");
            }
        }

        // Валидация телефона
        if (visitor.getPhone() != null && !visitor.getPhone().trim().isEmpty()) {
            String cleanPhone = visitor.getPhone().replaceAll("[\\s-()]", "");
            if (!isValidPhone(cleanPhone)) {
                throw new IllegalArgumentException("Некорректный формат телефона");
            }
        }

        // Валидация возраста
        if (visitor.getBirthDate() != null) {
            int age = calculateAge(visitor.getBirthDate());
            if (age < MIN_AGE) {
                throw new IllegalArgumentException(
                        "Минимальный возраст для регистрации: " + MIN_AGE + " лет"
                );
            }
            if (age > 120) {
                throw new IllegalArgumentException("Некорректная дата рождения");
            }
        }

        // Установить дату регистрации
        if (visitor.getRegistrationDate() == null) {
            visitor.setRegistrationDate(LocalDate.now());
        }

        // Установить статус по умолчанию
        if (visitor.getStatus() == null || visitor.getStatus().trim().isEmpty()) {
            visitor.setStatus("active");
        }

        return visitorDAO.addVisitor(visitor);
    }

    /**
     * Обновить посетителя
     */
    public boolean updateVisitor(Visitor visitor) {
        if (visitor.getVisitorId() <= 0) {
            throw new IllegalArgumentException("Некорректный ID читателя");
        }

        // Та же валидация что и при добавлении
        if (visitor.getFirstName() == null || visitor.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("Имя читателя обязательно");
        }

        if (visitor.getLastName() == null || visitor.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Фамилия читателя обязательна");
        }

        if (visitor.getEmail() != null && !visitor.getEmail().trim().isEmpty()) {
            if (!isValidEmail(visitor.getEmail())) {
                throw new IllegalArgumentException("Некорректный формат email");
            }
            if (isEmailExists(visitor.getEmail(), visitor.getVisitorId())) {
                throw new IllegalStateException("Читатель с таким email уже существует");
            }
        }

        return visitorDAO.updateVisitor(visitor);
    }

    /**
     * Удалить посетителя
     */
    public boolean deleteVisitor(int visitorId) {
        // Проверка: есть ли активные выдачи
        if (hasActiveLoans(visitorId)) {
            throw new IllegalStateException(
                    "Невозможно удалить читателя: есть активные выдачи книг. " +
                            "Сначала верните все книги."
            );
        }

        return visitorDAO.deleteVisitor(visitorId);
    }

    /**
     * Поиск посетителей
     */
    public ObservableList<Visitor> searchVisitors(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllVisitors();
        }
        return visitorDAO.searchVisitors(keyword);
    }

    /**
     * Получить активных посетителей
     */
    public ObservableList<Visitor> getActiveVisitors() {
        return visitorDAO.getActiveVisitors();
    }

    /**
     * Заблокировать посетителя
     */
    public boolean blockVisitor(int visitorId, String reason) {
        Visitor visitor = visitorDAO.getVisitorById(visitorId);
        if (visitor == null) {
            throw new IllegalArgumentException("Читатель не найден");
        }

        visitor.setStatus("blocked");

        // Можно добавить логирование причины блокировки
        System.out.println("Читатель " + visitor.getFullName() +
                " заблокирован. Причина: " + reason);

        return visitorDAO.updateVisitor(visitor);
    }

    /**
     * Разблокировать посетителя
     */
    public boolean unblockVisitor(int visitorId) {
        Visitor visitor = visitorDAO.getVisitorById(visitorId);
        if (visitor == null) {
            throw new IllegalArgumentException("Читатель не найден");
        }

        visitor.setStatus("active");
        return visitorDAO.updateVisitor(visitor);
    }

    /**
     * Проверить, есть ли у посетителя активные выдачи
     */
    public boolean hasActiveLoans(int visitorId) {
        ObservableList<Loan> loans = loanDAO.getLoansByVisitor(visitorId);
        return loans.stream()
                .anyMatch(loan -> "active".equals(loan.getStatus()));
    }

    /**
     * Получить количество активных выдач посетителя
     */
    public int getActiveLoansCount(int visitorId) {
        ObservableList<Loan> loans = loanDAO.getLoansByVisitor(visitorId);
        return (int) loans.stream()
                .filter(loan -> "active".equals(loan.getStatus()))
                .count();
    }

    /**
     * Получить общее количество книг, взятых посетителем за всё время
     */
    public int getTotalLoansCount(int visitorId) {
        ObservableList<Loan> loans = loanDAO.getLoansByVisitor(visitorId);
        return loans.size();
    }

    /**
     * Проверить, может ли посетитель брать книги
     */
    public boolean canTakeBooks(int visitorId) {
        Visitor visitor = visitorDAO.getVisitorById(visitorId);
        if (visitor == null) {
            return false;
        }

        // Проверка статуса
        if (!"active".equals(visitor.getStatus())) {
            return false;
        }

        // Проверка количества активных выдач (максимум 5)
        return getActiveLoansCount(visitorId) < 5;
    }

    /**
     * Валидация email
     */
    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Валидация телефона
     */
    private boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Проверка существования email
     */
    private boolean isEmailExists(String email, int excludeVisitorId) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        ObservableList<Visitor> allVisitors = visitorDAO.getAllVisitors();
        return allVisitors.stream()
                .filter(v -> v.getVisitorId() != excludeVisitorId)
                .anyMatch(v -> email.equalsIgnoreCase(v.getEmail()));
    }

    /**
     * Рассчитать возраст
     */
    private int calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * Получить возраст посетителя
     */
    public int getAge(int visitorId) {
        Visitor visitor = visitorDAO.getVisitorById(visitorId);
        if (visitor == null || visitor.getBirthDate() == null) {
            return 0;
        }
        return calculateAge(visitor.getBirthDate());
    }

    /**
     * Получить самых активных читателей
     */
    public ObservableList<Visitor> getMostActiveVisitors(int limit) {
        ObservableList<Visitor> visitors = getAllVisitors();

        // Сортировка по количеству взятых книг
        visitors.sort((v1, v2) -> {
            int count1 = getTotalLoansCount(v1.getVisitorId());
            int count2 = getTotalLoansCount(v2.getVisitorId());
            return Integer.compare(count2, count1);
        });

        if (visitors.size() > limit) {
            return javafx.collections.FXCollections.observableArrayList(
                    visitors.subList(0, limit)
            );
        }

        return visitors;
    }
}