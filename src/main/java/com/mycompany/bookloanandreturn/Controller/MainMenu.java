package com.mycompany.bookloanandreturn.Controller;

import com.mycompany.bookloanandreturn.View.MainMenuView;
import javafx.stage.Stage;

public class MainMenu {
    private final MainMenuView view;

    public MainMenu(Stage primaryStage) {
        view = new MainMenuView(primaryStage);
        view.addLoanBookListener(this::openLoanBook);
        view.addReturnBookListener(this::openReturnBook);
        view.addTransactionsListener(this::openTransactions);
        view.show();
    }

    public MainMenu() {
        this(new Stage());
    }

    private void openLoanBook() {
        new LoanBook(view.getStage(), view::show);
    }

    private void openReturnBook() {
        new ReturnBook(view.getStage(), view::show);
    }

    private void openTransactions() {
        new Transactions(view.getStage(), view::show);
    }
}
