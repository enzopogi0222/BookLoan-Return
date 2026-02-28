package com.mycompany.bookloanandreturn.Controller;

import com.mycompany.bookloanandreturn.Models.Book;
import com.mycompany.bookloanandreturn.DatabaseConnection;
import com.mycompany.bookloanandreturn.View.AddBookView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import javax.swing.SwingUtilities;
import java.sql.PreparedStatement;
import java.sql.SQLException;
/**
 * Controller for Add Book. Coordinates the view and database.
 */
public class AddBook implements ActionListener {
    private final AddBookView view;

    public AddBook() {
        view = new AddBookView();
        view.addAddBookListener(this);
        SwingUtilities.invokeLater(() -> view.show());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Book book = new Book();
        try {
            book.setBookName(view.getBookName());
            book.setAuthor(view.getAuthor());
            book.setGenre(view.getGenre());
            book.setPublishedYear(view.getPublishedYear());
            int stock = Integer.parseInt(view.getStockText());
            book.setStock(stock);
        } catch (NumberFormatException ex) {
            view.showError("Please enter a valid number for Stock.");
            return;
        } catch (IllegalArgumentException ex) {
            view.showError(ex.getMessage());
            return;
        }

        String insertSQL = "INSERT INTO book (bookName, author, genre, published_year, stock) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(insertSQL);
            ps.setString(1, book.getBookName());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getGenre());
            ps.setString(4, book.getPublishedYear());
            ps.setInt(5, book.getStock());

            int rowsInserted = ps.executeUpdate();
            if (rowsInserted > 0) {
                view.showSuccess("Book Added Successfully");
            } else {
                view.showError("Failed to add book.");
            }
            ps.close();
            conn.close();
        } catch (SQLException ex) {
            view.showError("Database error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
