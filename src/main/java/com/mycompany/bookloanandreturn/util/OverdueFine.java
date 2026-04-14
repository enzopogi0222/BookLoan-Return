package com.mycompany.bookloanandreturn.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Overdue fine: ₱10 per calendar day after the due date (Philippine pesos). */
public final class OverdueFine {
    public static final int PESOS_PER_DAY_LATE = 10;

    private OverdueFine() {}

    /**
     * Fine charged when the book is returned on {@code returnDate}.
     * On or before due date: 0. Each full day after due date adds {@link #PESOS_PER_DAY_LATE}.
     */
    public static int finePesos(LocalDate dueDate, LocalDate returnDate) {
        if (dueDate == null || returnDate == null) {
            return 0;
        }
        long daysLate = ChronoUnit.DAYS.between(dueDate, returnDate);
        if (daysLate <= 0) {
            return 0;
        }
        long raw = daysLate * (long) PESOS_PER_DAY_LATE;
        if (raw > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) raw;
    }

    /** Fine that would apply if the borrower returned today (for active loans). */
    public static int estimatedFineIfReturnedToday(LocalDate dueDate) {
        return finePesos(dueDate, LocalDate.now());
    }

    public static String formatPesos(int amount) {
        return "₱" + amount;
    }
}
