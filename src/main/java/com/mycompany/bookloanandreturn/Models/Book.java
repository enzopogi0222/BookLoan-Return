
package com.mycompany.bookloanandreturn.Models;
import java.time.Year;

public class Book {
    private static final int MAX_BOOK_NAME_LENGTH = 120;
    private static final int MAX_AUTHOR_LENGTH = 80;
    private static final int MAX_GENRE_LENGTH = 60;
    private static final int MIN_PUBLISHED_YEAR = 1450;
   private int bookId;
   private String bookName;
   private String author;
   private String genre;
   private String published_year;
   private int stock;
   
   public String getBookName(){
       return bookName;
       
   }
   
   public void setBookName(String bookName){
    if (bookName == null || bookName.trim().isEmpty()){
        throw new IllegalArgumentException("Book Name cannot be empty");
    }
    if (bookName.length() > MAX_BOOK_NAME_LENGTH){
        throw new IllegalArgumentException("Book Name must be at most " + MAX_BOOK_NAME_LENGTH + " characters.");
    }
       this.bookName = bookName;
   }
   
   public String getAuthor(){
       return author;
   }
   
   public void setAuthor(String author){
       if (author == null || author.trim().isEmpty()){
        throw new IllegalArgumentException("Author cannot be empty");
       }
    if (author.length() > MAX_AUTHOR_LENGTH){
     throw new IllegalArgumentException("Author must be at most " + MAX_AUTHOR_LENGTH + " characters.");
    }

    if (!author.matches("^[a-zA-Z][a-zA-Z .'-]*$")){
     throw new IllegalArgumentException("Author contains invalid characters.");
       }

       this.author = author;
   }
   
   public String getGenre(){
        return genre;
   }

   public void setGenre(String genre){
    if (genre == null || genre.trim().isEmpty()){
        throw new IllegalArgumentException("Genre cannot be empty");
    }
    if (genre.length() > MAX_GENRE_LENGTH){
        throw new IllegalArgumentException("Genre must be at most " + MAX_GENRE_LENGTH + " characters.");
    }
        this.genre = genre;
   }
   public String getPublishedYear(){
       return published_year;
   }
   
   public void setPublishedYear(String published_year){
    if (published_year == null || !published_year.matches("\\d+")){
        throw new IllegalArgumentException("Published Year must be contains only numbers");
    }
    int year = Integer.parseInt(published_year);
    int currentYear = Year.now().getValue();
    if (year < MIN_PUBLISHED_YEAR || year > currentYear){
        throw new IllegalArgumentException("Published Year must be between " + MIN_PUBLISHED_YEAR + " and " + currentYear + ".");
    }
       this.published_year = published_year;
   }
   
   public int getStock(){
       return stock;
   }
   
   public void setStock(int stock){
    if (stock <= 0){
        throw new IllegalArgumentException("Stock must be greater than 0");
    } 
       this.stock = stock;
   }

   public int getBookId() {
       return bookId;
   }

   public void setBookId(int bookId) {
       this.bookId = bookId;
   }
}
