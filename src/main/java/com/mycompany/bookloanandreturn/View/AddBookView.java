package com.mycompany.bookloanandreturn.View;

import com.mycompany.bookloanandreturn.View.common.BookFormView;
import javafx.scene.control.Alert;

/** View for the Add Book screen. Displays form fields and notifies listeners on submit. */
public class AddBookView extends BookFormView {

    public AddBookView() {
        super("Add Book", "Add Book");
    }

    public void addAddBookListener(Runnable listener) {
        addSaveListener(listener);
    }

    /** Show success message and clear form. */
    public void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        clearFields();
    }
}
