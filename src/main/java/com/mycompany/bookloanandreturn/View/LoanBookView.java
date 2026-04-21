package com.mycompany.bookloanandreturn.View;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.mycompany.bookloanandreturn.Models.AvailableBook;
import com.mycompany.bookloanandreturn.View.common.ViewStyles;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class LoanBookView {
    private final Stage stage;
    private final Scene scene;
    private final List<ComboBox<AvailableBook>> bookCombos = new ArrayList<>();
    private final VBox booksContainer;
    private final TextField borrowerField;
    private final Label studentNameLabel;
    private final DatePicker loanDatePicker;
    private final DatePicker dueDatePicker;
    private Runnable loanListener;
    private Runnable backListener;
    private Runnable refreshListener;
    private Consumer<String> studentIdListener;

    private List<AvailableBook> masterBookList = new ArrayList<>();

    public LoanBookView(Stage stage) {
        this.stage = stage;
        stage.setMinWidth(ViewStyles.SCENE_WIDTH);
        stage.setMinHeight(ViewStyles.SCENE_HEIGHT);
        stage.setResizable(true);

        Font font = Font.font("Segoe UI", 14);

        Label titleLabel = new Label("Loan Books");
        titleLabel.setStyle(ViewStyles.TITLE_STYLE);

        Label subtitleLabel = new Label("Create a new loan record for a student");
        subtitleLabel.setStyle("-fx-text-fill: #60708a; -fx-font-size: 12px;");

        VBox header = new VBox(4, titleLabel, subtitleLabel);
        header.setAlignment(Pos.CENTER_LEFT);

        borrowerField = new TextField();
        borrowerField.setPromptText("Enter Student ID");
        ViewStyles.styleInput(borrowerField);
        borrowerField.setPrefWidth(420);

        borrowerField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (studentIdListener != null) {
                studentIdListener.accept(newVal.trim());
            }
        });

        studentNameLabel = new Label("");
        studentNameLabel.setStyle("-fx-text-fill: #5f6f87; -fx-font-style: italic; -fx-font-size: 12px;");
        studentNameLabel.setVisible(false);
        studentNameLabel.setManaged(false);

        booksContainer = new VBox(10);
        booksContainer.setPadding(new Insets(12));
        booksContainer.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 8; "
            + "-fx-border-color: #d6dfef; -fx-border-radius: 8; -fx-border-width: 1;");
        addBookSelectionRow();

        Button addMoreButton = new Button("Add Another Book");
        ViewStyles.stylePrimaryButton(addMoreButton, font);
        addMoreButton.setStyle("-fx-background-color: transparent; -fx-text-fill: " + ViewStyles.BRAND_BLUE
                + "; -fx-border-color: #9eb8e8; -fx-border-radius: 10; -fx-background-radius: 10;"
                + " -fx-font-weight: 700; -fx-cursor: hand;");
        addMoreButton.setPrefWidth(180);
        addMoreButton.setOnAction(e -> addBookSelectionRow());

        LocalDate today = LocalDate.now();
        loanDatePicker = new DatePicker(today);
        dueDatePicker = new DatePicker(today.plusDays(3));
        loanDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                dueDatePicker.setValue(newVal.plusDays(3));
            }
        });
        styleDatePicker(loanDatePicker);
        styleDatePicker(dueDatePicker);

        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(18);
        grid.setPadding(new Insets(6, 0, 0, 0));

        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setPrefWidth(120);
        labelColumn.setHalignment(HPos.RIGHT);
        ColumnConstraints valueColumn = new ColumnConstraints();
        valueColumn.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(labelColumn, valueColumn);

        VBox borrowerSection = new VBox(4, borrowerField, studentNameLabel);
        borrowerSection.setAlignment(Pos.CENTER_LEFT);
        addRow(grid, 0, "Student ID:", borrowerSection);

        Label booksLabel = new Label("Books to Loan:");
        booksLabel.setStyle(ViewStyles.LABEL_STYLE);
        grid.add(booksLabel, 0, 1);

        VBox bookSelectionSection = new VBox(12, booksContainer, addMoreButton);
        bookSelectionSection.setAlignment(Pos.TOP_LEFT);
        grid.add(bookSelectionSection, 1, 1);

        addRow(grid, 2, "Loan date:", loanDatePicker);
        addRow(grid, 3, "Due date:", dueDatePicker);

        Button loanButton = new Button("Confirm Loan");
        ViewStyles.stylePrimaryButton(loanButton, font);
        loanButton.setPrefWidth(170);
        loanButton.setOnAction(e -> {
            if (loanListener != null) loanListener.run();
        });

        Button refreshButton = new Button("Refresh List");
        ViewStyles.stylePrimaryButton(refreshButton, font);
        refreshButton.setPrefWidth(160);
        refreshButton.setOnAction(e -> {
            if (refreshListener != null) refreshListener.run();
        });

        Button backButton = new Button("Back");
        ViewStyles.stylePrimaryButton(backButton, font);
        backButton.setPrefWidth(130);
        backButton.setOnAction(e -> {
            if (backListener != null) backListener.run();
        });

        HBox actions = new HBox(12, backButton, refreshButton, loanButton);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPadding(new Insets(14, 0, 0, 0));

        VBox card = new VBox(18, header, grid, actions);
        card.setPadding(new Insets(26));
        card.setPrefWidth(ViewStyles.SCENE_WIDTH - 40);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setMinHeight(ViewStyles.SCENE_HEIGHT - 40);
        ViewStyles.styleCard(card);

        StackPane cardWrapper = new StackPane(card);
        cardWrapper.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(cardWrapper);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setPadding(new Insets(0));
        card.prefWidthProperty().bind(Bindings.createDoubleBinding(
            () -> Math.max(ViewStyles.SCENE_WIDTH - 40, scrollPane.getViewportBounds().getWidth()),
            scrollPane.viewportBoundsProperty()
        ));

        StackPane root = new StackPane(scrollPane);
        root.setPadding(new Insets(20));
        root.setStyle(ViewStyles.BACKGROUND_STYLE);

        scene = new Scene(root, ViewStyles.SCENE_WIDTH, ViewStyles.SCENE_HEIGHT);
        stage.setScene(scene);
    }

    private void addBookSelectionRow() {
        if (bookCombos.size() >= 3) {
            showError("Maximum of 3 books per loan transaction.");
            return;
        }

        ComboBox<AvailableBook> combo = new ComboBox<>();
        setupSearchableComboBox(combo);
        combo.setPrefWidth(392);

        Button removeBtn = new Button("✕");
        removeBtn.setPrefSize(24, 24);
        removeBtn.setStyle("-fx-text-fill: #98a4ba; -fx-background-color: transparent; "
            + "-fx-font-weight: bold; -fx-font-size: 11px; -fx-cursor: hand;");
        removeBtn.setOnMouseEntered(e -> removeBtn.setStyle("-fx-text-fill: #44526b; -fx-background-color: transparent; "
            + "-fx-font-weight: bold; -fx-font-size: 11px; -fx-cursor: hand;"));
        removeBtn.setOnMouseExited(e -> removeBtn.setStyle("-fx-text-fill: #98a4ba; -fx-background-color: transparent; "
            + "-fx-font-weight: bold; -fx-font-size: 11px; -fx-cursor: hand;"));

        HBox row = new HBox(8, combo, removeBtn);
        row.setAlignment(Pos.CENTER_LEFT);

        removeBtn.setOnAction(e -> {
            if (bookCombos.size() > 1) {
                bookCombos.remove(combo);
                booksContainer.getChildren().remove(row);
            }
        });

        bookCombos.add(combo);
        booksContainer.getChildren().add(row);
    }

    private void setupSearchableComboBox(ComboBox<AvailableBook> combo) {
        combo.setEditable(true);
        combo.setPromptText("Type to search book...");

        FilteredList<AvailableBook> filteredItems = new FilteredList<>(FXCollections.observableArrayList(masterBookList), p -> true);
        combo.setItems(filteredItems);

        combo.setConverter(new StringConverter<>() {
            @Override
            public String toString(AvailableBook book) {
                return book == null ? "" : book.title();
            }

            @Override
            public AvailableBook fromString(String string) {
                return masterBookList.stream()
                        .filter(b -> b.title().equalsIgnoreCase(string))
                        .findFirst().orElse(null);
            }
        });

        combo.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                filteredItems.setPredicate(book -> true);
                combo.hide();
                return;
            }

            if (combo.getSelectionModel().getSelectedItem() == null ||
                !combo.getSelectionModel().getSelectedItem().title().equalsIgnoreCase(newVal)) {

                filteredItems.setPredicate(book ->
                    book.title().toLowerCase().contains(newVal.toLowerCase())
                );

                if (!filteredItems.isEmpty()) {
                    combo.show();
                } else {
                    combo.hide();
                }
            }
        });

        combo.showingProperty().addListener((obs, wasShowing, isShowing) -> {
            if (!isShowing && combo.getEditor().getText() != null && !combo.getEditor().getText().isEmpty()) {
                String typedText = combo.getEditor().getText();
                AvailableBook matchedBook = masterBookList.stream()
                    .filter(b -> b.title().equalsIgnoreCase(typedText))
                    .findFirst().orElse(null);

                if (matchedBook != null) {
                    combo.getSelectionModel().select(matchedBook);
                } else {
                    combo.getSelectionModel().clearSelection();
                }
            }
        });
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
        this.masterBookList = books;
        for (ComboBox<AvailableBook> combo : bookCombos) {
            AvailableBook sel = combo.getSelectionModel().getSelectedItem();
            FilteredList<AvailableBook> filtered = (FilteredList<AvailableBook>) combo.getItems();

            ((javafx.collections.ObservableList<AvailableBook>)filtered.getSource()).setAll(books);

            if (sel != null) {
                AvailableBook newSel = books.stream()
                    .filter(b -> b.bookId() == sel.bookId())
                    .findFirst().orElse(null);
                combo.getSelectionModel().select(newSel);
            } else {
                combo.getSelectionModel().clearSelection();
                combo.getEditor().setText("");
            }
            filtered.setPredicate(book -> true);
        }
    }

    public void setStudentDisplayName(String name) {
        String value = name != null ? name.trim() : "";
        boolean hasName = !value.isEmpty();
        studentNameLabel.setText(hasName ? "Borrower: " + value : "");
        studentNameLabel.setVisible(hasName);
        studentNameLabel.setManaged(hasName);
    }

    public List<AvailableBook> getSelectedBooks() {
        List<AvailableBook> selected = new ArrayList<>();
        for (ComboBox<AvailableBook> combo : bookCombos) {
            AvailableBook b = combo.getValue();
            if (b != null) selected.add(b);
        }
        return selected;
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

    public void setStudentIdListener(Consumer<String> listener) {
        this.studentIdListener = listener;
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
