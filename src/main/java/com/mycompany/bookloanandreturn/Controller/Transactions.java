package com.mycompany.bookloanandreturn.Controller;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.bookloanandreturn.DatabaseConnection;
import com.mycompany.bookloanandreturn.Models.LoanTransaction;
import com.mycompany.bookloanandreturn.View.TransactionsView;
import com.mycompany.bookloanandreturn.util.OverdueFine;

import javafx.stage.Stage;

public class Transactions implements Runnable {
    private final TransactionsView view;
    private final Runnable onReturnMenu;
    private List<LoanTransaction> allRows = new ArrayList<>();

    public Transactions(Stage stage, Runnable onReturnMenu) {
        view = new TransactionsView(stage);
        this.onReturnMenu = onReturnMenu;
        view.addRefreshListener(this);
        view.addFilterListener(this::applyFilter);
        view.addBackListener(this::goBack);
        loadTransactions();
        view.show();
    }

    private void goBack() {
        if (onReturnMenu != null) {
            onReturnMenu.run();
        }
    }

    @Override
    public void run() {
        loadTransactions();
    }

    private void loadTransactions() {
        String sql = """
                SELECT l.loan_id, b.bookName, l.borrower_name, l.loan_date, l.due_date,
                       r.return_date, r.notes, COALESCE(r.fine_pesos, 0) AS fine_pesos,
                       COALESCE(r.fine_paid, FALSE) AS fine_paid
                FROM loan l
                INNER JOIN book b ON b.book_id = l.book_id
                LEFT JOIN book_return r ON r.loan_id = l.loan_id
                ORDER BY l.loan_id DESC
                """;
        List<LoanTransaction> rows = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LoanTransaction t = new LoanTransaction();
                t.setLoanId(rs.getInt("loan_id"));
                String title = rs.getString("bookName");
                t.setBookTitle(title != null && !title.isBlank() ? title : "—");
                String borrower = rs.getString("borrower_name");
                t.setBorrowerName(borrower != null ? borrower : "—");
                Date ld = rs.getDate("loan_date");
                Date dd = rs.getDate("due_date");
                t.setLoanDate(ld != null ? ld.toLocalDate().toString() : "");
                t.setDueDate(dd != null ? dd.toLocalDate().toString() : "");
                Date rd = rs.getDate("return_date");
                if (rd != null) {
                    t.setReturnDate(rd.toLocalDate().toString());
                    t.setStatus("Returned");
                    t.setFinePesos(rs.getInt("fine_pesos"));
                    t.setFinePaid(rs.getBoolean("fine_paid"));
                } else {
                    t.setReturnDate("—");
                    t.setStatus("Active");
                    t.setFinePesos(0);
                    t.setFinePaid(false);
                }
                String notes = rs.getString("notes");
                t.setNotes(notes != null && !notes.isBlank() ? notes : "");
                rows.add(t);
            }
            allRows = rows;
            applyFilter();
        } catch (SQLException ex) {
            view.showError(dbMessage(ex));
        }
    }

    private void applyFilter() {
        String q = view.getSearchText();
        if (q.isEmpty()) {
            view.displayTransactions(allRows);
            return;
        }
        List<LoanTransaction> filtered = new ArrayList<>();
        for (LoanTransaction t : allRows) {
            if (matches(t, q)) {
                filtered.add(t);
            }
        }
        view.displayTransactions(filtered);
    }

    private static boolean matches(LoanTransaction t, String q) {
        return contains(t.getBookTitle(), q)
                || contains(t.getBorrowerName(), q)
                || contains(t.getLoanDate(), q)
                || contains(t.getDueDate(), q)
                || contains(t.getReturnDate(), q)
                || contains(t.getStatus(), q)
                || contains(t.getNotes(), q)
                || String.valueOf(t.getLoanId()).contains(q)
                || contains(OverdueFine.formatPesos(t.getFinePesos()), q)
                || String.valueOf(t.getFinePesos()).contains(q);
    }

    private static boolean contains(String value, String q) {
        return value != null && value.toLowerCase().contains(q);
    }

    private static String dbMessage(SQLException ex) {
        String msg = ex.getMessage();
        if (msg != null && (msg.contains("doesn't exist") || msg.contains("Unknown table"))) {
            return "Loan tables are missing. Run sql/schema_loan_return.sql on database bookloan_and_return.";
        }
        if (msg != null && msg.contains("fine_pesos")) {
            return "Database needs the fine column. Run sql/migration_add_fine_pesos.sql on bookloan_and_return.";
        }
        return "Database error: " + (msg != null ? msg : ex.getClass().getSimpleName());
    }
}
