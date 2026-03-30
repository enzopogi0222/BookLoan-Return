package com.mycompany.bookloanandreturn.View.common;

import java.time.LocalDate;
import java.util.function.UnaryOperator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Base for Add/Edit Book views: improved UI with icons and better layout. */
public abstract class BookFormView {
    protected final Stage stage;
    private final Scene scene;
    protected final TextField bookNameField;
    protected final TextField authorField;
    protected final TextField genreField;
    protected final DatePicker publishedDatePicker;
    protected final TextField stockField;
    protected final TextField[] allFields;
    private Runnable saveListener;
    private Runnable backListener;

    public BookFormView(Stage stage, String title, String buttonText) {
        this.stage = stage;
        stage.setTitle(title);
        stage.setMinWidth(ViewStyles.SCENE_WIDTH);
        stage.setMinHeight(ViewStyles.SCENE_HEIGHT);
        stage.setResizable(true);

        Label titleLabel = new Label(title);
        titleLabel.setStyle(ViewStyles.TITLE_STYLE);

        Label subtitleLabel = new Label("Complete the fields below to manage your catalog");
        subtitleLabel.setStyle(ViewStyles.SUBTITLE_STYLE);

        GridPane panel = new GridPane();
        panel.setHgap(20);
        panel.setVgap(18);
        panel.setAlignment(Pos.CENTER);

        bookNameField = new TextField();
        authorField = new TextField();
        genreField = new TextField();
        publishedDatePicker = new DatePicker();
        stockField = new TextField();

        bookNameField.setPromptText("Enter book name");
        authorField.setPromptText("Enter author name");
        genreField.setPromptText("Enter genre");
        publishedDatePicker.setPromptText("Select date");
        stockField.setPromptText("1");

        ViewStyles.styleInput(bookNameField);
        ViewStyles.styleInput(authorField);
        ViewStyles.styleInput(genreField);
        ViewStyles.styleDatePicker(publishedDatePicker);
        publishedDatePicker.setEditable(false);
        publishedDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(empty || item.isAfter(LocalDate.now()));
            }
        });
        
        ViewStyles.styleInput(stockField);
        applyNumericFieldFilter(stockField, 5);
        stockField.setText("1");
        stockField.setAlignment(Pos.CENTER);
        stockField.setPrefWidth(60);

        allFields = new TextField[]{bookNameField, authorField, genreField, stockField};

        addRow(panel, 0, "📖 Book Name:", bookNameField);
        addRow(panel, 1, "👤 Author:", authorField);
        addRow(panel, 2, "🏷️ Genre:", genreField);
        addDateRow(panel, 3, "📅 Published Date:", publishedDatePicker);
        addStockRow(panel, 4, "📦 Stock:");

        Button saveButton = new Button("💾 " + buttonText);
        ViewStyles.styleGreenButton(saveButton);
        saveButton.setPrefWidth(160);
        saveButton.setOnAction(e -> {
            if (saveListener != null) {
                saveListener.run();
            }
        });

        Button backButton = new Button("⬅ Back");
        ViewStyles.styleSecondaryButton(backButton);
        backButton.setPrefWidth(120);
        backButton.setOnAction(e -> {
            if (backListener != null) {
                backListener.run();
            }
        });

        HBox actions = new HBox(15, backButton, saveButton);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPadding(new Insets(10, 0, 0, 0));
        panel.add(actions, 1, 5);

        VBox card = new VBox(24, new VBox(6, titleLabel, subtitleLabel), panel);
        card.setPadding(new Insets(40));
        card.setMaxWidth(600);
        card.setAlignment(Pos.TOP_CENTER);
        ViewStyles.styleCard(card);

        StackPane root = new StackPane(card);
        root.setPadding(new Insets(30));
        root.setStyle(ViewStyles.BACKGROUND_STYLE);

        scene = new Scene(root, ViewStyles.SCENE_WIDTH, ViewStyles.SCENE_HEIGHT);
        stage.setScene(scene);
    }

    private static void addRow(GridPane panel, int row, String labelText, TextField field) {
        Label label = new Label(labelText);
        label.setStyle(ViewStyles.LABEL_STYLE);
        GridPane.setHgrow(field, Priority.ALWAYS);
        panel.add(label, 0, row);
        panel.add(field, 1, row);
    }

    private static void addDateRow(GridPane panel, int row, String labelText, DatePicker datePicker) {
        Label label = new Label(labelText);
        label.setStyle(ViewStyles.LABEL_STYLE);
        datePicker.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(datePicker, Priority.ALWAYS);
        panel.add(label, 0, row);
        panel.add(datePicker, 1, row);
    }

    private void addStockRow(GridPane panel, int row, String labelText) {
        Label label = new Label(labelText);
        label.setStyle(ViewStyles.LABEL_STYLE);

        Button minusButton = new Button("-");
        ViewStyles.styleSecondaryButton(minusButton);
        minusButton.setPrefSize(36, 36);
        minusButton.setOnAction(e -> stockField.setText(String.valueOf(Math.max(1, getStockValue() - 1))));

        Button plusButton = new Button("+");
        ViewStyles.styleSecondaryButton(plusButton);
        plusButton.setPrefSize(36, 36);
        plusButton.setOnAction(e -> stockField.setText(String.valueOf(getStockValue() + 1)));

        HBox stockControl = new HBox(10, minusButton, stockField, plusButton);
        stockControl.setAlignment(Pos.CENTER_LEFT);

        panel.add(label, 0, row);
        panel.add(stockControl, 1, row);
    }

    private int getStockValue() {
        try {
            int value = Integer.parseInt(stockField.getText().trim());
            return Math.max(1, value);
        } catch (NumberFormatException ex) {
            return 1;
        }
    }

    private static void applyNumericFieldFilter(TextField field, int maxLength) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getControlNewText();
            if (text.length() > maxLength) {
                return null;
            }
            return text.matches("\\d*") ? change : null;
        };
        field.setTextFormatter(new TextFormatter<>(filter));
    }

    protected void addSaveListener(Runnable listener) { this.saveListener = listener; }
    protected void addBackListener(Runnable listener) { this.backListener = listener; }

    public String getBookName() { return bookNameField.getText().trim(); }
    public String getAuthor() { return authorField.getText().trim(); }
    public String getGenre() { return genreField.getText().trim(); }
    public String getPublishedYear() {
        LocalDate selectedDate = publishedDatePicker.getValue();
        return selectedDate != null ? String.valueOf(selectedDate.getYear()) : "";
    }
    public String getStockText() { return stockField.getText().trim(); }
    public Stage getStage() { return stage; }
    public void show() { ViewStyles.showScenePreservingState(stage, scene); }

    public void showError(String message) {
        ViewStyles.showErrorAlert(message);
    }

    public void clearFields() {
        for (TextField f : allFields) {
            f.setText("");
        }
        publishedDatePicker.setValue(null);
        stockField.setText("1");
    }

    protected void setPublishedYear(String publishedYear) {
        if (publishedYear == null || publishedYear.trim().isEmpty()) {
            publishedDatePicker.setValue(null);
            return;
        }
        try {
            int year = Integer.parseInt(publishedYear.trim());
            publishedDatePicker.setValue(LocalDate.of(year, 1, 1));
        } catch (NumberFormatException ex) {
            publishedDatePicker.setValue(null);
        }
    }
}
