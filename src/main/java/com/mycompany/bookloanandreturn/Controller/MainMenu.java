package com.mycompany.bookloanandreturn.Controller;

import com.mycompany.bookloanandreturn.View.MainMenuView;
import javax.swing.SwingUtilities;

public class MainMenu {
    private final MainMenuView view;

    public MainMenu() {
        view = new MainMenuView();
        view.addAddBookListener(e -> openAddBook());
        view.addViewBookListener(e -> openViewBook());
        SwingUtilities.invokeLater(() -> view.show());
    }

    private void openAddBook() {
        view.hide();
        new AddBook(() -> view.show());
    }

    private void openViewBook() {
        new ViewBook();
    }
}
