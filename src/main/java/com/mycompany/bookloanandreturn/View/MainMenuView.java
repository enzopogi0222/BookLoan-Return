package com.mycompany.bookloanandreturn.View;

import com.mycompany.bookloanandreturn.View.common.ViewStyles;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

/** Main menu with integrated dashboard stats panel. */
public class MainMenuView {
    private final Stage stage;
    private final Scene scene;
    private final Button loanBookButton;
    private final Button returnBookButton;
    private final Button transactionsButton;
    private final Button reportsButton;
    private Label activeLoansValue;
    private Label returnedTodayValue;
    private Label unpaidFinesValue;
    private Label totalBooksValue;

    public MainMenuView(Stage stage) {
        this.stage = stage;
        this.stage.setMinWidth(ViewStyles.SCENE_WIDTH);
        this.stage.setMinHeight(ViewStyles.SCENE_HEIGHT);
        this.stage.setResizable(true);

        Font font = Font.font("Segoe UI", 14);

        double logoSize = 80;
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

        Label titleLabel = new Label("Book Loan and Return System");
        titleLabel.setStyle(ViewStyles.TITLE_STYLE);

        // Stats Panel
        VBox statsPanel = createStatsPanel();

        // Menu Buttons
        loanBookButton = new Button("Loan Book");
        ViewStyles.stylePrimaryButton(loanBookButton, font);
        loanBookButton.setPrefWidth(200);

        returnBookButton = new Button("Return Book");
        ViewStyles.stylePrimaryButton(returnBookButton, font);
        returnBookButton.setPrefWidth(200);

        transactionsButton = new Button("Transactions");
        ViewStyles.stylePrimaryButton(transactionsButton, font);
        transactionsButton.setPrefWidth(200);

        reportsButton = new Button("Reports");
        ViewStyles.stylePrimaryButton(reportsButton, font);
        reportsButton.setPrefWidth(200);

        HBox buttonRow = new HBox(12, loanBookButton, returnBookButton, transactionsButton, reportsButton);
        buttonRow.setAlignment(Pos.CENTER);

        // Main layout
        VBox header = new VBox(8);
        header.setAlignment(Pos.CENTER);
        if (showLogo) {
            header.getChildren().add(logoView);
        }
        header.getChildren().addAll(institutionLabel, titleLabel);

        VBox centerPanel = new VBox(20, header, statsPanel, buttonRow);
        centerPanel.setAlignment(Pos.CENTER);
        centerPanel.setPadding(new Insets(24, 36, 24, 36));
        ViewStyles.styleCard(centerPanel);

        StackPane root = new StackPane(centerPanel);
        root.setPadding(new Insets(20));
        root.setStyle(ViewStyles.BACKGROUND_STYLE);

        scene = new Scene(root, ViewStyles.SCENE_WIDTH, ViewStyles.SCENE_HEIGHT);
        this.stage.setScene(scene);
    }

    private VBox activeLoansCard, returnedTodayCard, unpaidFinesCard, totalBooksCard;

    private VBox createStatsPanel() {
        // Create value labels first
        activeLoansValue = createValueLabel("0", "#2196F3");
        returnedTodayValue = createValueLabel("0", "#4CAF50");
        unpaidFinesValue = createValueLabel("₱0", "#F44336");
        totalBooksValue = createValueLabel("0", "#9C27B0");

        // Create stat cards
        activeLoansCard = createStatCard("Active Loans", activeLoansValue, "#2196F3");
        returnedTodayCard = createStatCard("Returned Today", returnedTodayValue, "#4CAF50");
        unpaidFinesCard = createStatCard("Unpaid Fines", unpaidFinesValue, "#F44336");
        totalBooksCard = createStatCard("Total Books", totalBooksValue, "#9C27B0");

        HBox statsRow = new HBox(12, activeLoansCard, returnedTodayCard, unpaidFinesCard, totalBooksCard);
        statsRow.setAlignment(Pos.CENTER);

        VBox panel = new VBox(8, statsRow);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(10, 0, 10, 0));

        return panel;
    }

    private Label createValueLabel(String initialValue, String color) {
        Label valueLabel = new Label(initialValue);
        valueLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        valueLabel.setStyle("-fx-text-fill: " + color + ";");
        return valueLabel;
    }

    private VBox createStatCard(String title, Label valueLabel, String color) {
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
        titleLabel.setStyle("-fx-text-fill: #666;");

        VBox textBox = new VBox(4, titleLabel, valueLabel);
        textBox.setAlignment(Pos.CENTER);

        VBox card = new VBox(textBox);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(12, 20, 12, 20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-border-radius: 10; -fx-border-color: " + color + "; -fx-border-width: 2; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");
        card.setPrefWidth(140);

        return card;
    }

    public void setActiveLoans(int count) {
        pulseCard(activeLoansCard);
        activeLoansValue.setText(String.valueOf(count));
    }

    public void setReturnedToday(int count) {
        pulseCard(returnedTodayCard);
        returnedTodayValue.setText(String.valueOf(count));
    }

    public void setUnpaidFines(int amount) {
        pulseCard(unpaidFinesCard);
        unpaidFinesValue.setText("₱" + amount);
    }

    public void setTotalBooks(int count) {
        pulseCard(totalBooksCard);
        totalBooksValue.setText(String.valueOf(count));
    }

    private void pulseCard(VBox card) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(150), card);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(1.05);
        scale.setToY(1.05);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        scale.play();
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

    public void addReportsListener(Runnable listener) {
        reportsButton.setOnAction(e -> listener.run());
    }

    public void show() {
        ViewStyles.showScenePreservingState(stage, scene);
    }

    public Stage getStage() { return stage; }
}
