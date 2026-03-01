package com.mycompany.bookloanandreturn.View;

import com.mycompany.bookloanandreturn.View.common.BookFormView;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

/** View for the Add Book screen. Displays form fields and notifies listeners on submit. */
public class AddBookView extends BookFormView {

    public AddBookView() {
        super("Add Book", "Add Book");
    }

    public void addAddBookListener(ActionListener listener) {
        addSaveListener(listener);
    }

    /** Show success message and clear form. */
    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(frame, message);
        clearFields();
    }
}
