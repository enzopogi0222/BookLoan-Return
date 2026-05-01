package com.mycompany.bookloanandreturn.Models;

public class FineCollectionReport {
    private int loanId;
    private String bookTitle;
    private String borrowerName;
    private String returnDate;
    private int fineAmount;
    private int amountPaid;
    private boolean finePaid;
    private String paymentStatus;

    public int getLoanId() { return loanId; }
    public void setLoanId(int loanId) { this.loanId = loanId; }
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    public String getBorrowerName() { return borrowerName; }
    public void setBorrowerName(String borrowerName) { this.borrowerName = borrowerName; }
    public String getReturnDate() { return returnDate; }
    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }
    public int getFineAmount() { return fineAmount; }
    public void setFineAmount(int fineAmount) { this.fineAmount = fineAmount; }
    public int getAmountPaid() { return amountPaid; }
    public void setAmountPaid(int amountPaid) { this.amountPaid = amountPaid; }
    public int getRemainingBalance() { return fineAmount - amountPaid; }
    public boolean isFinePaid() { return finePaid; }
    public void setFinePaid(boolean finePaid) { this.finePaid = finePaid; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}
