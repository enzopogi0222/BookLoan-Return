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
            if (name.startsWith("Student #")) {
                view.setStudentDisplayName("ID not found");
            } else {
                view.setStudentDisplayName(name);
            }
        } catch (NumberFormatException e) {
            view.setStudentDisplayName("Invalid ID format");
        }
    }

    private void loadAvailableBooks() {
        String sql = "SELECT book_id, bookName, stock, COALESCE(cost, 0) as cost FROM book WHERE stock > 0 ORDER BY bookName";
        List<AvailableBook> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("book_id");
                String name = rs.getString("bookName");
                int stock = rs.getInt("stock");
                double cost = rs.getDouble("cost");
                if (name == null || name.isBlank()) name = "—";
                list.add(new AvailableBook(id, name, stock, cost));
            }
            view.setAvailableBooks(list);
            if (list.isEmpty()) {
                view.showError("No books with available stock.");
            }
        } catch (SQLException ex) {
            view.showError(dbMessage(ex));
        }
    }

    private void confirmLoan() {
        List<AvailableBook> selectedBooks = view.getSelectedBooks();
        if (selectedBooks.isEmpty()) {
            view.showError("Please select at least one book.");
            return;
        }

        long uniqueCount = selectedBooks.stream().map(AvailableBook::bookId).distinct().count();
        if (uniqueCount < selectedBooks.size()) {
            view.showError("Duplicate books selected. Please choose different books.");
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

        String studentName = getStudentName(studentId);
        if (studentName.startsWith("Student #") && !studentExists(studentId)) {
            view.showError("Student ID not found.");
            return;
        }

        LocalDate loanDate = view.getLoanDate();
        LocalDate dueDate = view.getDueDate();
        if (loanDate == null || dueDate == null) {
            view.showError("Please set both dates.");
            return;
        }
        if (dueDate.isBefore(loanDate)) {
            view.showError("Due date cannot be before loan date.");
            return;
        }

        int activeLoans = countActiveLoansForStudent(studentId);
        if (activeLoans + selectedBooks.size() > 3) {
            view.showError(String.format("Student has %d active loans. They can only borrow %d more book(s).", 
                activeLoans, 3 - activeLoans));
            return;
        }

        for (AvailableBook book : selectedBooks) {
            if (hasActiveLoanForBook(studentId, book.bookId())) {
                view.showError("Student already has '" + book.title() + "' on loan. Return it first.");
                return;
            }
        }

        String insertLoan = "INSERT INTO loan (book_id, borrower_name, student_id, loan_date, due_date) VALUES (?, ?, ?, ?, ?)";
        String decStock = "UPDATE book SET stock = stock - 1 WHERE book_id = ? AND stock > 0";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ins = conn.prepareStatement(insertLoan);
                 PreparedStatement upd = conn.prepareStatement(decStock)) {
                
                for (AvailableBook book : selectedBooks) {
                    ins.setInt(1, book.bookId());
                    ins.setString(2, studentName);
                    ins.setLong(3, studentId);
                    ins.setDate(4, Date.valueOf(loanDate));
                    ins.setDate(5, Date.valueOf(dueDate));
                    ins.addBatch();

                    upd.setInt(1, book.bookId());
                    upd.addBatch();
                }

                ins.executeBatch();
                int[] results = upd.executeBatch();
                
                for (int res : results) {
                    if (res == 0) {
                        conn.rollback();
                        view.showError("One or more books are no longer in stock.");
                        return;
                    }
                }

                conn.commit();
                view.showSuccess(String.format("Successfully loaned %d book(s) to %s.", selectedBooks.size(), studentName));
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
            return "Database tables are missing.";
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
            System.err.println("Error checking student: " + ex.getMessage());
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
            System.err.println("Error checking active loan: " + ex.getMessage());
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
