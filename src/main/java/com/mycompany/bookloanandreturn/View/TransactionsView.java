package com.mycompany.bookloanandreturn.View;

import com.mycompany.bookloanandreturn.Models.LoanTransaction;
import com.mycompany.bookloanandreturn.View.common.ViewStyles;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TransactionsView {
    private final Stage stage;
    private final Scene scene;
    private final TableView<LoanTransaction> table;
    private final TextField searchField;
    private Runnable refreshListener;
    private Runnable filterListener;
    private Runnable backListener;

    public TransactionsView(Stage stage) {
        this.stage = stage;
        stage.setTitle("Transaction history");
        stage.setMinWidth(ViewStyles.SCENE_WIDTH);
        stage.setMinHeight(ViewStyles.SCENE_HEIGHT);
        stage.setResizable(true);

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setFixedCellSize(34);
        table.setStyle(ViewStyles.TABLE_STYLE);
        table.setPlaceholder(new Label("No transactions yet"));

        TableColumn<LoanTransaction, String> idCol = new TableColumn<>("Loan #");
        idCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(String.valueOf(data.getValue().getLoanId())));

        TableColumn<LoanTransaction, String> bookCol = new TableColumn<>("Book");
        bookCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getBookTitle()));

        TableColumn<LoanTransaction, String> borrowerCol = new TableColumn<>("Borrower");
        borrowerCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getBorrowerName()));

        TableColumn<LoanTransaction, String> loanCol = new TableColumn<>("Loan date");
        loanCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getLoanDate()));

        TableColumn<LoanTransaction, String> dueCol = new TableColumn<>("Due date");
        dueCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getDueDate()));

        TableColumn<LoanTransaction, String> retCol = new TableColumn<>("Return date");
        retCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getReturnDate()));

        TableColumn<LoanTransaction, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getStatus()));

        TableColumn<LoanTransaction, String> notesCol = new TableColumn<>("Notes");
        notesCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                data.getValue().getNotes() != null ? data.getValue().getNotes() : ""));

        table.getColumns().add(idCol);
        table.getColumns().add(bookCol);
        table.getColumns().add(borrowerCol);
        table.getColumns().add(loanCol);
        table.getColumns().add(dueCol);
        table.getColumns().add(retCol);
        table.getColumns().add(statusCol);
        table.getColumns().add(notesCol);

        Label titleLabel = new Label("Transactions");
        titleLabel.setStyle(ViewStyles.TITLE_STYLE);

        Label searchLabel = new Label("Search:");
        searchLabel.setStyle(ViewStyles.LABEL_STYLE);
        searchField = new TextField();
        searchField.setPromptText("Filter by book, borrower, dates, status, or notes");
        ViewStyles.styleInput(searchField);
        searchField.setPrefWidth(400);
        searchField.textProperty().addListener((obs, o, n) -> fireFilterRequested());

        HBox searchRow = new HBox(10, searchLabel, searchField);
        searchRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button refreshButton = new Button("Refresh");
        ViewStyles.styleGreenButton(refreshButton);
        refreshButton.setPrefWidth(120);
        refreshButton.setOnAction(e -> fireRefreshRequested());

        Button backButton = new Button("Back");
        ViewStyles.styleGreenButton(backButton);
        backButton.setPrefWidth(120);
        backButton.setOnAction(e -> fireBackRequested());

        HBox bottom = new HBox(10, backButton, refreshButton);
        bottom.setAlignment(Pos.CENTER_RIGHT);

        BorderPane main = new BorderPane();
        main.setTop(new VBox(12, titleLabel, searchRow));
        main.setCenter(table);
        main.setBottom(bottom);
        main.setPadding(new Insets(20));

        VBox card = new VBox(main);
        card.setPadding(new Insets(10));
        ViewStyles.styleCard(card);

        StackPane root = new StackPane(card);
        root.setPadding(new Insets(18));
        root.setStyle(ViewStyles.BACKGROUND_STYLE);

        scene = new Scene(root, ViewStyles.SCENE_WIDTH, ViewStyles.SCENE_HEIGHT);
        stage.setScene(scene);
    }

    public void addRefreshListener(Runnable listener) {
        this.refreshListener = listener;
    }

    public void addFilterListener(Runnable listener) {
        this.filterListener = listener;
    }

    public void addBackListener(Runnable listener) {
        this.backListener = listener;
    }

    public String getSearchText() {
        return searchField.getText().trim().toLowerCase();
    }

    public void displayTransactions(List<LoanTransaction> rows) {
        table.getItems().setAll(rows);
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

    private void fireRefreshRequested() {
        if (refreshListener != null) refreshListener.run();
    }

    private void fireFilterRequested() {
        if (filterListener != null) filterListener.run();
    }

    private void fireBackRequested() {
        if (backListener != null) backListener.run();
    }
}
