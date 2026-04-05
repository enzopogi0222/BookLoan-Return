package com.mycompany.bookloanandreturn.View;

import com.mycompany.bookloanandreturn.View.common.ViewStyles;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/** Main menu: loan, return, and transaction history. */
public class MainMenuView {
    private final Stage stage;
    private final Scene scene;
    private final Button loanBookButton;
    private final Button returnBookButton;
    private final Button transactionsButton;

    public MainMenuView(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("Book Loan and Return - Main Menu");
        this.stage.setMinWidth(ViewStyles.SCENE_WIDTH);
        this.stage.setMinHeight(ViewStyles.SCENE_HEIGHT);
        this.stage.setResizable(true);

        Font font = Font.font("Segoe UI", 14);
        Label titleLabel = new Label("Main Menu");
        titleLabel.setStyle(ViewStyles.TITLE_STYLE);

        Label subtitleLabel = new Label("Loan, return, and review activity");
        subtitleLabel.setStyle(ViewStyles.SUBTITLE_STYLE);

        loanBookButton = new Button("Loan Book");
        ViewStyles.styleGreenButton(loanBookButton, font);
        loanBookButton.setPrefWidth(220);

        returnBookButton = new Button("Return Book");
        ViewStyles.styleGreenButton(returnBookButton, font);
        returnBookButton.setPrefWidth(220);

        transactionsButton = new Button("Transactions");
        ViewStyles.styleGreenButton(transactionsButton, font);
        transactionsButton.setPrefWidth(220);

        VBox centerPanel = new VBox(14, titleLabel, subtitleLabel, loanBookButton, returnBookButton, transactionsButton);
        centerPanel.setAlignment(Pos.CENTER);
        centerPanel.setPadding(new Insets(28, 36, 28, 36));
        ViewStyles.styleCard(centerPanel);

        StackPane root = new StackPane(centerPanel);
        root.setPadding(new Insets(24));
        root.setStyle(ViewStyles.BACKGROUND_STYLE);

        scene = new Scene(root, ViewStyles.SCENE_WIDTH, ViewStyles.SCENE_HEIGHT);
        this.stage.setScene(scene);
    }

    public void addLoanBookListener(Runnable listener) {
        loanBookButton.setOnAction(e -> listener.run());
    }

    public void addReturnBookListener(Runnable listener) {
        returnBookButton.setOnAction(e -> listener.run());
    }

    public void addTransactionsListener(Runnable listener) {
        transactionsButton.setOnAction(e -> listener.run());
    }

    public void show() {
        ViewStyles.showScenePreservingState(stage, scene);
    }

    public Stage getStage() { return stage; }
}
