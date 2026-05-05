package com.mycompany.bookloanandreturn.Models;

import java.time.LocalDate;

/** Active loan row for the return table (no matching book_return yet). */
public class LoanRecord {
    private int loanId;
    private int bookId;
    private String bookTitle;
    private String borrowerName;
    private long studentId;
    private String studentName;
    private String phone;
    private String loanDate;
    private String dueDate;
    /** Parsed due date for fine calculation; may be null if not set. */
    private LocalDate dueDateValue;
    /** Flag indicating this loan has been returned but has unpaid fine. */
    private boolean hasUnpaidFine;
    /** Remaining balance for unpaid fine. */
    private int remainingBalance;
    /** Return ID if book has been returned (for additional payments). */
    private int returnId;
    /** Original days late when the book was first returned (for unpaid fine display). */
    private long originalDaysLate;

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(String loanDate) {
        this.loanDate = loanDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getDueDateValue() {
        return dueDateValue;
    }

    public void setDueDateValue(LocalDate dueDateValue) {
        this.dueDateValue = dueDateValue;
    }

    public boolean isHasUnpaidFine() {
        return hasUnpaidFine;
    }

    public void setHasUnpaidFine(boolean hasUnpaidFine) {
        this.hasUnpaidFine = hasUnpaidFine;
    }

    public int getRemainingBalance() {
        return remainingBalance;
    }

    public void setRemainingBalance(int remainingBalance) {
        this.remainingBalance = remainingBalance;
    }

    public int getReturnId() {
        return returnId;
    }

    public void setReturnId(int returnId) {
        this.returnId = returnId;
    }

    public long getOriginalDaysLate() {
        return originalDaysLate;
    }

    public void setOriginalDaysLate(long originalDaysLate) {
        this.originalDaysLate = originalDaysLate;
    }
}
