package com.mycompany.bookloanandreturn.Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import com.mycompany.bookloanandreturn.DatabaseConnection;
import com.mycompany.bookloanandreturn.View.MainMenuView;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainMenu {
    private final MainMenuView view;
    private final Timeline refreshTimer;

    public MainMenu(Stage primaryStage) {
        view = new MainMenuView(primaryStage);
        view.addLoanBookListener(this::openLoanBook);
        view.addReturnBookListener(this::openReturnBook);
        view.addTransactionsListener(this::openTransactions);
        view.addReportsListener(this::openReports);

        // Create auto-refresh timer (refresh every 5 seconds)
        refreshTimer = new Timeline(new KeyFrame(Duration.seconds(5), e -> loadStatistics()));
        refreshTimer.setCycleCount(Timeline.INDEFINITE);

        // Initial load and start timer
        loadStatistics();
        refreshTimer.play();

        view.show();
    }

    private void loadStatistics() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Active loans count
            String activeLoansSql = """
                SELECT COUNT(*) as count FROM loan l
                LEFT JOIN book_return r ON r.loan_id = l.loan_id
                WHERE r.return_id IS NULL
                """;
            try (PreparedStatement ps = conn.prepareStatement(activeLoansSql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    view.setActiveLoans(rs.getInt("count"));
                }
            }

            // Returned today count
            String returnedTodaySql = """
                SELECT COUNT(*) as count FROM book_return
                WHERE return_date = ?
                """;
            try (PreparedStatement ps = conn.prepareStatement(returnedTodaySql)) {
                ps.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        view.setReturnedToday(rs.getInt("count"));
                    }
                }
            }

            // Unpaid fines total (from returned books + estimated from active overdue loans)
            String unpaidFinesSql = """
                SELECT COALESCE(SUM(fine_pesos - amount_paid), 0) as total FROM book_return
                WHERE fine_paid = FALSE AND fine_pesos > 0
                """;
            int unpaidFromReturned = 0;
            try (PreparedStatement ps = conn.prepareStatement(unpaidFinesSql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    unpaidFromReturned = rs.getInt("total");
                }
            }

            // Estimated fines from active overdue loans
            String estimatedFinesSql = """
                SELECT COALESCE(SUM(
                    GREATEST(0, DATEDIFF(?, l.due_date) * 10)
                ), 0) as total
                FROM loan l
                LEFT JOIN book_return r ON r.loan_id = l.loan_id
                WHERE r.return_id IS NULL AND l.due_date < ?
                """;
            int estimatedFromActive = 0;
            try (PreparedStatement ps = conn.prepareStatement(estimatedFinesSql)) {
                ps.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
                ps.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        estimatedFromActive = rs.getInt("total");
                    }
                }
            }

            view.setUnpaidFines(unpaidFromReturned + estimatedFromActive);

            // Total books in library
            String totalBooksSql = """
                SELECT COALESCE(SUM(stock), 0) as total FROM book
                """;
            try (PreparedStatement ps = conn.prepareStatement(totalBooksSql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    view.setTotalBooks(rs.getInt("total"));
                }
            }

        } catch (SQLException ex) {
            // Silently fail - stats will show 0
        }
    }

    public MainMenu() {
        this(new Stage());
    }

    private void openLoanBook() {
        refreshTimer.pause();
        new LoanBook(view.getStage(), () -> {
            view.show();
            loadStatistics();
            refreshTimer.play();
        });
    }

    private void openReturnBook() {
        refreshTimer.pause();
        new ReturnBook(view.getStage(), () -> {
            view.show();
            loadStatistics();
            refreshTimer.play();
        });
    }

    private void openTransactions() {
        refreshTimer.pause();
        new Transactions(view.getStage(), () -> {
            view.show();
            loadStatistics();
            refreshTimer.play();
        });
    }

    private void openReports() {
        refreshTimer.pause();
        new Reports(view.getStage(), () -> {
            view.show();
            loadStatistics();
            refreshTimer.play();
        });
    }
}
