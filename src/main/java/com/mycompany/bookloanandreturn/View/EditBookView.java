package com.mycompany.bookloanandreturn.View;

import com.mycompany.bookloanandreturn.View.common.BookFormView;
import com.mycompany.bookloanandreturn.View.common.ViewStyles;
import javafx.stage.Stage;

/** View for the Edit Book screen. Displays form fields and notifies listeners on save. */
public class EditBookView extends BookFormView {
    private int currentBookId;

    public EditBookView(Stage stage) {
        super(stage, "Edit Book", "Update Book");
    }

    public void addEditBookListener(Runnable listener) {
        addSaveListener(listener);
    }

    public void addBackListener(Runnable listener) {
        super.addBackListener(listener);
    }

    /** Author field value (controller uses getAuthorName). */
    public String getAuthorName() {
        return getAuthor();
    }

    public void setBookId(int id) { this.currentBookId = id; }
    public int getBookId() { return currentBookId; }

    /** Pre-fill the form with book data (e.g. when editing a selected book). */
    public void setBook(String bookName, String author, String genre, String publishedYear, int stock) {
        bookNameField.setText(bookName != null ? bookName : "");
        authorField.setText(author != null ? author : "");
        genreField.setText(genre != null ? genre : "");
        publishedYearField.setText(publishedYear != null ? publishedYear : "");
        stockField.setText(String.valueOf(stock));
    }

    /** Show success message (matches controller spelling). */
    public void showSucess(String message) {
        ViewStyles.showInfoAlert("Success", message);
    }
}
