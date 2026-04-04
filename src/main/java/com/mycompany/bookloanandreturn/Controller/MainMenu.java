package com.mycompany.bookloanandreturn.Controller;

import com.mycompany.bookloanandreturn.View.MainMenuView;
import javafx.stage.Stage;

public class MainMenu {
    private final MainMenuView view;

    public MainMenu(Stage primaryStage) {
        view = new MainMenuView(primaryStage);
        view.addAddBookListener(this::openAddBook);
        view.addViewBookListener(this::openViewBook);
        view.addLoanBookListener(this::openLoanBook);
        view.addReturnBookListener(this::openReturnBook);
        view.show();
    }

    public MainMenu() {
        this(new Stage());
    }

    private void openAddBook() {
        new AddBook(view.getStage(), view::show);
    }

    private void openViewBook() {
        new ViewBook(view.getStage(), view::show);
    }

    private void openLoanBook() {
        new LoanBook(view.getStage(), view::show);
    }

    private void openReturnBook() {
        new ReturnBook(view.getStage(), view::show);
    }
}
