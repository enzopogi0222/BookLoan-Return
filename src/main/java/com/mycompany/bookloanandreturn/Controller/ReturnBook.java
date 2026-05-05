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
import com.mycompany.bookloanandreturn.Models.MultiReceiptData;
import com.mycompany.bookloanandreturn.Models.ReceiptData;
import com.mycompany.bookloanandreturn.View.ReceiptView;
import com.mycompany.bookloanandreturn.View.ReturnBookView;
import com.mycompany.bookloanandreturn.util.OverdueFine;

import javafx.stage.Stage;

public class ReturnBook {
    private final ReturnBookView view;
    private List<LoanRecord> allLoans = new ArrayList<>();

    public ReturnBook(Stage stage, Runnable onReturnMenu) {
        view = new ReturnBookView(stage);
        view.addBackListener(() -> {
            if (onReturnMenu != null) onReturnMenu.run();
        });
        view.addRefreshListener(this::loadActiveLoans);
        view.addFilterListener(this::applyFilter);
        view.addReturnListener(this::recordReturn);
        loadActiveLoans();
        view.show();
    }

    private void loadActiveLoans() {
        // Query 1: Active loans (not yet returned)
        String activeSql = """
                SELECT l.loan_id, l.book_id, b.bookName, l.borrower_name, l.student_id,
                       s.full_name AS student_name, s.phone, l.loan_date, l.due_date,
                       0 as return_id, 0 as remaining_balance, FALSE as has_unpaid_fine,
                       0 as original_days_late
                FROM loan l
                INNER JOIN book b ON b.book_id = l.book_id
                LEFT JOIN book_return r ON r.loan_id = l.loan_id
                LEFT JOIN student s ON s.student_id = l.student_id
                WHERE r.return_id IS NULL
                """;

        // Query 2: Returned books with unpaid fines (for additional payments)
        String unpaidSql = """
                SELECT l.loan_id, l.book_id, b.bookName, l.borrower_name, l.student_id,
                       s.full_name AS student_name, s.phone, l.loan_date, l.due_date,
                       r.return_id, (r.fine_pesos - r.amount_paid) as remaining_balance,
                       TRUE as has_unpaid_fine,
                       GREATEST(0, DATEDIFF(r.return_date, l.due_date)) as original_days_late
                FROM loan l
                INNER JOIN book b ON b.book_id = l.book_id
                INNER JOIN book_return r ON r.loan_id = l.loan_id
                LEFT JOIN student s ON s.student_id = l.student_id
                WHERE r.fine_paid = FALSE AND r.fine_pesos > r.amount_paid
                """;

        List<LoanRecord> rows = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Load active loans
            try (PreparedStatement ps = conn.prepareStatement(activeSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(mapLoanRecord(rs));
                }
            }
            // Load returned books with unpaid fines
            try (PreparedStatement ps = conn.prepareStatement(unpaidSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(mapLoanRecord(rs));
                }
            }
            allLoans = rows;
            applyFilter();
        } catch (SQLException ex) {
            view.showError(dbMessage(ex));
        }
    }

    private LoanRecord mapLoanRecord(ResultSet rs) throws SQLException {
        LoanRecord lr = new LoanRecord();
        lr.setLoanId(rs.getInt("loan_id"));
        lr.setBookId(rs.getInt("book_id"));
        String title = rs.getString("bookName");
        lr.setBookTitle(title != null && !title.isBlank() ? title : "—");
        lr.setBorrowerName(rs.getString("borrower_name"));
        lr.setStudentId(rs.getLong("student_id"));
        lr.setStudentName(rs.getString("student_name"));
        lr.setPhone(rs.getString("phone"));
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
        lr.setReturnId(rs.getInt("return_id"));
        lr.setRemainingBalance(rs.getInt("remaining_balance"));
        lr.setHasUnpaidFine(rs.getBoolean("has_unpaid_fine"));
        lr.setOriginalDaysLate(rs.getLong("original_days_late"));
        return lr;
    }

    private void applyFilter() {
        String q = view.getSearchText();
        if (q.isEmpty()) {
            view.displayLoans(allLoans);
            return;
        }
        List<LoanRecord> filtered = new ArrayList<>();
        for (LoanRecord lr : allLoans) {
            if (matches(lr, q)) {
                filtered.add(lr);
            }
        }
        view.displayLoans(filtered);
    }

    private static boolean matches(LoanRecord lr, String q) {
        return contains(lr.getBookTitle(), q)
                || contains(lr.getBorrowerName(), q)
                || contains(lr.getStudentName(), q)
                || contains(lr.getPhone(), q)
                || contains(lr.getLoanDate(), q)
                || contains(lr.getDueDate(), q);
    }

    private static boolean contains(String value, String q) {
        return value != null && value.toLowerCase().contains(q);
    }

    private void recordReturn() {
        List<LoanRecord> selected = view.getSelectedLoans();
        if (selected.isEmpty()) {
            view.showError("Please select at least one active loan or unpaid fine to process.");
            return;
        }

        String notes = view.getNotes();
        LocalDate returnDay = LocalDate.now();
        int totalFine = 0;

        MultiReceiptData multiReceipt = new MultiReceiptData();
        multiReceipt.setReturnDate(returnDay);
        multiReceipt.setNotes(notes);

        // First pass: calculate fines and build receipt data (no DB operations yet)
        for (LoanRecord loan : selected) {
            LocalDate due = loan.getDueDateValue();
            int fine;
            long daysLate;

            if (loan.isHasUnpaidFine()) {
                // For books already returned with unpaid fine, use remaining balance
                fine = loan.getRemainingBalance();
                daysLate = loan.getOriginalDaysLate();
            } else {
                // For active loans, calculate fine based on overdue days
                fine = OverdueFine.finePesos(due, returnDay);
                daysLate = due != null ? ChronoUnit.DAYS.between(due, returnDay) : 0L;
            }
            totalFine += fine;

            if (multiReceipt.getBorrowerName() == null) {
                multiReceipt.setBorrowerName(loan.getBorrowerName());
                multiReceipt.setStudentId(String.valueOf(loan.getStudentId()));
            }
            LocalDate loanDate = (loan.getLoanDate() != null && !loan.getLoanDate().isEmpty())
                    ? LocalDate.parse(loan.getLoanDate()) : null;
            // Use existing return_id for unpaid fines, loan_id for new returns
            int returnId = loan.isHasUnpaidFine() ? loan.getReturnId() : loan.getLoanId();
            multiReceipt.addItem(new MultiReceiptData.ReceiptItem(
                    returnId, loan.getLoanId(), loan.getBookTitle(),
                    loanDate, due, daysLate, fine));
        }

        // If there are fines, show receipt first and require payment before returning
        if (multiReceipt.hasFines()) {
            ReceiptView receiptView = new ReceiptView(view.getStage());
            receiptView.displayMultiReceipt(multiReceipt);
            receiptView.addPayListener(() -> {
                // Only return books and record payment when "Pay Fine" is clicked
                int paymentAmount = receiptView.getPaymentAmount();
                processReturnWithPayment(selected, multiReceipt, returnDay, notes, paymentAmount);
            });
            receiptView.show();
            // Check if payment was made
            if (!receiptView.isPaid()) {
                view.showInfo("Return cancelled. Fine of " + OverdueFine.formatPesos(totalFine)
                    + " must be paid before returning the book(s).");
                return;
            }
            // If paid, the return was already processed in the payListener
            return;
        }

        // No fines - proceed with normal return
        processReturnWithoutPayment(selected, returnDay, notes);
    }

    private void processReturnWithPayment(List<LoanRecord> selected, MultiReceiptData multiReceipt,
                                          LocalDate returnDay, String notes, int totalPaymentAmount) {
        String insReturn = "INSERT INTO book_return (loan_id, return_date, fine_pesos, amount_paid, fine_paid, notes) VALUES (?, ?, ?, ?, ?, ?)";
        String updReturn = "UPDATE book_return SET amount_paid = amount_paid + ?, fine_paid = ? WHERE return_id = ?";
        String incStock = "UPDATE book SET stock = stock + 1 WHERE book_id = ?";
        int successCount = 0;
        int distributedPayment = 0;
        StringBuilder errors = new StringBuilder();

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            for (int i = 0; i < selected.size(); i++) {
                LoanRecord loan = selected.get(i);
                MultiReceiptData.ReceiptItem item = multiReceipt.getItems().get(i);

                // Distribute payment across items
                int itemPayment;
                if (i == selected.size() - 1) {
                    itemPayment = totalPaymentAmount - distributedPayment;
                } else {
                    itemPayment = Math.min(item.fineAmount, totalPaymentAmount - distributedPayment);
                }
                distributedPayment += itemPayment;
                boolean fullyPaid = itemPayment >= item.fineAmount;

                try {
                    int returnId;

                    if (loan.isHasUnpaidFine()) {
                        // Book already returned - just update payment
                        try (PreparedStatement upd = conn.prepareStatement(updReturn)) {
                            upd.setInt(1, itemPayment);
                            upd.setBoolean(2, fullyPaid);
                            upd.setInt(3, loan.getReturnId());
                            upd.executeUpdate();
                        }
                        returnId = loan.getReturnId();
                    } else {
                        // New return - insert record
                        try (PreparedStatement ins = conn.prepareStatement(insReturn, Statement.RETURN_GENERATED_KEYS);
                             PreparedStatement upd = conn.prepareStatement(incStock)) {
                            ins.setInt(1, loan.getLoanId());
                            ins.setDate(2, Date.valueOf(returnDay));
                            ins.setInt(3, item.fineAmount);
                            ins.setInt(4, itemPayment);
                            ins.setBoolean(5, fullyPaid);
                            if (notes.isEmpty()) {
                                ins.setNull(6, java.sql.Types.VARCHAR);
                            } else {
                                ins.setString(6, notes);
                            }
                            ins.executeUpdate();

                            try (ResultSet generatedKeys = ins.getGeneratedKeys()) {
                                if (generatedKeys.next()) {
                                    returnId = generatedKeys.getInt(1);
                                } else {
                                    returnId = 0;
                                }
                            }

                            upd.setInt(1, loan.getBookId());
                            int n = upd.executeUpdate();
                            if (n != 1) {
                                conn.rollback();
                                view.showError("Could not update book stock for: " + loan.getBookTitle());
                                conn.setAutoCommit(true);
                                return;
                            }
                        }
                    }

                    // Save receipt and mark as paid
                    ReceiptData singleReceipt = new ReceiptData();
                    singleReceipt.setReturnId(returnId);
                    singleReceipt.setLoanId(loan.getLoanId());
                    singleReceipt.setBookTitle(loan.getBookTitle());
                    singleReceipt.setBorrowerName(multiReceipt.getBorrowerName());
                    singleReceipt.setLoanDate(item.loanDate);
                    singleReceipt.setDueDate(item.dueDate);
                    singleReceipt.setReturnDate(returnDay);
                    singleReceipt.setDaysLate(item.daysLate);
                    singleReceipt.setFineAmount(item.fineAmount);
                    singleReceipt.setNotes(notes);
                    saveReceiptAndMarkPaid(conn, singleReceipt, itemPayment, fullyPaid);

                    successCount++;
                } catch (SQLException ex) {
                    conn.rollback();
                    conn.setAutoCommit(true);
                    if (ex.getErrorCode() == 1062 || (ex.getMessage() != null && ex.getMessage().contains("Duplicate"))) {
                        errors.append(loan.getBookTitle()).append(" was already returned.\n");
                    } else {
                        errors.append("Error processing ").append(loan.getBookTitle()).append(": ").append(ex.getMessage()).append("\n");
                    }
                }
            }

            conn.commit();
            conn.setAutoCommit(true);

            if (errors.length() > 0) {
                view.showError(errors.toString());
            }
            if (successCount > 0) {
                int remaining = multiReceipt.getTotalFine() - totalPaymentAmount;
                String msg;
                if (remaining <= 0) {
                    msg = "Payment recorded for " + successCount + " item(s). Fine fully paid: "
                        + OverdueFine.formatPesos(totalPaymentAmount);
                } else {
                    msg = "Payment recorded for " + successCount + " item(s). Payment: "
                        + OverdueFine.formatPesos(totalPaymentAmount) + ". Remaining balance: "
                        + OverdueFine.formatPesos(remaining);
                }
                view.showSuccess(msg);
                view.clearSelection();
                loadActiveLoans();
            }
        } catch (SQLException ex) {
            view.showError(dbMessage(ex));
        }
    }

    private void processReturnWithoutPayment(List<LoanRecord> selected, LocalDate returnDay, String notes) {
        String insReturn = "INSERT INTO book_return (loan_id, return_date, fine_pesos, amount_paid, fine_paid, notes) VALUES (?, ?, ?, 0, TRUE, ?)";
        String incStock = "UPDATE book SET stock = stock + 1 WHERE book_id = ?";
        int successCount = 0;
        StringBuilder errors = new StringBuilder();

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            for (LoanRecord loan : selected) {
                try (PreparedStatement ins = conn.prepareStatement(insReturn, Statement.RETURN_GENERATED_KEYS);
                     PreparedStatement upd = conn.prepareStatement(incStock)) {
                    ins.setInt(1, loan.getLoanId());
                    ins.setDate(2, Date.valueOf(returnDay));
                    ins.setInt(3, 0);
                    if (notes.isEmpty()) {
                        ins.setNull(4, java.sql.Types.VARCHAR);
                    } else {
                        ins.setString(4, notes);
                    }
                    ins.executeUpdate();

                    upd.setInt(1, loan.getBookId());
                    int n = upd.executeUpdate();
                    if (n != 1) {
                        conn.rollback();
                        view.showError("Could not update book stock for: " + loan.getBookTitle());
                        conn.setAutoCommit(true);
                        return;
                    }
                    successCount++;
                } catch (SQLException ex) {
                    conn.rollback();
                    conn.setAutoCommit(true);
                    if (ex.getErrorCode() == 1062 || (ex.getMessage() != null && ex.getMessage().contains("Duplicate"))) {
                        errors.append(loan.getBookTitle()).append(" was already returned.\n");
                    } else {
                        errors.append("Error returning ").append(loan.getBookTitle()).append(": ").append(ex.getMessage()).append("\n");
                    }
                }
            }

            conn.commit();
            conn.setAutoCommit(true);

            if (errors.length() > 0) {
                view.showError(errors.toString());
            }
            if (successCount > 0) {
                view.showSuccess("Returned " + successCount + " book(s). No overdue fine. Stock updated.");
                view.clearSelection();
                loadActiveLoans();
            }
        } catch (SQLException ex) {
            view.showError(dbMessage(ex));
        }
    }

    private void saveReceiptAndMarkPaid(Connection conn, ReceiptData receipt,
                                        int amountPaid, boolean fullyPaid) throws SQLException {
        // book_return is already updated in processReturnWithPayment
        // Just insert a receipt record for this payment
        String insertReceipt = "INSERT INTO receipts (return_id, loan_id, borrower_name, book_title, " +
                "loan_date, due_date, return_date, days_late, fine_amount, amount_paid, fine_paid, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ins = conn.prepareStatement(insertReceipt)) {
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
            ins.setInt(10, amountPaid);
            ins.setBoolean(11, fullyPaid);
            if (receipt.getNotes() != null && !receipt.getNotes().isEmpty()) {
                ins.setString(12, receipt.getNotes());
            } else {
                ins.setNull(12, java.sql.Types.VARCHAR);
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
        if (msg != null && msg.contains("amount_paid")) {
            return "Database needs the amount_paid column. Run sql/migration_add_amount_paid.sql on bookloan_and_return.";
        }
        return "Database error: " + (msg != null ? msg : ex.getClass().getSimpleName());
    }
}
