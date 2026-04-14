package com.mycompany.bookloanandreturn.Controller;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.bookloanandreturn.DatabaseConnection;
import com.mycompany.bookloanandreturn.Models.LoanRecord;
import com.mycompany.bookloanandreturn.Models.ReceiptData;
import com.mycompany.bookloanandreturn.View.ReceiptView;
import com.mycompany.bookloanandreturn.View.ReturnBookView;
import com.mycompany.bookloanandreturn.util.OverdueFine;

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
                if (dd != null) {
                    LocalDate dueLocal = dd.toLocalDate();
                    lr.setDueDate(dueLocal.toString());
                    lr.setDueDateValue(dueLocal);
                } else {
                    lr.setDueDate("");
                }
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
        LocalDate due = selected.getDueDateValue();
        int fine = OverdueFine.finePesos(due, returnDay);
        long daysLate = due != null ? ChronoUnit.DAYS.between(due, returnDay) : 0L;

        String notes = view.getNotes();
        String insReturn = "INSERT INTO book_return (loan_id, return_date, fine_pesos, notes) VALUES (?, ?, ?, ?)";
        String incStock = "UPDATE book SET stock = stock + 1 WHERE book_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ins = conn.prepareStatement(insReturn, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement upd = conn.prepareStatement(incStock)) {
                ins.setInt(1, selected.getLoanId());
                ins.setDate(2, Date.valueOf(returnDay));
                ins.setInt(3, fine);
                if (notes.isEmpty()) {
                    ins.setNull(4, java.sql.Types.VARCHAR);
                } else {
                    ins.setString(4, notes);
                }
                ins.executeUpdate();
                int returnId;
                try (ResultSet generatedKeys = ins.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        returnId = generatedKeys.getInt(1);
                    } else {
                        returnId = 0;
                    }
                }
                upd.setInt(1, selected.getBookId());
                int n = upd.executeUpdate();
                if (n != 1) {
                    conn.rollback();
                    view.showError("Could not update book stock.");
                    return;
                }
                conn.commit();
                if (fine > 0) {
                    ReceiptData receipt = new ReceiptData();
                    receipt.setReturnId(returnId);
                    receipt.setLoanId(selected.getLoanId());
                    receipt.setBookTitle(selected.getBookTitle());
                    receipt.setBorrowerName(selected.getBorrowerName());
                    if (selected.getLoanDate() != null && !selected.getLoanDate().isEmpty()) {
                        receipt.setLoanDate(LocalDate.parse(selected.getLoanDate()));
                    }
                    receipt.setDueDate(due);
                    receipt.setReturnDate(returnDay);
                    receipt.setDaysLate(daysLate);
                    receipt.setFineAmount(fine);
                    receipt.setNotes(notes);

                    ReceiptView receiptView = new ReceiptView(view.getStage());
                    receiptView.displayReceipt(receipt);
                    receiptView.addPayListener(() -> {
                        try {
                            saveReceiptAndMarkPaid(conn, receipt);
                        } catch (SQLException ex) {
                            view.showError(dbMessage(ex));
                        }
                    });
                    receiptView.show();
                }
                String successMsg = fine > 0
                        ? "Return recorded. Overdue fine: "
                                + OverdueFine.formatPesos(fine)
                                + " ("
                                + daysLate
                                + " day(s) after due date). Stock updated."
                        : "Return recorded. No overdue fine. Stock updated.";
                view.showSuccess(successMsg);
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

    private void saveReceiptAndMarkPaid(Connection conn, ReceiptData receipt) throws SQLException {
        String updateFinePaid = "UPDATE book_return SET fine_paid = TRUE WHERE return_id = ?";
        String insertReceipt = "INSERT INTO receipts (return_id, loan_id, borrower_name, book_title, " +
                "loan_date, due_date, return_date, days_late, fine_amount, fine_paid, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, TRUE, ?)";

        try (PreparedStatement upd = conn.prepareStatement(updateFinePaid);
             PreparedStatement ins = conn.prepareStatement(insertReceipt)) {
            upd.setInt(1, receipt.getReturnId());
            upd.executeUpdate();

            ins.setInt(1, receipt.getReturnId());
            ins.setInt(2, receipt.getLoanId());
            ins.setString(3, receipt.getBorrowerName());
            ins.setString(4, receipt.getBookTitle());
            if (receipt.getLoanDate() != null) {
                ins.setDate(5, Date.valueOf(receipt.getLoanDate()));
            } else {
                ins.setNull(5, java.sql.Types.DATE);
            }
            ins.setDate(6, Date.valueOf(receipt.getDueDate()));
            ins.setDate(7, Date.valueOf(receipt.getReturnDate()));
            ins.setLong(8, receipt.getDaysLate());
            ins.setInt(9, receipt.getFineAmount());
            if (receipt.getNotes() != null && !receipt.getNotes().isEmpty()) {
                ins.setString(10, receipt.getNotes());
            } else {
                ins.setNull(10, java.sql.Types.VARCHAR);
            }
            ins.executeUpdate();
        }
    }

    private static String dbMessage(SQLException ex) {
        String msg = ex.getMessage();
        if (msg != null && (msg.contains("doesn't exist") || msg.contains("Unknown table"))) {
            return "Loan tables are missing. Run sql/schema_loan_return.sql on database bookloan_and_return.";
        }
        if (msg != null && msg.contains("fine_paid")) {
            return "Database needs the fine_paid column. Run sql/migration_add_receipts_and_fine_status.sql on bookloan_and_return.";
        }
        if (msg != null && msg.contains("receipts")) {
            return "Database needs the receipts table. Run sql/migration_add_receipts_and_fine_status.sql on bookloan_and_return.";
        }
        if (msg != null && msg.contains("fine_pesos")) {
            return "Database needs the fine column. Run sql/migration_add_fine_pesos.sql on bookloan_and_return.";
        }
        return "Database error: " + (msg != null ? msg : ex.getClass().getSimpleName());
    }
}
