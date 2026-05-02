package com.mycompany.bookloanandreturn.Models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Data model for a combined receipt with multiple books. */
public class MultiReceiptData {
    private String borrowerName;
    private String studentId;
    private LocalDate returnDate;
    private final List<ReceiptItem> items = new ArrayList<>();
    private String notes;

    public static class ReceiptItem {
        public int returnId;
        public int loanId;
        public String bookTitle;
        public LocalDate loanDate;
        public LocalDate dueDate;
        public long daysLate;
        public int fineAmount;
        public String bookCondition;
        public double bookCost;
        public int overdueFine;

        public ReceiptItem(int returnId, int loanId, String bookTitle, LocalDate loanDate, 
                          LocalDate dueDate, long daysLate, int fineAmount, String bookCondition, double bookCost, int overdueFine) {
            this.returnId = returnId;
            this.loanId = loanId;
            this.bookTitle = bookTitle;
            this.loanDate = loanDate;
            this.dueDate = dueDate;
            this.daysLate = daysLate;
            this.fineAmount = fineAmount;
            this.bookCondition = bookCondition;
            this.bookCost = bookCost;
            this.overdueFine = overdueFine;
        }
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public List<ReceiptItem> getItems() {
        return items;
    }

    public void addItem(ReceiptItem item) {
        items.add(item);
    }

    public int getTotalFine() {
        int total = 0;
        for (ReceiptItem item : items) {
            total += item.fineAmount;
        }
        return total;
    }

    public int getItemCount() {
        return items.size();
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean hasFines() {
        return getTotalFine() > 0;
    }
}
