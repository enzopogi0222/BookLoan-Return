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
    private List<Book> currentBooks = new ArrayList<>();

    public ViewBook() {
        view = new ViewBookView();
        view.addRefreshListener(this);
        view.addFilterListener(e -> applyFilter());
        loadBooks();
        SwingUtilities.invokeLater(() -> view.show());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        loadBooks();
    }

    private void applyFilter() {
        String query = view.getSearchText().toLowerCase();
        if (query.isEmpty()) {
            view.displayBooks(currentBooks);
            return;
        }
        List<Book> filtered = new ArrayList<>();
        for (Book b : currentBooks) {
            if (matchesSearch(b, query)) {
                filtered.add(b);
            }
        }
        view.displayBooks(filtered);
    }

    private boolean matchesSearch(Book b, String query) {
        return (b.getBookName() != null && b.getBookName().toLowerCase().contains(query))
                || (b.getAuthor() != null && b.getAuthor().toLowerCase().contains(query))
                || (b.getGenre() != null && b.getGenre().toLowerCase().contains(query))
                || (b.getPublishedYear() != null && b.getPublishedYear().contains(query));
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
            String bookName = rs.getString("bookName");
            String author = rs.getString("author");
            String genre = rs.getString("genre");
            String publishedYear = rs.getString("published_year");
            int stock = rs.getInt("stock");
            if (bookName == null || bookName.trim().isEmpty()) bookName = "—";
            if (author == null || author.trim().isEmpty() || !author.matches("^[a-zA-Z\\s]+$")) author = "Unknown";
            if (genre == null || genre.trim().isEmpty()) genre = "—";
            if (publishedYear == null || !publishedYear.matches("\\d+")) publishedYear = "0";
            if (stock < 0) stock = 0;
            book.setBookName(bookName);
            book.setAuthor(author);
            book.setGenre(genre);
            book.setPublishedYear(publishedYear);
            book.setStock(stock == 0 ? 1 : stock);
            books.add(book);
        }
        rs.close();
        ps.close();
        conn.close();
        currentBooks = books;
        applyFilter();

    } catch (SQLException ex) {
        view.showError("Database error: " + ex.getMessage());
        ex.printStackTrace();
        }
    }
}
