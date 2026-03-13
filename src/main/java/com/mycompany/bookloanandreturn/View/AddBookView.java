package com.mycompany.bookloanandreturn.View;

import com.mycompany.bookloanandreturn.View.common.BookFormView;
import com.mycompany.bookloanandreturn.View.common.ViewStyles;
import javafx.stage.Stage;

/** View for the Add Book screen. Displays form fields and notifies listeners on submit. */
public class AddBookView extends BookFormView {

    public AddBookView(Stage stage) {
        super(stage, "Add Book", "Add Book");
    }

    public void addAddBookListener(Runnable listener) {
        addSaveListener(listener);
    }

    public void addBackListener(Runnable listener) {
        super.addBackListener(listener);
    }

    /** Show success message and clear form. */
    public void showSuccess(String message) {
        ViewStyles.showInfoAlert("Success", message);
        clearFields();
    }
}
