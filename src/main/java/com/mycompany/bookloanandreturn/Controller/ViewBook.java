package com.mycompany.bookloanandreturn.Controller;

import com.mycompany.bookloanandreturn.Models.Book;
import com.mycompany.bookloanandreturn.DatabaseConnection;
import com.mycompany.bookloanandreturn.View.ViewBookView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

public class ViewBook implements ActionListener {
    private final ViewBookView view;

    public ViewBook() {
        view = new ViewBookView();
        view.addRefreshListener(this);
        loadBooks();
        SwingUtilities.invokeLater(() -> view.show());
}

@Override
public void actionPerformed(ActionEvent e) {
    loadBooks();
}

private void loadBooks(){
    String sql = "SELECT bookName, author, genre, published_year, stock FROM book";
    List<Book> books = new ArrayList<>();
    try {
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            Book book = new Book();
            book.setBookName(rs.getString("bookName"));
            book.setAuthor(rs.getString("author"));
            book.setGenre(rs.getString("genre"));
            book.setPublishedYear(rs.getString("published_year"));
            book.setStock(rs.getInt("stock"));
            books.add(book);
        }
        rs.close();
        ps.close();
        conn.close();
        view.displayBooks(books);

    } catch (SQLException ex) {
        view.showError("Database error: " + ex.getMessage());
        ex.printStackTrace();
        }
    }
}
