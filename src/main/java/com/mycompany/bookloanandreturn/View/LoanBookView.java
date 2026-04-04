package com.mycompany.bookloanandreturn.View;

import com.mycompany.bookloanandreturn.Models.AvailableBook;
import com.mycompany.bookloanandreturn.View.common.ViewStyles;
import java.time.LocalDate;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class LoanBookView {
    private final Stage stage;
    private final Scene scene;
    private final ComboBox<AvailableBook> bookCombo;
    private final TextField borrowerField;
    private final DatePicker loanDatePicker;
    private final DatePicker dueDatePicker;
    private Runnable loanListener;
    private Runnable backListener;
    private Runnable refreshListener;

    public LoanBookView(Stage stage) {
        this.stage = stage;
        stage.setTitle("Loan Book");
        stage.setMinWidth(ViewStyles.SCENE_WIDTH);
        stage.setMinHeight(ViewStyles.SCENE_HEIGHT);
        stage.setResizable(true);

        Font font = Font.font("Segoe UI", 14);

        Label titleLabel = new Label("Loan a book");
        titleLabel.setStyle(ViewStyles.TITLE_STYLE);

        bookCombo = new ComboBox<>();
        bookCombo.setPromptText("Select a book");
        bookCombo.setPrefWidth(420);

        borrowerField = new TextField();
        borrowerField.setPromptText("Borrower name (e.g. Maria Santos)");
        ViewStyles.styleInput(borrowerField);
        borrowerField.setPrefWidth(420);

        loanDatePicker = new DatePicker(LocalDate.now());
        dueDatePicker = new DatePicker(LocalDate.now().plusWeeks(2));
        styleDatePicker(loanDatePicker);
        styleDatePicker(dueDatePicker);

        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(12);
        addRow(grid, 0, "Book:", bookCombo);
        addRow(grid, 1, "Borrower:", borrowerField);
        addRow(grid, 2, "Loan date:", loanDatePicker);
        addRow(grid, 3, "Due date:", dueDatePicker);

        Button loanButton = new Button("Confirm loan");
        ViewStyles.styleGreenButton(loanButton, font);
        loanButton.setPrefWidth(160);
        loanButton.setOnAction(e -> {
            if (loanListener != null) loanListener.run();
        });

        Button refreshButton = new Button("Refresh list");
        ViewStyles.styleGreenButton(refreshButton, font);
        refreshButton.setPrefWidth(140);
        refreshButton.setOnAction(e -> {
            if (refreshListener != null) refreshListener.run();
        });

        Button backButton = new Button("Back");
        ViewStyles.styleGreenButton(backButton, font);
        backButton.setPrefWidth(120);
        backButton.setOnAction(e -> {
            if (backListener != null) backListener.run();
        });

        HBox actions = new HBox(10, backButton, refreshButton, loanButton);
        actions.setAlignment(Pos.CENTER_RIGHT);

        VBox card = new VBox(18, titleLabel, grid, actions);
        card.setPadding(new Insets(24));
        ViewStyles.styleCard(card);

        StackPane root = new StackPane(card);
        root.setPadding(new Insets(24));
        root.setStyle(ViewStyles.BACKGROUND_STYLE);

        scene = new Scene(root, ViewStyles.SCENE_WIDTH, ViewStyles.SCENE_HEIGHT);
        stage.setScene(scene);
    }

    private static void addRow(GridPane grid, int row, String labelText, javafx.scene.Node field) {
        Label label = new Label(labelText);
        label.setStyle(ViewStyles.LABEL_STYLE);
        grid.add(label, 0, row);
        grid.add(field, 1, row);
    }

    private static void styleDatePicker(DatePicker picker) {
        picker.setPrefWidth(420);
        picker.setStyle(ViewStyles.INPUT_STYLE);
    }

    public void setAvailableBooks(List<AvailableBook> books) {
        AvailableBook sel = bookCombo.getSelectionModel().getSelectedItem();
        bookCombo.getItems().setAll(books);
        if (sel != null) {
            for (AvailableBook b : books) {
                if (b.bookId() == sel.bookId()) {
                    bookCombo.getSelectionModel().select(b);
                    return;
                }
            }
        }
    }

    public AvailableBook getSelectedBook() {
        return bookCombo.getSelectionModel().getSelectedItem();
    }

    public String getBorrowerName() {
        return borrowerField.getText().trim();
    }

    public LocalDate getLoanDate() {
        return loanDatePicker.getValue();
    }

    public LocalDate getDueDate() {
        return dueDatePicker.getValue();
    }

    public void addLoanListener(Runnable listener) {
        this.loanListener = listener;
    }

    public void addBackListener(Runnable listener) {
        this.backListener = listener;
    }

    public void addRefreshListener(Runnable listener) {
        this.refreshListener = listener;
    }

    public Stage getStage() {
        return stage;
    }

    public void show() {
        ViewStyles.showScenePreservingState(stage, scene);
    }

    public void showError(String message) {
        ViewStyles.showErrorAlert(message);
    }

    public void showSuccess(String message) {
        ViewStyles.showInfoAlert("Success", message);
    }
}
