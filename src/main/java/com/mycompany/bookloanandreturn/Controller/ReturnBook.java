package com.mycompany.bookloanandreturn.Controller;

import com.mycompany.bookloanandreturn.DatabaseConnection;
import com.mycompany.bookloanandreturn.Models.LoanRecord;
import com.mycompany.bookloanandreturn.View.ReturnBookView;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.stage.Stage;

public class ReturnBook {
    private final ReturnBookView view;

    public ReturnBook(Stage stage, Runnable onReturnMenu) {
        view = new ReturnBookView(stage);
        view.addBackListener(() -> {
            if (onReturnMenu != null) onReturnMenu.run();
        });
        view.addRefreshListener(this::loadActiveLoans);
        view.addReturnListener(this::recordReturn);
        loadActiveLoans();
        view.show();
    }

    private void loadActiveLoans() {
        String sql = """
                SELECT l.loan_id, l.book_id, b.bookName, l.borrower_name, l.loan_date, l.due_date
                FROM loan l
                INNER JOIN book b ON b.book_id = l.book_id
                LEFT JOIN book_return r ON r.loan_id = l.loan_id
                WHERE r.return_id IS NULL
                ORDER BY l.loan_date DESC, l.loan_id DESC
                """;
        List<LoanRecord> rows = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LoanRecord lr = new LoanRecord();
                lr.setLoanId(rs.getInt("loan_id"));
                lr.setBookId(rs.getInt("book_id"));
                String title = rs.getString("bookName");
                lr.setBookTitle(title != null && !title.isBlank() ? title : "—");
                lr.setBorrowerName(rs.getString("borrower_name"));
                Date ld = rs.getDate("loan_date");
                Date dd = rs.getDate("due_date");
                lr.setLoanDate(ld != null ? ld.toLocalDate().toString() : "");
                lr.setDueDate(dd != null ? dd.toLocalDate().toString() : "");
                rows.add(lr);
            }
            view.displayLoans(rows);
        } catch (SQLException ex) {
            view.showError(dbMessage(ex));
        }
    }

    private void recordReturn() {
        int idx = view.getSelectedRowIndex();
        LoanRecord selected = view.getLoanAt(idx);
        if (selected == null) {
            view.showError("Please select an active loan to return.");
            return;
        }
        LocalDate returnDay = LocalDate.now();
        String notes = view.getNotes();
        String insReturn = "INSERT INTO book_return (loan_id, return_date, notes) VALUES (?, ?, ?)";
        String incStock = "UPDATE book SET stock = stock + 1 WHERE book_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ins = conn.prepareStatement(insReturn);
                 PreparedStatement upd = conn.prepareStatement(incStock)) {
                ins.setInt(1, selected.getLoanId());
                ins.setDate(2, Date.valueOf(returnDay));
                if (notes.isEmpty()) {
                    ins.setNull(3, java.sql.Types.VARCHAR);
                } else {
                    ins.setString(3, notes);
                }
                ins.executeUpdate();
                upd.setInt(1, selected.getBookId());
                int n = upd.executeUpdate();
                if (n != 1) {
                    conn.rollback();
                    view.showError("Could not update book stock.");
                    return;
                }
                conn.commit();
                view.showSuccess("Return recorded. Stock updated.");
                loadActiveLoans();
            } catch (SQLException ex) {
                conn.rollback();
                if (ex.getErrorCode() == 1062 || (ex.getMessage() != null && ex.getMessage().contains("Duplicate"))) {
                    view.showError("This loan was already returned.");
                } else {
                    view.showError(dbMessage(ex));
                }
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            view.showError(dbMessage(ex));
        }
    }

    private static String dbMessage(SQLException ex) {
        String msg = ex.getMessage();
        if (msg != null && (msg.contains("doesn't exist") || msg.contains("Unknown table"))) {
            return "Loan tables are missing. Run sql/schema_loan_return.sql on database bookloan_and_return.";
        }
        return "Database error: " + (msg != null ? msg : ex.getClass().getSimpleName());
    }
}
