package com.mycompany.bookloanandreturn.View.common;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Base for Add/Edit Book views: same form layout, colors, and window-close behavior. */
public abstract class BookFormView {
    protected final Stage stage;
    private final Scene scene;
    protected final TextField bookNameField;
    protected final TextField authorField;
    protected final TextField genreField;
    protected final TextField publishedYearField;
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

        GridPane panel = new GridPane();
        panel.setHgap(14);
        panel.setVgap(12);

        bookNameField = new TextField();
        authorField = new TextField();
        genreField = new TextField();
        publishedYearField = new TextField();
        stockField = new TextField();

        bookNameField.setPromptText("Enter book name");
        authorField.setPromptText("Enter author");
        genreField.setPromptText("Enter genre");
        publishedYearField.setPromptText("e.g. 2024");
        stockField.setPromptText("e.g. 5");

        ViewStyles.styleInput(bookNameField);
        ViewStyles.styleInput(authorField);
        ViewStyles.styleInput(genreField);
        ViewStyles.styleInput(publishedYearField);
        ViewStyles.styleInput(stockField);

        allFields = new TextField[]{bookNameField, authorField, genreField, publishedYearField, stockField};

        addRow(panel, 0, "Book Name:", bookNameField);
        addRow(panel, 1, "Author:", authorField);
        addRow(panel, 2, "Genre:", genreField);
        addRow(panel, 3, "Published Year:", publishedYearField);
        addRow(panel, 4, "Stock:", stockField);

        Button saveButton = new Button(buttonText);
        ViewStyles.styleGreenButton(saveButton);
        saveButton.setPrefWidth(150);
        saveButton.setOnAction(e -> {
            if (saveListener != null) {
                saveListener.run();
            }
        });

        Button backButton = new Button("Back");
        ViewStyles.styleGreenButton(backButton);
        backButton.setPrefWidth(120);
        backButton.setOnAction(e -> {
            if (backListener != null) {
                backListener.run();
            }
        });

        HBox actions = new HBox(10, backButton, saveButton);
        actions.setAlignment(Pos.CENTER_RIGHT);
        panel.add(actions, 1, 5);

        VBox card = new VBox(18, titleLabel, panel);
        card.setPadding(new Insets(24));
        ViewStyles.styleCard(card);

        StackPane root = new StackPane(card);
        root.setPadding(new Insets(24));
        root.setStyle(ViewStyles.BACKGROUND_STYLE);

        scene = new Scene(root, ViewStyles.SCENE_WIDTH, ViewStyles.SCENE_HEIGHT);
        stage.setScene(scene);
    }

    private static void addRow(GridPane panel, int row, String labelText, TextField field) {
        Label label = new Label(labelText);
        label.setStyle(ViewStyles.LABEL_STYLE);
        panel.add(label, 0, row);
        panel.add(field, 1, row);
    }

    protected void addSaveListener(Runnable listener) {
        this.saveListener = listener;
    }

    protected void addBackListener(Runnable listener) {
        this.backListener = listener;
    }

    public String getBookName() { return bookNameField.getText().trim(); }
    public String getAuthor() { return authorField.getText().trim(); }
    public String getGenre() { return genreField.getText().trim(); }
    public String getPublishedYear() { return publishedYearField.getText().trim(); }
    public String getStockText() { return stockField.getText().trim(); }
    public Stage getStage() { return stage; }
    public void show() {
        ViewStyles.showScenePreservingState(stage, scene);
    }

    public void showError(String message) {
        ViewStyles.showErrorAlert(message);
    }

    public void clearFields() {
        for (TextField f : allFields) {
            f.setText("");
        }
    }
}
