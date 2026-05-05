package com.mycompany.bookloanandreturn.Models;

/** Book row with copies available to loan (stock greater than zero). Shown in the loan ComboBox. */
public record AvailableBook(int bookId, String title, int stock, double cost) {
    @Override
    public String toString() {
        return title + " (available: " + stock + ", cost: ₱" + String.format("%.2f", cost) + ")";
    }
}
