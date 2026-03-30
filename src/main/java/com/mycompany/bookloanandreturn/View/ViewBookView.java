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
    private final Label countLabel;
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
        table.setFixedCellSize(40);
        table.setStyle(ViewStyles.TABLE_STYLE);
        
        // Improved Empty Placeholder
        VBox emptyPlaceholder = new VBox(15);
        emptyPlaceholder.setAlignment(Pos.CENTER);
        Label iconLabel = new Label("📚");
        iconLabel.setStyle("-fx-font-size: 48px;");
        Label textLabel = new Label("No books found in your catalog");
        textLabel.setStyle(ViewStyles.SUBTITLE_STYLE + "-fx-font-weight: bold;");
        emptyPlaceholder.getChildren().addAll(iconLabel, textLabel);
        table.setPlaceholder(emptyPlaceholder);

        TableColumn<Book, String> nameColumn = new TableColumn<>("Book Name");
        nameColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getBookName()));

        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getAuthor()));

        TableColumn<Book, String> genreColumn = new TableColumn<>("Genre");
        genreColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getGenre()));

        TableColumn<Book, String> yearColumn = new TableColumn<>("Year");
        yearColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getPublishedYear()));

        TableColumn<Book, String> stockColumn = new TableColumn<>("Stock");
        stockColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(String.valueOf(data.getValue().getStock())));

        TableColumn<Book, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("📝 Edit");
            {
                ViewStyles.styleGreenButton(editBtn);
                editBtn.setPrefHeight(30);
                editBtn.setStyle(editBtn.getStyle() + "-fx-font-size: 11px; -fx-padding: 4 10 4 10;");
                editBtn.setOnAction(e -> {
                    Book selected = getTableView().getItems().get(getIndex());
                    getTableView().getSelectionModel().select(selected);
                    fireEditRequested();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(editBtn);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });
        actionColumn.setPrefWidth(100);

        table.getColumns().addAll(nameColumn, authorColumn, genreColumn, yearColumn, stockColumn, actionColumn);

        Label titleLabel = new Label("Library Catalog");
        titleLabel.setStyle(ViewStyles.TITLE_STYLE);

        Label subtitleLabel = new Label("Browse and manage your books");
        subtitleLabel.setStyle(ViewStyles.SUBTITLE_STYLE);

        countLabel = new Label("0 books");
        countLabel.setStyle(ViewStyles.LABEL_STYLE + "-fx-background-color: #e8f5e9; -fx-padding: 4 12; -fx-background-radius: 12;");

        searchField = new TextField();
        searchField.setPromptText("🔍 Search by title, author, or genre...");
        ViewStyles.styleInput(searchField);
        searchField.setPrefWidth(400);
        searchField.textProperty().addListener((obs, oldText, newText) -> fireFilterRequested());

        Button refreshButton = new Button("🔄 Refresh");
        ViewStyles.styleGreenButton(refreshButton);
        refreshButton.setPrefWidth(120);
        refreshButton.setOnAction(e -> fireRefreshRequested());

        Button backButton = new Button("⬅ Back");
        ViewStyles.styleSecondaryButton(backButton);
        backButton.setPrefWidth(100);
        backButton.setOnAction(e -> fireBackRequested());

        HBox topActions = new HBox(15, searchField, refreshButton);
        topActions.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        VBox header = new VBox(12, titleLabel, subtitleLabel, new HBox(countLabel), topActions);
        header.setPadding(new Insets(0, 0, 15, 0));

        HBox footer = new HBox(backButton);
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setPadding(new Insets(15, 0, 0, 0));

        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(header);
        mainLayout.setCenter(table);
        mainLayout.setBottom(footer);
        mainLayout.setPadding(new Insets(25));

        VBox card = new VBox(mainLayout);
        ViewStyles.styleCard(card);

        StackPane root = new StackPane(card);
        root.setPadding(new Insets(25));
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

    private void fireRefreshRequested() { if (refreshListener != null) refreshListener.run(); }
    private void fireFilterRequested() { if (filterListener != null) filterListener.run(); }
    private void fireEditRequested() { if (editListener != null) editListener.run(); }
    private void fireBackRequested() { if (backListener != null) backListener.run(); }

    public void displayBooks(List<Book> books) {
        table.getItems().setAll(books);
        countLabel.setText(books.size() + (books.size() == 1 ? " book" : " books"));
    }

    public void show() { ViewStyles.showScenePreservingState(stage, scene); }
    public void showError(String message) { ViewStyles.showErrorAlert(message); }
}
