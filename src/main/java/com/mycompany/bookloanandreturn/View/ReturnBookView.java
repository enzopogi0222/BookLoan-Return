package com.mycompany.bookloanandreturn.View;

import com.mycompany.bookloanandreturn.Models.LoanRecord;
import com.mycompany.bookloanandreturn.View.common.ViewStyles;
import com.mycompany.bookloanandreturn.util.OverdueFine;
import java.time.LocalDate;
import java.util.List;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ReturnBookView {
    private final Stage stage;
    private final Scene scene;
    private final TableView<LoanRecord> table;
    private final TextField notesField;
    private Runnable returnListener;
    private Runnable backListener;
    private Runnable refreshListener;

    public ReturnBookView(Stage stage) {
        this.stage = stage;
        stage.setTitle("Return Book");
        stage.setMinWidth(ViewStyles.SCENE_WIDTH);
        stage.setMinHeight(ViewStyles.SCENE_HEIGHT);
        stage.setResizable(true);

        Font font = Font.font("Segoe UI", 14);

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setFixedCellSize(34);
        table.setStyle(ViewStyles.TABLE_STYLE);
        table.setPlaceholder(new Label("No active loans"));

        TableColumn<LoanRecord, String> titleCol = new TableColumn<>("Book");
        titleCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getBookTitle()));

        TableColumn<LoanRecord, String> borrowerCol = new TableColumn<>("Borrower");
        borrowerCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getBorrowerName()));

        TableColumn<LoanRecord, String> loanCol = new TableColumn<>("Loan date");
        loanCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getLoanDate()));

        TableColumn<LoanRecord, String> dueCol = new TableColumn<>("Due date");
        dueCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getDueDate()));

        TableColumn<LoanRecord, String> estFineCol = new TableColumn<>("Fine if returned today");
        estFineCol.setCellValueFactory(data -> {
            LocalDate due = data.getValue().getDueDateValue();
            if (due == null) {
                return new ReadOnlyStringWrapper("—");
            }
            int est = OverdueFine.estimatedFineIfReturnedToday(due);
            return new ReadOnlyStringWrapper(OverdueFine.formatPesos(est));
        });

        table.getColumns().addAll(titleCol, borrowerCol, loanCol, dueCol, estFineCol);

        Label titleLabel = new Label("Return a book");
        titleLabel.setStyle(ViewStyles.TITLE_STYLE);

        Label notesLabel = new Label("Return notes (optional):");
        notesLabel.setStyle(ViewStyles.LABEL_STYLE);
        notesField = new TextField();
        notesField.setPromptText("e.g. Good condition");
        ViewStyles.styleInput(notesField);
        notesField.setMaxWidth(480);

        Button returnButton = new Button("Record return");
        ViewStyles.stylePrimaryButton(returnButton, font);
        returnButton.setPrefWidth(150);
        returnButton.setOnAction(e -> {
            if (returnListener != null) returnListener.run();
        });

        Button refreshButton = new Button("Refresh");
        ViewStyles.stylePrimaryButton(refreshButton, font);
        refreshButton.setPrefWidth(120);
        refreshButton.setOnAction(e -> {
            if (refreshListener != null) refreshListener.run();
        });

        Button backButton = new Button("Back");
        ViewStyles.stylePrimaryButton(backButton, font);
        backButton.setPrefWidth(120);
        backButton.setOnAction(e -> {
            if (backListener != null) backListener.run();
        });

        HBox bottom = new HBox(10, backButton, refreshButton, returnButton);
        bottom.setAlignment(Pos.CENTER_RIGHT);

        HBox notesRow = new HBox(12, notesLabel, notesField);
        notesRow.setAlignment(Pos.CENTER_LEFT);
        VBox top = new VBox(10, titleLabel, notesRow);

        BorderPane main = new BorderPane();
        main.setTop(top);
        main.setCenter(table);
        main.setBottom(bottom);
        main.setPadding(new Insets(16));
        BorderPane.setMargin(table, new Insets(12, 0, 12, 0));

        VBox card = new VBox(main);
        card.setPadding(new Insets(10));
        ViewStyles.styleCard(card);

        StackPane root = new StackPane(card);
        root.setPadding(new Insets(18));
        root.setStyle(ViewStyles.BACKGROUND_STYLE);

        scene = new Scene(root, ViewStyles.SCENE_WIDTH, ViewStyles.SCENE_HEIGHT);
        stage.setScene(scene);
    }

    public void displayLoans(List<LoanRecord> loans) {
        table.getItems().setAll(loans);
    }

    public int getSelectedRowIndex() {
        return table.getSelectionModel().getSelectedIndex();
    }

    public LoanRecord getLoanAt(int index) {
        if (index < 0 || index >= table.getItems().size()) return null;
        return table.getItems().get(index);
    }

    public String getNotes() {
        return notesField.getText().trim();
    }

    public void addReturnListener(Runnable listener) {
        this.returnListener = listener;
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
