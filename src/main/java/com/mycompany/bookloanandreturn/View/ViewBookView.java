package com.mycompany.bookloanandreturn.View;

import com.mycompany.bookloanandreturn.Models.Book;
import com.mycompany.bookloanandreturn.View.common.ViewStyles;
import java.util.List;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ViewBookView {
    private final Stage stage;
    private final TableView<Book> table;
    private final TextField searchField;
    private Runnable refreshListener;
    private Runnable filterListener;
    private Runnable editListener;

    public ViewBookView() {
        stage = new Stage();
        stage.setTitle("View Books");

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

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
                    Book selected = getTableView().getItems().get(getIndex());
                    getTableView().getSelectionModel().select(selected);
                    fireEditRequested();
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

        Label searchLabel = new Label("Search:");
        searchLabel.setStyle(ViewStyles.LABEL_STYLE);
        searchField = new TextField();
        searchField.setPrefWidth(280);
        searchField.textProperty().addListener((obs, oldText, newText) -> fireFilterRequested());

        HBox searchPanel = new HBox(8, searchLabel, searchField);
        searchPanel.setAlignment(Pos.CENTER_LEFT);
        searchPanel.setPadding(new Insets(10));
        searchPanel.setStyle(ViewStyles.BACKGROUND_STYLE);

        Button refreshButton = new Button("Refresh");
        ViewStyles.styleGreenButton(refreshButton);
        refreshButton.setOnAction(e -> fireRefreshRequested());

        Button editButton = new Button("Edit");
        ViewStyles.styleGreenButton(editButton);
        editButton.setOnAction(e -> fireEditRequested());

        HBox bottomPanel = new HBox(10, refreshButton, editButton);
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setPadding(new Insets(10));
        bottomPanel.setStyle(ViewStyles.BACKGROUND_STYLE);

        BorderPane mainPanel = new BorderPane();
        mainPanel.setTop(searchPanel);
        mainPanel.setCenter(table);
        mainPanel.setBottom(bottomPanel);
        mainPanel.setStyle(ViewStyles.BACKGROUND_STYLE);

        Scene scene = new Scene(mainPanel, 900, 520);
        stage.setScene(scene);
    }

    public void addRefreshListener(Runnable listener) { this.refreshListener = listener; }
    public void addFilterListener(Runnable listener) { this.filterListener = listener; }
    public void addEditListener(Runnable listener) { this.editListener = listener; }
    public String getSearchText() { return searchField.getText().trim(); }
    public int getSelectedRowIndex() { return table.getSelectionModel().getSelectedIndex(); }

    private void fireRefreshRequested() {
        if (refreshListener != null) refreshListener.run();
    }

    private void fireFilterRequested() {
        if (filterListener != null) filterListener.run();
    }

    private void fireEditRequested() {
        if (editListener != null) editListener.run();
    }

    public void displayBooks(List<Book> books) {
        table.getItems().setAll(books);
    }

    public void show() { stage.show(); }

    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
