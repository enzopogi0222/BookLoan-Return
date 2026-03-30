package com.mycompany.bookloanandreturn.util;

import com.mycompany.bookloanandreturn.Models.Book;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Shared logic: build a Book from form fields and validate stock. */
public final class BookFormHelper {

    private BookFormHelper() {}

    /**
     * Builds a Book from form values. Shows error via showError and returns null if stock is invalid.
     */
    public static Book fromForm(String bookName, String author, String genre, String publishedYear,
                               String stockText, Consumer<String> showError) {
        String normalizedBookName = normalizeText(bookName);
        String normalizedAuthor = normalizeText(author);
        String normalizedGenre = normalizeText(genre);
        String normalizedPublishedYear = normalizeNumeric(publishedYear);
        String normalizedStockText = normalizeStock(stockText);

        List<String> errors = new ArrayList<>();
        validateField(() -> new Book().setBookName(normalizedBookName), errors);
        validateField(() -> new Book().setAuthor(normalizedAuthor), errors);
        validateField(() -> new Book().setGenre(normalizedGenre), errors);
        validateField(() -> new Book().setPublishedYear(normalizedPublishedYear), errors);

        Integer parsedStock = null;
        try {
            parsedStock = Integer.parseInt(normalizedStockText);
            final int stockValue = parsedStock;
            validateField(() -> new Book().setStock(stockValue), errors);
        } catch (NumberFormatException ex) {
            errors.add("Please enter a valid number for Stock.");
        }

        if (!errors.isEmpty()) {
            showError.accept(buildErrorsMessage(errors));
            return null;
        }

        Book book = new Book();
        book.setBookName(normalizedBookName);
        book.setAuthor(normalizedAuthor);
        book.setGenre(normalizedGenre);
        book.setPublishedYear(normalizedPublishedYear);
        book.setStock(parsedStock);
        return book;
    }

    private static void validateField(Runnable validateAction, List<String> errors) {
        try {
            validateAction.run();
        } catch (IllegalArgumentException ex) {
            errors.add(ex.getMessage());
        }
    }

    private static String normalizeText(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFKC)
                .replaceAll("\\p{Cntrl}", "")
                .trim()
                .replaceAll("\\s+", " ");
        return normalized;
    }

    private static String normalizeNumeric(String value) {
        return normalizeText(value).replaceAll("\\s+", "");
    }

    private static String normalizeStock(String value) {
        String normalized = normalizeText(value)
                .replaceAll(",", "")
                .replaceAll("\\s+", "")
                .replaceAll("^0+(?!$)", "");
        return normalized.isEmpty() ? "1" : normalized;
    }

    private static String buildErrorsMessage(List<String> errors) {
        StringBuilder builder = new StringBuilder("Please fix the following:\n");
        for (String error : errors) {
            builder.append("- ").append(error).append("\n");
        }
        return builder.toString().trim();
    }
}
