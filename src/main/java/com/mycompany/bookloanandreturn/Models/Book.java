
package com.mycompany.bookloanandreturn.Models;
import com.mycompany.bookloanandreturn.DatabaseConnection;

public class Book {
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
       this.bookName = bookName;
   }
   
   public String getAuthor(){
       return author;
   }
   
   public void setAuthor(String author){
       if (author == null || author.trim().isEmpty()){
        throw new IllegalArgumentException("Author cannot be empty");
       }

       if (author.matches("^[a-zA-Z]+$")){
        throw new IllegalArgumentException("Author must contain only letters");
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
        this.genre = genre;
   }
   public String getPublishedYear(){
       return published_year;
   }
   
   public void setPublishedYear(String published_year){
    if (published_year == null || !published_year.matches("\\d+")){
        throw new IllegalArgumentException("Published Year must be contains only numbers");
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
           
}
