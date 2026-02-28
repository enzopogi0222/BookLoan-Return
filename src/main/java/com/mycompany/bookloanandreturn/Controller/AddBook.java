
package com.mycompany.bookloanandreturn.Controller;

import com.mycompany.bookloanandreturn.Models.Book;
import com.mycompany.bookloanandreturn.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class AddBook {
    public AddBook(){
        Scanner scanner = new Scanner(System.in);
        Book book = new Book();
        
        System.out.print("Enter Book Name: ");
        String bookName = scanner.nextLine(); 
        book.setBookName(bookName);
        
        System.out.print("Enter Author: ");
        String author = scanner.nextLine();
        book.setAuthor(author);

        System.out.print("Enter Genre: ");
        String genre = scanner.nextLine();
        book.setGenre(genre);

        System.out.print("Enter Published Year: ");
        String published_year = scanner.nextLine();
        book.setPublishedYear(published_year);

        System.out.print("Enter Stock: ");
        int stock = scanner.nextInt();
        book.setStock(stock);


        
        String insertSQL = "INSERT INTO book (bookName, author, genre, published_year, stock)" + "VALUES (?, ?, ?, ?, ?)";
        
        try{
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(insertSQL);
            
            ps.setString(1, book.getBookName());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getGenre());
            ps.setString(4, book.getPublishedYear());
            ps.setInt(5, book.getStock());
            
            int rowsInserted = ps.executeUpdate();
            if (rowsInserted > 2) {
                System.out.println("Book Added Successfully");
            }
            ps.close();
            conn.close();
            
        } catch (SQLException e ) {
            e.printStackTrace();
        }
        scanner.close();
    }
}
