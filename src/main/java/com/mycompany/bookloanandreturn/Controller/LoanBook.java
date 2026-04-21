package com.mycompany.bookloanandreturn.Controller;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.bookloanandreturn.DatabaseConnection;
import com.mycompany.bookloanandreturn.Models.AvailableBook;
import com.mycompany.bookloanandreturn.View.LoanBookView;

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

        // Real-time student lookup for convenience
        view.setStudentIdListener(this::handleStudentIdChange);

        loadAvailableBooks();
        view.show();
    }

    private void handleStudentIdChange(String studentIdText) {
        if (studentIdText.isEmpty()) {
            view.setStudentDisplayName("");
            return;
        }
        try {
            long studentId = Long.parseLong(studentIdText);
            String name = getStudentName(studentId);
            if (name.startsWith("Student #")) { // Not found in DB, getStudentName returns default
                view.setStudentDisplayName("ID not found");
            } else {
                view.setStudentDisplayName(name);
            }
        } catch (NumberFormatException e) {
            view.setStudentDisplayName("Invalid ID format");
        }
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
        String studentIdText = view.getBorrowerName().trim();
        if (studentIdText.isEmpty()) {
            view.showError("Student ID is required.");
            return;
        }
        long studentId;
        try {
            studentId = Long.parseLong(studentIdText);
        } catch (NumberFormatException e) {
            view.showError("Student ID must be a number.");
            return;
        }

        // Verify student exists and get their name
        String studentName = getStudentName(studentId);
        if (studentName.startsWith("Student #") && !studentExists(studentId)) {
            view.showError("Student ID not found. Please register the student first.");
            return;
        }

        // Check if student already has this book borrowed and not returned
        if (hasActiveLoanForBook(studentId, choice.bookId())) {
            view.showError("Student already has this book on loan. Return it first before borrowing again.");
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

        // Check student's active loan count (max 3 books allowed)
        int activeLoans = countActiveLoansForStudent(studentId);
        if (activeLoans >= 3) {
            view.showError("Student has already borrowed the maximum of 3 books. Return a book before borrowing another.");
            return;
        }

        // Normalization: In a truly normalized DB, we don't store 'borrower_name' in the loan table
        // if we have a student table. However, to maintain compatibility with your existing schema:
        String insertLoan = "INSERT INTO loan (book_id, borrower_name, student_id, loan_date, due_date) VALUES (?, ?, ?, ?, ?)";
        String decStock = "UPDATE book SET stock = stock - 1 WHERE book_id = ? AND stock > 0";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ins = conn.prepareStatement(insertLoan);
                 PreparedStatement upd = conn.prepareStatement(decStock)) {
                ins.setInt(1, choice.bookId());
                ins.setString(2, studentName); // Use real name instead of "Student #ID"
                ins.setLong(3, studentId);
                ins.setDate(4, Date.valueOf(loanDate));
                ins.setDate(5, Date.valueOf(dueDate));
                ins.executeUpdate();
                upd.setInt(1, choice.bookId());
                int n = upd.executeUpdate();
                if (n != 1) {
                    conn.rollback();
                    view.showError("Could not update stock (book may be out of stock).");
                    return;
                }
                conn.commit();
                String successMsg = String.format("Loan recorded for %s. Book: '%s'. Stock updated.",
                        studentName, choice.title());
                view.showSuccess(successMsg);
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
        if (msg != null && msg.contains("student_id")) {
            return "Database needs the student_id column. Run sql/migration_add_student_id_to_loan.sql on bookloan_and_return.";
        }
        return "Database error: " + (msg != null ? msg : ex.getClass().getSimpleName());
    }

    private int countActiveLoansForStudent(long studentId) {
        String sql = """
                SELECT COUNT(*) AS active_count
                FROM loan l
                LEFT JOIN book_return r ON r.loan_id = l.loan_id
                WHERE l.student_id = ? AND r.return_id IS NULL
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("active_count");
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error counting active loans: " + ex.getMessage());
        }
        return 0;
    }

    private boolean studentExists(long studentId) {
        String sql = "SELECT 1 FROM student WHERE student_id = ? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            System.err.println("Error checking student exists: " + ex.getMessage());
        }
        return false;
    }

    private boolean hasActiveLoanForBook(long studentId, int bookId) {
        String sql = """
                SELECT 1 FROM loan l
                LEFT JOIN book_return r ON r.loan_id = l.loan_id
                WHERE l.student_id = ? AND l.book_id = ? AND r.return_id IS NULL
                LIMIT 1
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ps.setInt(2, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            System.err.println("Error checking active loan for book: " + ex.getMessage());
        }
        return false;
    }

    private String getStudentName(long studentId) {
        String sql = "SELECT full_name FROM student WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("full_name");
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error fetching student name: " + ex.getMessage());
        }
        return "Student #" + studentId;
    }
}
