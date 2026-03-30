package com.mycompany.bookloanandreturn.Controller;

import com.mycompany.bookloanandreturn.Models.Book;
import com.mycompany.bookloanandreturn.DatabaseConnection;
import com.mycompany.bookloanandreturn.View.ViewBookView;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import javafx.stage.Stage;

public class ViewBook implements Runnable {
    private static final int MAX_BOOK_NAME_LENGTH = 120;
    private static final int MAX_AUTHOR_LENGTH = 80;
    private static final int MAX_GENRE_LENGTH = 60;
    private static final int MIN_PUBLISHED_YEAR = 1450;

    private final ViewBookView view;
    private final Runnable onReturnMenu;
    private List<Book> currentBooks = new ArrayList<>();
    /** Books currently shown in the table (after filter). Used to get selected book for edit. */
    private List<Book> displayedBooks = new ArrayList<>();

    public ViewBook(Stage stage, Runnable onReturnMenu) {
        view = new ViewBookView(stage);
        this.onReturnMenu = onReturnMenu;
        view.addRefreshListener(this);
        view.addFilterListener(this::applyFilter);
        view.addEditListener(this::openEditBook);
        view.addBackListener(this::goBack);
        loadBooks();
        view.show();
    }

    private void openEditBook() {
        int idx = view.getSelectedRowIndex();
        if (idx < 0) {
            view.showError("Please select a book to edit.");
            return;
        }
        if (idx >= displayedBooks.size()) return;
        Book selected = displayedBooks.get(idx);
        new EditBook(view.getStage(), selected, () -> loadBooks());
    }

    private void goBack() {
        if (onReturnMenu != null) {
            onReturnMenu.run();
        }
    }

    @Override
    public void run() {
        loadBooks();
    }

    private void applyFilter() {
        String query = view.getSearchText().toLowerCase();
        if (query.isEmpty()) {
            displayedBooks = new ArrayList<>(currentBooks);
            view.displayBooks(displayedBooks);
            return;
        }
        List<Book> filtered = new ArrayList<>();
        for (Book b : currentBooks) {
            if (matchesSearch(b, query)) filtered.add(b);
        }
        displayedBooks = filtered;
        view.displayBooks(displayedBooks);
    }

    private boolean matchesSearch(Book b, String query) {
        return (b.getBookName() != null && b.getBookName().toLowerCase().contains(query))
                || (b.getAuthor() != null && b.getAuthor().toLowerCase().contains(query))
                || (b.getGenre() != null && b.getGenre().toLowerCase().contains(query))
                || (b.getPublishedYear() != null && b.getPublishedYear().contains(query));
    }

    private void loadBooks() {
        String sql = "SELECT book_id, bookName, author, genre, published_year, stock FROM book";
        List<Book> books = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Book book = new Book();
                book.setBookId(rs.getInt("book_id"));
                String bookName = sanitizeBookName(rs.getString("bookName"));
                String author = sanitizeAuthor(rs.getString("author"));
                String genre = sanitizeGenre(rs.getString("genre"));
                String publishedYear = sanitizePublishedYear(rs.getString("published_year"));
                int stock = sanitizeStock(rs.getInt("stock"));

                book.setBookName(bookName);
                book.setAuthor(author);
                book.setGenre(genre);
                book.setPublishedYear(publishedYear);
                book.setStock(stock);
                books.add(book);
            }
            currentBooks = books;
            applyFilter();
        } catch (SQLException ex) {
            view.showError("Database error: " + ex.getMessage());
        }
    }

    private static String sanitizeBookName(String value) {
        String cleaned = cleanText(value);
        if (cleaned.isEmpty()) {
            return "Untitled";
        }
        return truncate(cleaned, MAX_BOOK_NAME_LENGTH);
    }

    private static String sanitizeAuthor(String value) {
        String cleaned = cleanText(value);
        if (cleaned.isEmpty()) {
            return "Unknown";
        }
        cleaned = truncate(cleaned, MAX_AUTHOR_LENGTH);
        if (!cleaned.matches("^[a-zA-Z][a-zA-Z .'-]*$")) {
            return "Unknown";
        }
        return cleaned;
    }

    private static String sanitizeGenre(String value) {
        String cleaned = cleanText(value);
        if (cleaned.isEmpty()) {
            return "General";
        }
        return truncate(cleaned, MAX_GENRE_LENGTH);
    }

    private static String sanitizePublishedYear(String value) {
        int currentYear = Year.now().getValue();
        if (value == null || !value.trim().matches("\\d+")) {
            return String.valueOf(currentYear);
        }
        int year = Integer.parseInt(value.trim());
        if (year < MIN_PUBLISHED_YEAR || year > currentYear) {
            return String.valueOf(currentYear);
        }
        return String.valueOf(year);
    }

    private static int sanitizeStock(int stock) {
        return stock <= 0 ? 1 : stock;
    }

    private static String cleanText(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().replaceAll("\\s+", " ");
    }

    private static String truncate(String value, int maxLength) {
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }
}
