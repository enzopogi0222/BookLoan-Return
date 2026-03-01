package com.mycompany.bookloanandreturn.Controller;

import com.mycompany.bookloanandreturn.Models.Book;
import com.mycompany.bookloanandreturn.DatabaseConnection;
import com.mycompany.bookloanandreturn.View.AddBookView;
import com.mycompany.bookloanandreturn.util.BookFormHelper;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.SwingUtilities;

/** Controller for Add Book. Coordinates the view and database. */
public class AddBook implements ActionListener {
    private final AddBookView view;

    /** Opens Add Book; when the window is closed, onReturnToMenu is run (e.g. show main menu). */
    public AddBook(Runnable onReturnToMenu) {
        view = new AddBookView();
        view.addAddBookListener(this);
        if (onReturnToMenu != null) {
            view.setOnWindowClose(() -> SwingUtilities.invokeLater(onReturnToMenu));
        }
        SwingUtilities.invokeLater(() -> view.show());
    }

    /** Opens Add Book with no "return" callback (e.g. when not launched from main menu). */
    public AddBook() {
        this(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Book book = BookFormHelper.fromForm(
                view.getBookName(), 
                view.getAuthor(), 
                view.getGenre(),
                view.getPublishedYear(), 
                view.getStockText(), 
                view::showError);
        if (book == null) return;

        String sql = "INSERT INTO book (bookName, author, genre, published_year, stock) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getBookName());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getGenre());
            ps.setString(4, book.getPublishedYear());
            ps.setInt(5, book.getStock());
            int rows = ps.executeUpdate();
            if (rows > 0) view.showSuccess("Book Added Successfully.");
            else view.showError("Failed to add book.");
        } catch (SQLException ex) {
            view.showError("Database error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
