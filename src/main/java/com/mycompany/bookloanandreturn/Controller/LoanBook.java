package com.mycompany.bookloanandreturn.Controller;

import com.mycompany.bookloanandreturn.DatabaseConnection;
import com.mycompany.bookloanandreturn.Models.AvailableBook;
import com.mycompany.bookloanandreturn.View.LoanBookView;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.stage.Stage;

public class LoanBook {
    private final LoanBookView view;

    public LoanBook(Stage stage, Runnable onReturnMenu) {
        view = new LoanBookView(stage);
        view.addBackListener(() -> {
            if (onReturnMenu != null) onReturnMenu.run();
        });
        view.addRefreshListener(this::loadAvailableBooks);
        view.addLoanListener(this::confirmLoan);
        loadAvailableBooks();
        view.show();
    }

    private void loadAvailableBooks() {
        String sql = "SELECT book_id, bookName, stock FROM book WHERE stock > 0 ORDER BY bookName";
        List<AvailableBook> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("book_id");
                String name = rs.getString("bookName");
                int stock = rs.getInt("stock");
                if (name == null || name.isBlank()) name = "—";
                list.add(new AvailableBook(id, name, stock));
            }
            view.setAvailableBooks(list);
            if (list.isEmpty()) {
                view.showError("No books with available stock. Add books or return copies first.");
            }
        } catch (SQLException ex) {
            view.showError(dbMessage(ex));
        }
    }

    private void confirmLoan() {
        AvailableBook choice = view.getSelectedBook();
        if (choice == null) {
            view.showError("Please select a book.");
            return;
        }
        String borrower = view.getBorrowerName();
        if (borrower.isEmpty()) {
            view.showError("Borrower name is required.");
            return;
        }
        LocalDate loanDate = view.getLoanDate();
        LocalDate dueDate = view.getDueDate();
        if (loanDate == null || dueDate == null) {
            view.showError("Please set both loan date and due date.");
            return;
        }
        if (dueDate.isBefore(loanDate)) {
            view.showError("Due date cannot be before loan date.");
            return;
        }

        String insertLoan = "INSERT INTO loan (book_id, borrower_name, loan_date, due_date) VALUES (?, ?, ?, ?)";
        String decStock = "UPDATE book SET stock = stock - 1 WHERE book_id = ? AND stock > 0";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ins = conn.prepareStatement(insertLoan);
                 PreparedStatement upd = conn.prepareStatement(decStock)) {
                ins.setInt(1, choice.bookId());
                ins.setString(2, borrower);
                ins.setDate(3, Date.valueOf(loanDate));
                ins.setDate(4, Date.valueOf(dueDate));
                ins.executeUpdate();
                upd.setInt(1, choice.bookId());
                int n = upd.executeUpdate();
                if (n != 1) {
                    conn.rollback();
                    view.showError("Could not update stock (book may be out of stock).");
                    return;
                }
                conn.commit();
                view.showSuccess("Loan recorded. Stock updated.");
                loadAvailableBooks();
            } catch (SQLException ex) {
                conn.rollback();
                view.showError(dbMessage(ex));
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
