package com.mycompany.bookloanandreturn.Models;

import java.time.LocalDate;

/** Active loan row for the return table (no matching book_return yet). */
public class LoanRecord {
    private int loanId;
    private int bookId;
    private String bookTitle;
    private String borrowerName;
    private String loanDate;
    private String dueDate;
    /** Parsed due date for fine calculation; may be null if not set. */
    private LocalDate dueDateValue;

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
}
