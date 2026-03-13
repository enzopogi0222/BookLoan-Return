package com.mycompany.bookloanandreturn.Controller;

import com.mycompany.bookloanandreturn.View.MainMenuView;
import javafx.stage.Stage;

public class MainMenu {
    private final MainMenuView view;

    public MainMenu(Stage primaryStage) {
        view = new MainMenuView(primaryStage);
        view.addAddBookListener(this::openAddBook);
        view.addViewBookListener(this::openViewBook);
        view.show();
    }

    public MainMenu() {
        this(new Stage());
    }

    private void openAddBook() {
        view.hide();
        new AddBook(() -> view.show());
    }

    private void openViewBook() {
        new ViewBook();
    }
}
