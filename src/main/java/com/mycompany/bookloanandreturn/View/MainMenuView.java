package com.mycompany.bookloanandreturn.View;

import com.mycompany.bookloanandreturn.View.common.ViewStyles;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
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
        this.stage.setTitle("RMMC — Book Loan and Return");
        this.stage.setMinWidth(ViewStyles.SCENE_WIDTH);
        this.stage.setMinHeight(ViewStyles.SCENE_HEIGHT);
        this.stage.setResizable(true);

        Font font = Font.font("Segoe UI", 14);

        double logoSize = 108;
        ImageView logoView = new ImageView();
        Image logo = ViewStyles.loadBrandLogo();
        boolean showLogo = logo != null && !logo.isError();
        if (showLogo) {
            logoView.setImage(logo);
            logoView.setFitWidth(logoSize);
            logoView.setFitHeight(logoSize);
            logoView.setPreserveRatio(true);
            logoView.setSmooth(true);
            double r = logoSize / 2;
            Circle clip = new Circle(r, r, r);
            logoView.setClip(clip);
        }

        Label institutionLabel = new Label("Ramon Magsaysay Memorial Colleges");
        institutionLabel.setStyle(ViewStyles.INSTITUTION_STYLE);

        Label titleLabel = new Label("Main Menu");
        titleLabel.setStyle(ViewStyles.TITLE_STYLE);

        Label subtitleLabel = new Label("Loan, return, and review activity");
        subtitleLabel.setStyle(ViewStyles.SUBTITLE_STYLE);

        loanBookButton = new Button("Loan Book");
        ViewStyles.stylePrimaryButton(loanBookButton, font);
        loanBookButton.setPrefWidth(220);

        returnBookButton = new Button("Return Book");
        ViewStyles.stylePrimaryButton(returnBookButton, font);
        returnBookButton.setPrefWidth(220);

        transactionsButton = new Button("Transactions");
        ViewStyles.stylePrimaryButton(transactionsButton, font);
        transactionsButton.setPrefWidth(220);

        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        if (showLogo) {
            header.getChildren().add(logoView);
        }
        header.getChildren().addAll(institutionLabel, titleLabel, subtitleLabel);

        VBox centerPanel = new VBox(16, header, loanBookButton, returnBookButton, transactionsButton);
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
