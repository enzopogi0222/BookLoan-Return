package com.mycompany.bookloanandreturn.View;

import com.mycompany.bookloanandreturn.Models.Book;
import com.mycompany.bookloanandreturn.View.common.ViewStyles;
import java.util.List;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ViewBookView {
    private final Stage stage;
    private final Scene scene;
    private final TableView<Book> table;
    private final TextField searchField;
    private Runnable refreshListener;
    private Runnable filterListener;
    private Runnable editListener;
    private Runnable backListener;

    public ViewBookView(Stage stage) {
        this.stage = stage;
        stage.setTitle("View Books");
        stage.setMinWidth(ViewStyles.SCENE_WIDTH);
        stage.setMinHeight(ViewStyles.SCENE_HEIGHT);
        stage.setResizable(true);

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setFixedCellSize(34);
        table.setStyle(ViewStyles.TABLE_STYLE);
        table.setPlaceholder(new Label("No books found"));

        TableColumn<Book, String> nameColumn = new TableColumn<>("Book Name");
        nameColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getBookName()));

        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getAuthor()));

        TableColumn<Book, String> genreColumn = new TableColumn<>("Genre");
        genreColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getGenre()));

        TableColumn<Book, String> yearColumn = new TableColumn<>("Published Year");
        yearColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getPublishedYear()));

        TableColumn<Book, String> stockColumn = new TableColumn<>("Stock");
        stockColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(String.valueOf(data.getValue().getStock())));

        TableColumn<Book, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            {
                ViewStyles.styleGreenButton(editButton);
                editButton.setOnAction(e -> {
                    int rowIndex = getIndex();
                    if (rowIndex >= 0 && rowIndex < getTableView().getItems().size()) {
                        Book selected = getTableView().getItems().get(rowIndex);
                        getTableView().getSelectionModel().select(selected);
                        fireEditRequested();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : editButton);
            }
        });
        actionColumn.setPrefWidth(90);

        table.getColumns().addAll(nameColumn, authorColumn, genreColumn, yearColumn, stockColumn, actionColumn);

        Label titleLabel = new Label("Book Library");
        titleLabel.setStyle(ViewStyles.TITLE_STYLE);

        Label searchLabel = new Label("Search:");
        searchLabel.setStyle(ViewStyles.LABEL_STYLE);
        searchField = new TextField();
        searchField.setPromptText("Search by title, author, genre, or year");
        ViewStyles.styleInput(searchField);
        searchField.setPrefWidth(360);
        searchField.textProperty().addListener((obs, oldText, newText) -> fireFilterRequested());

        HBox searchPanel = new HBox(10, searchLabel, searchField);
        searchPanel.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button refreshButton = new Button("Refresh");
        ViewStyles.styleGreenButton(refreshButton);
        refreshButton.setPrefWidth(130);
        refreshButton.setOnAction(e -> fireRefreshRequested());

        Button editButton = new Button("Edit");
        ViewStyles.styleGreenButton(editButton);
        editButton.setPrefWidth(130);
        editButton.setOnAction(e -> fireEditRequested());

        Button backButton = new Button("Back");
        ViewStyles.styleGreenButton(backButton);
        backButton.setPrefWidth(120);
        backButton.setOnAction(e -> fireBackRequested());

        HBox bottomPanel = new HBox(10, backButton, refreshButton, editButton);
        bottomPanel.setAlignment(Pos.CENTER_RIGHT);

        BorderPane mainPanel = new BorderPane();
        mainPanel.setTop(new VBox(12, titleLabel, searchPanel));
        mainPanel.setCenter(table);
        mainPanel.setBottom(bottomPanel);
        mainPanel.setPadding(new Insets(20));

        VBox card = new VBox(mainPanel);
        card.setPadding(new Insets(10));
        ViewStyles.styleCard(card);

        StackPane root = new StackPane(card);
        root.setPadding(new Insets(18));
        root.setStyle(ViewStyles.BACKGROUND_STYLE);

        scene = new Scene(root, ViewStyles.SCENE_WIDTH, ViewStyles.SCENE_HEIGHT);
        stage.setScene(scene);
    }

    public void addRefreshListener(Runnable listener) { this.refreshListener = listener; }
    public void addFilterListener(Runnable listener) { this.filterListener = listener; }
    public void addEditListener(Runnable listener) { this.editListener = listener; }
    public void addBackListener(Runnable listener) { this.backListener = listener; }
    public String getSearchText() { return searchField.getText().trim(); }
    public int getSelectedRowIndex() { return table.getSelectionModel().getSelectedIndex(); }
    public Stage getStage() { return stage; }

    private void fireRefreshRequested() {
        if (refreshListener != null) refreshListener.run();
    }

    private void fireFilterRequested() {
        if (filterListener != null) filterListener.run();
    }

    private void fireEditRequested() {
        if (editListener != null) editListener.run();
    }

    private void fireBackRequested() {
        if (backListener != null) backListener.run();
    }

    public void displayBooks(List<Book> books) {
        table.getItems().setAll(books);
    }

    public void show() {
        ViewStyles.showScenePreservingState(stage, scene);
    }

    public void showError(String message) {
        ViewStyles.showErrorAlert(message);
    }
}
