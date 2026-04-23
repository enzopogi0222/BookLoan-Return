package com.mycompany.bookloanandreturn.Models;

public class OverdueBookReport {
    private int loanId;
    private String bookTitle;
    private String borrowerName;
    private long studentId;
    private String studentName;
    private String phone;
    private String loanDate;
    private String dueDate;
    private int daysOverdue;
    private int estimatedFine;

    public int getLoanId() { return loanId; }
    public void setLoanId(int loanId) { this.loanId = loanId; }
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    public String getBorrowerName() { return borrowerName; }
    public void setBorrowerName(String borrowerName) { this.borrowerName = borrowerName; }
    public long getStudentId() { return studentId; }
    public void setStudentId(long studentId) { this.studentId = studentId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getLoanDate() { return loanDate; }
    public void setLoanDate(String loanDate) { this.loanDate = loanDate; }
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public int getDaysOverdue() { return daysOverdue; }
    public void setDaysOverdue(int daysOverdue) { this.daysOverdue = daysOverdue; }
    public int getEstimatedFine() { return estimatedFine; }
    public void setEstimatedFine(int estimatedFine) { this.estimatedFine = estimatedFine; }
}
