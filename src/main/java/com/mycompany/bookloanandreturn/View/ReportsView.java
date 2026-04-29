package com.mycompany.bookloanandreturn.View;

import java.util.List;
import java.util.function.Consumer;

import com.mycompany.bookloanandreturn.Models.FineCollectionReport;
import com.mycompany.bookloanandreturn.Models.MonthlyStatistics;
import com.mycompany.bookloanandreturn.Models.OverdueBookReport;
import com.mycompany.bookloanandreturn.View.common.ViewStyles;
import com.mycompany.bookloanandreturn.util.OverdueFine;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ReportsView {
    private final Stage stage;
    private final Scene scene;
    private final ComboBox<String> reportTypeCombo;
    private final TableView<Object> table;
    private final Label summaryLabel;
    private final Label titleLabel;

    private Runnable backListener;
    private Consumer<String> reportTypeListener;
    private Runnable exportListener;
    private Runnable refreshListener;

    public ReportsView(Stage stage) {
        this.stage = stage;
        stage.setMinWidth(ViewStyles.SCENE_WIDTH);
        stage.setMinHeight(ViewStyles.SCENE_HEIGHT);
        stage.setResizable(true);

        titleLabel = new Label("Reports");
        titleLabel.setStyle(ViewStyles.TITLE_STYLE);

        Label reportTypeLabel = new Label("Report Type:");
        reportTypeLabel.setStyle(ViewStyles.LABEL_STYLE);

        reportTypeCombo = new ComboBox<>();
        reportTypeCombo.getItems().addAll(
            "Overdue Books",
            "Monthly Statistics",
            "Fine Collection"
        );
        reportTypeCombo.setPromptText("Select a report");
        reportTypeCombo.setPrefWidth(250);
        reportTypeCombo.setOnAction(e -> {
            if (reportTypeListener != null && reportTypeCombo.getValue() != null) {
                reportTypeListener.accept(reportTypeCombo.getValue());
            }
        });

        Button refreshButton = new Button("Refresh");
        ViewStyles.stylePrimaryButton(refreshButton);
        refreshButton.setPrefWidth(100);
        refreshButton.setOnAction(e -> fireRefreshRequested());

        HBox controlsRow = new HBox(15, reportTypeLabel, reportTypeCombo, refreshButton);
        controlsRow.setAlignment(Pos.CENTER_LEFT);

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setFixedCellSize(34);
        table.setStyle(ViewStyles.TABLE_STYLE);
        table.setPlaceholder(new Label("Select a report type to view data"));

        summaryLabel = new Label("");
        summaryLabel.setStyle(ViewStyles.LABEL_STYLE);
        summaryLabel.setPadding(new Insets(10, 0, 0, 0));

        Button exportButton = new Button("Export to Excel");
        ViewStyles.stylePrimaryButton(exportButton);
        exportButton.setPrefWidth(140);
        exportButton.setOnAction(e -> fireExportRequested());

        Button backButton = new Button("Back");
        ViewStyles.stylePrimaryButton(backButton);
        backButton.setPrefWidth(100);
        backButton.setOnAction(e -> fireBackRequested());

        HBox bottom = new HBox(10, backButton, exportButton);
        bottom.setAlignment(Pos.CENTER_RIGHT);

        BorderPane main = new BorderPane();
        main.setTop(new VBox(12, titleLabel, controlsRow));
        main.setCenter(table);
        VBox bottomBox = new VBox(summaryLabel, bottom);
        bottomBox.setSpacing(10);
        main.setBottom(bottomBox);
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

    public void setupOverdueBooksColumns() {
        table.getColumns().clear();
        table.getItems().clear();

        TableColumn<Object, String> idCol = new TableColumn<>("Loan no:");
        idCol.setCellValueFactory(data -> {
            OverdueBookReport r = (OverdueBookReport) data.getValue();
            return new ReadOnlyStringWrapper(String.valueOf(r.getLoanId()));
        });

        TableColumn<Object, String> bookCol = new TableColumn<>("Book");
        bookCol.setCellValueFactory(data -> {
            OverdueBookReport r = (OverdueBookReport) data.getValue();
            return new ReadOnlyStringWrapper(r.getBookTitle());
        });

        TableColumn<Object, String> borrowerCol = new TableColumn<>("Borrower");
        borrowerCol.setCellValueFactory(data -> {
            OverdueBookReport r = (OverdueBookReport) data.getValue();
            return new ReadOnlyStringWrapper(r.getBorrowerName());
        });

        TableColumn<Object, String> studentCol = new TableColumn<>("Student");
        studentCol.setCellValueFactory(data -> {
            OverdueBookReport r = (OverdueBookReport) data.getValue();
            String name = r.getStudentName();
            return new ReadOnlyStringWrapper(name != null ? name : "\u2014");
        });

        TableColumn<Object, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(data -> {
            OverdueBookReport r = (OverdueBookReport) data.getValue();
            String phone = r.getPhone();
            return new ReadOnlyStringWrapper(phone != null ? phone : "\u2014");
        });

        TableColumn<Object, String> loanDateCol = new TableColumn<>("Loan Date");
        loanDateCol.setCellValueFactory(data -> {
            OverdueBookReport r = (OverdueBookReport) data.getValue();
            return new ReadOnlyStringWrapper(r.getLoanDate());
        });

        TableColumn<Object, String> dueDateCol = new TableColumn<>("Due Date");
        dueDateCol.setCellValueFactory(data -> {
            OverdueBookReport r = (OverdueBookReport) data.getValue();
            return new ReadOnlyStringWrapper(r.getDueDate());
        });

        TableColumn<Object, String> daysOverdueCol = new TableColumn<>("Days Overdue");
        daysOverdueCol.setCellValueFactory(data -> {
            OverdueBookReport r = (OverdueBookReport) data.getValue();
            return new ReadOnlyStringWrapper(String.valueOf(r.getDaysOverdue()));
        });

        TableColumn<Object, String> fineCol = new TableColumn<>("Est. Fine");
        fineCol.setCellValueFactory(data -> {
            OverdueBookReport r = (OverdueBookReport) data.getValue();
            return new ReadOnlyStringWrapper(OverdueFine.formatPesos(r.getEstimatedFine()));
        });

        table.getColumns().addAll(idCol, bookCol, borrowerCol, studentCol, phoneCol,
                                  loanDateCol, dueDateCol, daysOverdueCol, fineCol);
    }

    public void setupMonthlyStatsColumns() {
        table.getColumns().clear();
        table.getItems().clear();

        TableColumn<Object, String> periodCol = new TableColumn<>("Period");
        periodCol.setCellValueFactory(data -> {
            MonthlyStatistics s = (MonthlyStatistics) data.getValue();
            return new ReadOnlyStringWrapper(s.getMonthYear());
        });

        TableColumn<Object, String> loansCol = new TableColumn<>("Loans");
        loansCol.setCellValueFactory(data -> {
            MonthlyStatistics s = (MonthlyStatistics) data.getValue();
            return new ReadOnlyStringWrapper(String.valueOf(s.getLoansCount()));
        });

        TableColumn<Object, String> returnsCol = new TableColumn<>("Returns");
        returnsCol.setCellValueFactory(data -> {
            MonthlyStatistics s = (MonthlyStatistics) data.getValue();
            return new ReadOnlyStringWrapper(String.valueOf(s.getReturnsCount()));
        });

        TableColumn<Object, String> overdueCol = new TableColumn<>("Overdue Returns");
        overdueCol.setCellValueFactory(data -> {
            MonthlyStatistics s = (MonthlyStatistics) data.getValue();
            return new ReadOnlyStringWrapper(String.valueOf(s.getOverdueReturns()));
        });

        TableColumn<Object, String> finesCol = new TableColumn<>("Fines Collected");
        finesCol.setCellValueFactory(data -> {
            MonthlyStatistics s = (MonthlyStatistics) data.getValue();
            return new ReadOnlyStringWrapper(OverdueFine.formatPesos(s.getTotalFinesCollected()));
        });

        table.getColumns().addAll(periodCol, loansCol, returnsCol, overdueCol, finesCol);
    }

    public void setupFineCollectionColumns() {
        table.getColumns().clear();
        table.getItems().clear();

        TableColumn<Object, String> idCol = new TableColumn<>("Loan no:");
        idCol.setCellValueFactory(data -> {
            FineCollectionReport r = (FineCollectionReport) data.getValue();
            return new ReadOnlyStringWrapper(String.valueOf(r.getLoanId()));
        });

        TableColumn<Object, String> bookCol = new TableColumn<>("Book");
        bookCol.setCellValueFactory(data -> {
            FineCollectionReport r = (FineCollectionReport) data.getValue();
            return new ReadOnlyStringWrapper(r.getBookTitle());
        });

        TableColumn<Object, String> borrowerCol = new TableColumn<>("Borrower");
        borrowerCol.setCellValueFactory(data -> {
            FineCollectionReport r = (FineCollectionReport) data.getValue();
            return new ReadOnlyStringWrapper(r.getBorrowerName());
        });

        TableColumn<Object, String> returnDateCol = new TableColumn<>("Return Date");
        returnDateCol.setCellValueFactory(data -> {
            FineCollectionReport r = (FineCollectionReport) data.getValue();
            return new ReadOnlyStringWrapper(r.getReturnDate());
        });

        TableColumn<Object, String> fineCol = new TableColumn<>("Fine Amount");
        fineCol.setCellValueFactory(data -> {
            FineCollectionReport r = (FineCollectionReport) data.getValue();
            return new ReadOnlyStringWrapper(OverdueFine.formatPesos(r.getFineAmount()));
        });

        TableColumn<Object, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> {
            FineCollectionReport r = (FineCollectionReport) data.getValue();
            return new ReadOnlyStringWrapper(r.getPaymentStatus());
        });

        TableColumn<Object, String> paidCol = new TableColumn<>("Paid");
        paidCol.setCellValueFactory(data -> {
            FineCollectionReport r = (FineCollectionReport) data.getValue();
            return new ReadOnlyStringWrapper(OverdueFine.formatPesos(r.getAmountPaid()));
        });

        TableColumn<Object, String> remainingCol = new TableColumn<>("Remaining");
        remainingCol.setCellValueFactory(data -> {
            FineCollectionReport r = (FineCollectionReport) data.getValue();
            return new ReadOnlyStringWrapper(OverdueFine.formatPesos(r.getRemainingBalance()));
        });

        table.getColumns().addAll(idCol, bookCol, borrowerCol, returnDateCol, fineCol, statusCol, paidCol, remainingCol);
    }

    public void displayData(List<?> data) {
        table.getItems().setAll(data);
    }

    public Object getSelectedItem() {
        return table.getSelectionModel().getSelectedItem();
    }

    public void setSummary(String summary) {
        summaryLabel.setText(summary);
    }

    public void addBackListener(Runnable listener) {
        this.backListener = listener;
    }

    public void addReportTypeListener(Consumer<String> listener) {
        this.reportTypeListener = listener;
    }

    public void addExportListener(Runnable listener) {
        this.exportListener = listener;
    }

    public void addRefreshListener(Runnable listener) {
        this.refreshListener = listener;
    }

    public String getSelectedReportType() {
        return reportTypeCombo.getValue();
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

    public void showInfo(String title, String message) {
        ViewStyles.showInfoAlert(title, message);
    }

    public void showSuccess(String message) {
        ViewStyles.showInfoAlert("Success", message);
    }

    private void fireBackRequested() {
        if (backListener != null) backListener.run();
    }

    private void fireExportRequested() {
        if (exportListener != null) exportListener.run();
    }

    private void fireRefreshRequested() {
        if (refreshListener != null) refreshListener.run();
    }

}
