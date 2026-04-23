package com.mycompany.bookloanandreturn.Controller;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.bookloanandreturn.DatabaseConnection;
import com.mycompany.bookloanandreturn.Models.FineCollectionReport;
import com.mycompany.bookloanandreturn.Models.MonthlyStatistics;
import com.mycompany.bookloanandreturn.Models.OverdueBookReport;
import com.mycompany.bookloanandreturn.View.ReportsView;
import com.mycompany.bookloanandreturn.util.ExcelExport;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Reports {
    private final ReportsView view;
    private final Runnable onReturnMenu;

    private List<OverdueBookReport> currentOverdueData = new ArrayList<>();
    private List<MonthlyStatistics> currentMonthlyData = new ArrayList<>();
    private List<FineCollectionReport> currentFineData = new ArrayList<>();

    private String currentReportType = null;

    public Reports(Stage stage, Runnable onReturnMenu) {
        this.view = new ReportsView(stage);
        this.onReturnMenu = onReturnMenu;

        view.addBackListener(this::goBack);
        view.addReportTypeListener(this::loadReport);
        view.addExportListener(this::exportCurrentReport);
        view.addRefreshListener(this::refreshCurrentReport);

        view.show();
    }

    private void goBack() {
        if (onReturnMenu != null) {
            onReturnMenu.run();
        }
    }

    private void refreshCurrentReport() {
        if (currentReportType != null) {
            loadReport(currentReportType);
        }
    }

    private void loadReport(String reportType) {
        currentReportType = reportType;

        switch (reportType) {
            case "Overdue Books" -> loadOverdueBooks();
            case "Monthly Statistics" -> loadMonthlyStatistics();
            case "Fine Collection" -> loadFineCollection();
        }
    }

    private void loadOverdueBooks() {
        view.setupOverdueBooksColumns();
        currentOverdueData.clear();

        String sql = """
            SELECT l.loan_id, b.bookName, l.borrower_name, l.student_id,
                   s.full_name AS student_name, s.phone, l.loan_date, l.due_date,
                   DATEDIFF(?, l.due_date) AS days_overdue,
                   GREATEST(0, DATEDIFF(?, l.due_date) * 10) AS estimated_fine
            FROM loan l
            INNER JOIN book b ON b.book_id = l.book_id
            LEFT JOIN book_return r ON r.loan_id = l.loan_id
            LEFT JOIN student s ON s.student_id = l.student_id
            WHERE r.return_id IS NULL AND l.due_date < ?
            ORDER BY days_overdue DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            LocalDate today = LocalDate.now();
            ps.setDate(1, Date.valueOf(today));
            ps.setDate(2, Date.valueOf(today));
            ps.setDate(3, Date.valueOf(today));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OverdueBookReport r = new OverdueBookReport();
                    r.setLoanId(rs.getInt("loan_id"));
                    r.setBookTitle(rs.getString("bookName"));
                    r.setBorrowerName(rs.getString("borrower_name"));
                    r.setStudentId(rs.getLong("student_id"));
                    r.setStudentName(rs.getString("student_name"));
                    r.setPhone(rs.getString("phone"));
                    r.setLoanDate(rs.getDate("loan_date").toLocalDate().toString());
                    r.setDueDate(rs.getDate("due_date").toLocalDate().toString());
                    r.setDaysOverdue(rs.getInt("days_overdue"));
                    r.setEstimatedFine(rs.getInt("estimated_fine"));
                    currentOverdueData.add(r);
                }
            }

            view.displayData(currentOverdueData);

            int totalFine = currentOverdueData.stream().mapToInt(OverdueBookReport::getEstimatedFine).sum();
            view.setSummary(String.format("Total overdue books: %d | Total estimated fines: \u20B1%d",
                currentOverdueData.size(), totalFine));

        } catch (SQLException ex) {
            view.showError(dbMessage(ex));
        }
    }

    private void loadMonthlyStatistics() {
        view.setupMonthlyStatsColumns();
        currentMonthlyData.clear();

        String sql = """
            SELECT
                YEAR(l.loan_date) AS year,
                MONTH(l.loan_date) AS month,
                MONTHNAME(l.loan_date) AS month_name,
                COUNT(l.loan_id) AS loans_count,
                COUNT(r.return_id) AS returns_count,
                SUM(CASE WHEN r.return_date > l.due_date THEN 1 ELSE 0 END) AS overdue_returns,
                SUM(COALESCE(r.fine_pesos, 0)) AS total_fines
            FROM loan l
            LEFT JOIN book_return r ON r.loan_id = l.loan_id
            WHERE l.loan_date >= DATE_SUB(?, INTERVAL 12 MONTH)
            GROUP BY YEAR(l.loan_date), MONTH(l.loan_date), MONTHNAME(l.loan_date)
            ORDER BY year DESC, month DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(LocalDate.now()));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MonthlyStatistics s = new MonthlyStatistics();
                    s.setYear(rs.getInt("year"));
                    s.setMonth(rs.getString("month_name"));
                    s.setLoansCount(rs.getInt("loans_count"));
                    s.setReturnsCount(rs.getInt("returns_count"));
                    s.setOverdueReturns(rs.getInt("overdue_returns"));
                    s.setTotalFinesCollected(rs.getInt("total_fines"));
                    currentMonthlyData.add(s);
                }
            }

            view.displayData(currentMonthlyData);

            int totalLoans = currentMonthlyData.stream().mapToInt(MonthlyStatistics::getLoansCount).sum();
            int totalFines = currentMonthlyData.stream().mapToInt(MonthlyStatistics::getTotalFinesCollected).sum();
            view.setSummary(String.format("Total loans (last 12 months): %d | Total fines collected: \u20B1%d",
                totalLoans, totalFines));

        } catch (SQLException ex) {
            view.showError(dbMessage(ex));
        }
    }

    private void loadFineCollection() {
        view.setupFineCollectionColumns();
        currentFineData.clear();

        String sql = """
            SELECT l.loan_id, b.bookName, l.borrower_name, r.return_date,
                   r.fine_pesos, r.fine_paid
            FROM book_return r
            INNER JOIN loan l ON l.loan_id = r.loan_id
            INNER JOIN book b ON b.book_id = l.book_id
            WHERE r.fine_pesos > 0
            ORDER BY r.return_date DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                FineCollectionReport r = new FineCollectionReport();
                r.setLoanId(rs.getInt("loan_id"));
                r.setBookTitle(rs.getString("bookName"));
                r.setBorrowerName(rs.getString("borrower_name"));
                r.setReturnDate(rs.getDate("return_date").toLocalDate().toString());
                r.setFineAmount(rs.getInt("fine_pesos"));
                r.setFinePaid(rs.getBoolean("fine_paid"));
                r.setPaymentStatus(r.isFinePaid() ? "Paid" : "Unpaid");
                currentFineData.add(r);
            }

            view.displayData(currentFineData);

            long paidCount = currentFineData.stream().filter(FineCollectionReport::isFinePaid).count();
            int totalFines = currentFineData.stream().mapToInt(FineCollectionReport::getFineAmount).sum();
            int paidFines = currentFineData.stream()
                .filter(FineCollectionReport::isFinePaid)
                .mapToInt(FineCollectionReport::getFineAmount)
                .sum();

            view.setSummary(String.format("Total fines: %d | Paid: %d (\u20B1%d) | Unpaid: %d (\u20B1%d)",
                currentFineData.size(), paidCount, paidFines,
                currentFineData.size() - paidCount, totalFines - paidFines));

        } catch (SQLException ex) {
            view.showError(dbMessage(ex));
        }
    }

    private void exportCurrentReport() {
        if (currentReportType == null) {
            view.showError("Please select a report type first.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Report");
        fileChooser.setInitialFileName(ExcelExport.generateFilename(currentReportType));
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );

        File file = fileChooser.showSaveDialog(view.getStage());
        if (file == null) {
            return;
        }

        try {
            switch (currentReportType) {
                case "Overdue Books" -> ExcelExport.exportOverdueBooks(currentOverdueData, file);
                case "Monthly Statistics" -> ExcelExport.exportMonthlyStatistics(currentMonthlyData, file);
                case "Fine Collection" -> ExcelExport.exportFineCollection(currentFineData, file);
            }
            view.showInfo("Export Complete", "Report exported successfully to:\n" + file.getAbsolutePath());
        } catch (IOException ex) {
            view.showError("Failed to export: " + ex.getMessage());
        }
    }

    private static String dbMessage(SQLException ex) {
        String msg = ex.getMessage();
        if (msg != null && (msg.contains("doesn't exist") || msg.contains("Unknown table"))) {
            return "Database tables are missing. Please check your database setup.";
        }
        return "Database error: " + (msg != null ? msg : ex.getClass().getSimpleName());
    }
}
