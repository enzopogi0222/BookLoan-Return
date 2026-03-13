package com.mycompany.bookloanandreturn.View.common;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Base for Add/Edit Book views: same form layout, colors, and window-close behavior. */
public abstract class BookFormView {
    protected final Stage stage;
    protected final TextField bookNameField;
    protected final TextField authorField;
    protected final TextField genreField;
    protected final TextField publishedYearField;
    protected final TextField stockField;
    protected final TextField[] allFields;
    private Runnable saveListener;

    public BookFormView(String title, String buttonText) {
        stage = new Stage();
        stage.setTitle(title);

        GridPane panel = new GridPane();
        panel.setHgap(10);
        panel.setVgap(10);
        panel.setPadding(new Insets(20));
        panel.setStyle(ViewStyles.BACKGROUND_STYLE);

        bookNameField = new TextField();
        authorField = new TextField();
        genreField = new TextField();
        publishedYearField = new TextField();
        stockField = new TextField();
        allFields = new TextField[]{bookNameField, authorField, genreField, publishedYearField, stockField};

        addRow(panel, 0, "Book Name:", bookNameField);
        addRow(panel, 1, "Author:", authorField);
        addRow(panel, 2, "Genre:", genreField);
        addRow(panel, 3, "Published Year:", publishedYearField);
        addRow(panel, 4, "Stock:", stockField);

        Button saveButton = new Button(buttonText);
        ViewStyles.styleGreenButton(saveButton);
        saveButton.setOnAction(e -> {
            if (saveListener != null) {
                saveListener.run();
            }
        });
        panel.add(saveButton, 1, 5);

        VBox root = new VBox(panel);
        root.setStyle(ViewStyles.BACKGROUND_STYLE);

        Scene scene = new Scene(root, 520, 420);
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

    public void setOnWindowClose(Runnable onClose) {
        if (onClose != null) {
            stage.setOnHidden(event -> onClose.run());
        }
    }

    public String getBookName() { return bookNameField.getText().trim(); }
    public String getAuthor() { return authorField.getText().trim(); }
    public String getGenre() { return genreField.getText().trim(); }
    public String getPublishedYear() { return publishedYearField.getText().trim(); }
    public String getStockText() { return stockField.getText().trim(); }
    public Stage getStage() { return stage; }
    public void show() { stage.show(); }

    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void clearFields() {
        for (TextField f : allFields) {
            f.setText("");
        }
    }
}
