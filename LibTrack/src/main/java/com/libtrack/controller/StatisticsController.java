package com.libtrack.controller;

import com.libtrack.service.StatisticsService;
import com.libtrack.service.StatisticsService.StatData;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import java.util.Map;

/**
 * Контроллер статистики и аналитики
 */
public class StatisticsController {

    // Метрики
    @FXML private Label totalBooksLabel;
    @FXML private Label totalCopiesLabel;
    @FXML private Label availableCopiesLabel;
    @FXML private Label totalAuthorsLabel;
    @FXML private Label totalVisitorsLabel;
    @FXML private Label activeVisitorsLabel;
    @FXML private Label activeLoansLabel;
    @FXML private Label overdueLoansLabel;
    @FXML private Label totalLoansLabel;
    @FXML private Label returnedLoansLabel;

    // Графики
    @FXML private PieChart genreChart;
    @FXML private BarChart<String, Number> popularBooksChart;
    @FXML private LineChart<String, Number> loansTimelineChart;
    @FXML private BarChart<String, Number> activeReadersChart;
    @FXML private PieChart returnStatsChart;

    private StatisticsService statisticsService;

    @FXML
    public void initialize() {
        statisticsService = new StatisticsService();

        loadGeneralStatistics();
        loadGenreChart();
        loadPopularBooksChart();
        loadLoansTimelineChart();
        loadActiveReadersChart();
        loadReturnStatsChart();
    }

    /**
     * Загрузить общую статистику
     */
    private void loadGeneralStatistics() {
        Map<String, Integer> stats = statisticsService.getGeneralStatistics();

        totalBooksLabel.setText(String.valueOf(stats.getOrDefault("totalBooks", 0)));
        totalCopiesLabel.setText(String.valueOf(stats.getOrDefault("totalCopies", 0)));
        availableCopiesLabel.setText(String.valueOf(stats.getOrDefault("availableCopies", 0)));
        totalAuthorsLabel.setText(String.valueOf(stats.getOrDefault("totalAuthors", 0)));
        totalVisitorsLabel.setText(String.valueOf(stats.getOrDefault("totalVisitors", 0)));
        activeVisitorsLabel.setText(String.valueOf(stats.getOrDefault("activeVisitors", 0)));
        activeLoansLabel.setText(String.valueOf(stats.getOrDefault("activeLoans", 0)));
        overdueLoansLabel.setText(String.valueOf(stats.getOrDefault("overdueLoans", 0)));
        totalLoansLabel.setText(String.valueOf(stats.getOrDefault("totalLoans", 0)));
        returnedLoansLabel.setText(String.valueOf(stats.getOrDefault("returnedLoans", 0)));
    }

    /**
     * Загрузить диаграмму по жанрам
     */
    private void loadGenreChart() {
        ObservableList<StatData> data = statisticsService.getBooksByGenre();

        genreChart.getData().clear();

        for (StatData item : data) {
            PieChart.Data slice = new PieChart.Data(
                    item.getLabel() + " (" + item.getValue() + ")",
                    item.getValue()
            );
            genreChart.getData().add(slice);
        }

        genreChart.setTitle("Распределение книг по жанрам");
        genreChart.setLegendVisible(true);
    }

    /**
     * Загрузить диаграмму популярных книг
     */
    private void loadPopularBooksChart() {
        ObservableList<StatData> data = statisticsService.getPopularBooks();

        popularBooksChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Количество выдач");

        for (StatData item : data) {
            // Обрезаем длинные названия
            String title = item.getLabel().length() > 30 ?
                    item.getLabel().substring(0, 30) + "..." : item.getLabel();
            series.getData().add(new XYChart.Data<>(title, item.getValue()));
        }

        popularBooksChart.getData().add(series);
        popularBooksChart.setTitle("Топ-10 популярных книг");
        popularBooksChart.setLegendVisible(false);
    }

    /**
     * Загрузить график выдач по месяцам
     */
    private void loadLoansTimelineChart() {
        ObservableList<StatData> data = statisticsService.getLoansByMonth();

        loansTimelineChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Выдачи");

        for (StatData item : data) {
            series.getData().add(new XYChart.Data<>(item.getLabel(), item.getValue()));
        }

        loansTimelineChart.getData().add(series);
        loansTimelineChart.setTitle("Динамика выдач за последние 12 месяцев");
        loansTimelineChart.setLegendVisible(true);
    }

    /**
     * Загрузить диаграмму активных читателей
     */
    private void loadActiveReadersChart() {
        ObservableList<StatData> data = statisticsService.getMostActiveVisitors();

        activeReadersChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Количество взятых книг");

        for (StatData item : data) {
            series.getData().add(new XYChart.Data<>(item.getLabel(), item.getValue()));
        }

        activeReadersChart.getData().add(series);
        activeReadersChart.setTitle("Топ-10 активных читателей");
        activeReadersChart.setLegendVisible(false);
    }

    /**
     * Загрузить статистику возвратов
     */
    private void loadReturnStatsChart() {
        Map<String, Integer> stats = statisticsService.getReturnStatistics();

        returnStatsChart.getData().clear();

        int onTime = stats.getOrDefault("onTime", 0);
        int late = stats.getOrDefault("late", 0);
        int currentOverdue = stats.getOrDefault("currentOverdue", 0);

        if (onTime > 0) {
            returnStatsChart.getData().add(new PieChart.Data("Вовремя (" + onTime + ")", onTime));
        }
        if (late > 0) {
            returnStatsChart.getData().add(new PieChart.Data("С опозданием (" + late + ")", late));
        }
        if (currentOverdue > 0) {
            returnStatsChart.getData().add(new PieChart.Data("Текущие просрочки (" + currentOverdue + ")", currentOverdue));
        }

        returnStatsChart.setTitle("Статистика возвратов");
        returnStatsChart.setLegendVisible(true);
    }

    /**
     * Обновить все графики
     */
    @FXML
    private void handleRefresh() {
        loadGeneralStatistics();
        loadGenreChart();
        loadPopularBooksChart();
        loadLoansTimelineChart();
        loadActiveReadersChart();
        loadReturnStatsChart();
    }
}