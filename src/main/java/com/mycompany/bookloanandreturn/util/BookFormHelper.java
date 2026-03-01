package com.mycompany.bookloanandreturn.util;

import com.mycompany.bookloanandreturn.Models.Book;
import java.util.function.Consumer;

/** Shared logic: build a Book from form fields and validate stock. */
public final class BookFormHelper {

    private BookFormHelper() {}

    /**
     * Builds a Book from form values. Shows error via showError and returns null if stock is invalid.
     */
    public static Book fromForm(String bookName, String author, String genre, String publishedYear,
                               String stockText, Consumer<String> showError) {
        Book book = new Book();
        try {
            book.setBookName(bookName != null ? bookName.trim() : "");
            book.setAuthor(author != null ? author.trim() : "");
            book.setGenre(genre != null ? genre.trim() : "");
            book.setPublishedYear(publishedYear != null ? publishedYear.trim() : "");
            int stock = Integer.parseInt(stockText != null ? stockText.trim() : "0");
            book.setStock(stock);
        } catch (NumberFormatException ex) {
            showError.accept("Please enter a valid number for Stock.");
            return null;
        } catch (IllegalArgumentException ex) {
            showError.accept(ex.getMessage());
            return null;
        }
        return book;
    }
}
