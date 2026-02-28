
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
       this.bookName = bookName;
   }
   
   public String getAuthor(){
       return author;
   }
   
   public void setAuthor(String author){
       this.author = author;
   }
   
   public String getGenre(){
        return genre;
   }

   public void setGenre(String genre){
        this.genre = genre;
   }
   public String getPublishedYear(){
       return published_year;
   }
   
   public void setPublishedYear(String published_year){
       this.published_year = published_year;
   }
   
   public int getStock(){
       return stock;
   }
   
   public void setStock(int stock){
       this.stock = stock;
   }
           
}
