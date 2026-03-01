package com.mycompany.bookloanandreturn.Controller;

import com.mycompany.bookloanandreturn.Models.Book;
import com.mycompany.bookloanandreturn.DatabaseConnection;
import com.mycompany.bookloanandreturn.View.EditBookView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import javax.swing.SwingUtilities;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EditBook implements ActionListener {
    private final EditBookView view;

    /** Open edit form with the given book's data pre-filled. */
    public EditBook(Book toEdit, Runnable onReturnMenu) {
        view = new EditBookView();
        if (toEdit != null) {
            view.setBookId(toEdit.getBookId());
            view.setBook(
                toEdit.getBookName(),
                toEdit.getAuthor(),
                toEdit.getGenre(),
                toEdit.getPublishedYear(),
                toEdit.getStock()
            );
        }
        view.addEditBookListener(this);
        if (onReturnMenu != null) {
            view.setOnWindowClose(() -> SwingUtilities.invokeLater(onReturnMenu));
        }
        SwingUtilities.invokeLater(() -> view.show());
    }

    /** Open edit form with no pre-filled data. */
    public EditBook(Runnable onReturnMenu) {
        this(null, onReturnMenu);
    }

    public EditBook() {
        this(null, null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Book book = new Book();
        try {
            book.setBookName(view.getBookName());
            book.setAuthor(view.getAuthorName());
            book.setGenre(view.getGenre());
            book.setPublishedYear(view.getPublishedYear());
            int stock = Integer.parseInt(view.getStockText());
            book.setStock(stock);
        } catch (NumberFormatException ex) {
            view.showError("Please enter a valid number for Stock");
            return;
        } catch (IllegalArgumentException ex) {
            view.showError(ex.getMessage());
            return;
        }
        
        int bookId = view.getBookId();
        if (bookId <= 0) {
            view.showError("No book selected to update.");
            return;
        }
        String updateSQL = "UPDATE book SET bookName = ?, author = ?, genre = ?, published_year = ?, stock = ? WHERE book_id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(updateSQL);
            ps.setString(1, book.getBookName());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getGenre());
            ps.setString(4, book.getPublishedYear());
            ps.setInt(5, book.getStock());
            ps.setInt(6, bookId);
            
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0){
                view.showSucess("Book updated Successfully.");
            } else {
                view.showError("Failed to update book.");
            }
            ps.close();
            conn.close();
       } catch (SQLException ex) {
           view.showError("Database error: " + ex.getMessage());
           ex.printStackTrace();
       }
    }
}
