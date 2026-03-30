package com.mycompany.bookloanandreturn.Controller;

import com.mycompany.bookloanandreturn.Models.Book;
import com.mycompany.bookloanandreturn.DatabaseConnection;
import com.mycompany.bookloanandreturn.View.EditBookView;
import com.mycompany.bookloanandreturn.util.BookFormHelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.stage.Stage;

public class EditBook implements Runnable {
    private final EditBookView view;

    /** Open edit form with the given book's data pre-filled. */
    public EditBook(Stage stage, Book toEdit, Runnable onReturnMenu) {
        view = new EditBookView(stage);
        if (toEdit != null) {
            view.setBookId(toEdit.getBookId());
            view.setBook(
                toEdit.getBookName(), 
                toEdit.getAuthor(), 
                toEdit.getGenre(),
                toEdit.getPublishedYear(), 
                toEdit.getStock());
        }
        view.addEditBookListener(this);
        if (onReturnMenu != null) {
            view.addBackListener(onReturnMenu);
        }
        view.show();
    }

    @Override
    public void run() {
        Book book = BookFormHelper.fromForm(
                view.getBookName(), 
            view.getAuthor(), 
                view.getGenre(),
                view.getPublishedYear(), 
                view.getStockText(), 
                view::showError);
        if (book == null) return;

        int bookId = view.getBookId();
        if (bookId <= 0) {
            view.showError("No book selected to update.");
            return;
        }

        String sql = "UPDATE book SET bookName = ?, author = ?, genre = ?, published_year = ?, stock = ? WHERE book_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getBookName());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getGenre());
            ps.setString(4, book.getPublishedYear());
            ps.setInt(5, book.getStock());
            ps.setInt(6, bookId);
            int rows = ps.executeUpdate();
            if (rows > 0) view.showSuccess("Book updated Successfully.");
            else view.showError("Failed to update book.");
        } catch (SQLException ex) {
            view.showError("Database error: " + ex.getMessage());
        }
    }
}
