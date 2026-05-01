package com.mycompany.bookloanandreturn.Models;

public class MonthlyStatistics {
    private int year;
    private String month;
    private int loansCount;
    private int returnsCount;
    private int overdueReturns;
    private int totalFinesCollected;

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
    public int getLoansCount() { return loansCount; }
    public void setLoansCount(int loansCount) { this.loansCount = loansCount; }
    public int getReturnsCount() { return returnsCount; }
    public void setReturnsCount(int returnsCount) { this.returnsCount = returnsCount; }
    public int getOverdueReturns() { return overdueReturns; }
    public void setOverdueReturns(int overdueReturns) { this.overdueReturns = overdueReturns; }
    public int getTotalFinesCollected() { return totalFinesCollected; }
    public void setTotalFinesCollected(int totalFinesCollected) { this.totalFinesCollected = totalFinesCollected; }
    public String getMonthYear() { return month + " " + year; }
}
