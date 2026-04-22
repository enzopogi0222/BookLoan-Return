package com.mycompany.bookloanandreturn.View;

import java.time.format.DateTimeFormatter;

import com.mycompany.bookloanandreturn.Models.MultiReceiptData;
import com.mycompany.bookloanandreturn.Models.ReceiptData;
import com.mycompany.bookloanandreturn.View.common.ViewStyles;
import com.mycompany.bookloanandreturn.util.OverdueFine;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

/** Dialog showing a receipt for fine payment. */
public class ReceiptView {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    private final Stage dialog;
    private final VBox receiptContent;
    private Runnable payListener;
    private boolean paid = false;

    public ReceiptView(Stage owner) {
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(owner);
        dialog.setTitle("Overdue Fine");
        dialog.setResizable(false);

        receiptContent = new VBox(8);
        receiptContent.setPadding(new Insets(20));
        receiptContent.setAlignment(Pos.TOP_LEFT);
        receiptContent.setStyle("-fx-background-color: white;");
        receiptContent.setPrefWidth(400);

        Scene scene = new Scene(receiptContent);
        dialog.setScene(scene);

        dialog.setOnCloseRequest(e -> {
            System.out.println("[DEBUG] X button or Alt+F4 pressed - NOT paying fine");
            paid = false;
            System.out.println("[DEBUG] paid flag set to false in setOnCloseRequest");
        });
    }

    public void displayReceipt(ReceiptData data) {
        receiptContent.getChildren().clear();

        ImageView logo = new ImageView(ViewStyles.loadBrandLogo());
        logo.setFitHeight(60);
        logo.setFitWidth(60);
        logo.setPreserveRatio(true);

        Label institutionLabel = new Label("RMMC Library");
        institutionLabel.setStyle(ViewStyles.INSTITUTION_STYLE);
        institutionLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        Label titleLabel = new Label("RECEIPT");
        titleLabel.setStyle(ViewStyles.TITLE_STYLE);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        HBox headerBox = new HBox(15, logo, new VBox(4, institutionLabel, titleLabel));
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Separator separator1 = new Separator();

        Label receiptInfo = new Label(String.format("Receipt #: R-%d\nDate: %s",
                data.getReturnId(),
                data.getReturnDate().format(DATE_FORMAT)));
        receiptInfo.setFont(Font.font("Segoe UI", 12));

        Label borrowerLabel = new Label("Borrower: " + data.getBorrowerName());
        borrowerLabel.setFont(Font.font("Segoe UI", 13));

        Separator separator2 = new Separator();

        Label bookLabel = new Label("Book: " + data.getBookTitle());
        bookLabel.setFont(Font.font("Segoe UI", 13));

        Label loanInfo = new Label(String.format("Loaned: %s    Due: %s",
                data.getLoanDate() != null ? data.getLoanDate().format(DATE_FORMAT) : "—",
                data.getDueDate() != null ? data.getDueDate().format(DATE_FORMAT) : "—"));
        loanInfo.setFont(Font.font("Segoe UI", 12));

        Label returnInfo = new Label("Returned: " + data.getReturnDate().format(DATE_FORMAT));
        returnInfo.setFont(Font.font("Segoe UI", 12));

        Separator separator3 = new Separator();

        VBox fineBox = new VBox(4);
        fineBox.setAlignment(Pos.CENTER_RIGHT);

        Label daysLateLabel = new Label(String.format("Days Late: %d", data.getDaysLate()));
        daysLateLabel.setFont(Font.font("Segoe UI", 12));

        Label fineLabel = new Label("TOTAL FINE DUE: " + OverdueFine.formatPesos(data.getFineAmount()));
        fineLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        fineLabel.setStyle("-fx-text-fill: " + ViewStyles.BRAND_BLUE + ";");

        fineBox.getChildren().addAll(daysLateLabel, fineLabel);

        boolean hasFine = data.getFineAmount() > 0;
        if (data.getNotes() != null && !data.getNotes().isEmpty()) {
            Separator separator4 = new Separator();
            Label notesLabel = new Label("Notes: " + data.getNotes());
            notesLabel.setFont(Font.font("Segoe UI", 11));
            notesLabel.setStyle("-fx-text-fill: #666;");
            receiptContent.getChildren().addAll(headerBox, separator1, receiptInfo, borrowerLabel,
                    separator2, bookLabel, loanInfo, returnInfo,
                    separator3, fineBox, separator4, notesLabel, createButtonBox(hasFine));
        } else {
            receiptContent.getChildren().addAll(headerBox, separator1, receiptInfo, borrowerLabel,
                    separator2, bookLabel, loanInfo, returnInfo,
                    separator3, fineBox, createButtonBox(hasFine));
        }
    }

    public void displayMultiReceipt(MultiReceiptData data) {
        receiptContent.getChildren().clear();

        ImageView logo = new ImageView(ViewStyles.loadBrandLogo());
        logo.setFitHeight(60);
        logo.setFitWidth(60);
        logo.setPreserveRatio(true);

        Label institutionLabel = new Label("RMMC Library");
        institutionLabel.setStyle(ViewStyles.INSTITUTION_STYLE);
        institutionLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        Label titleLabel = new Label("RECEIPT");
        titleLabel.setStyle(ViewStyles.TITLE_STYLE);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        HBox headerBox = new HBox(15, logo, new VBox(4, institutionLabel, titleLabel));
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Separator separator1 = new Separator();

        int receiptNo = !data.getItems().isEmpty() ? data.getItems().get(0).returnId : 0;
        Label receiptInfo = new Label(String.format("Receipt #: R-%d\nDate: %s",
                receiptNo,
                data.getReturnDate() != null ? data.getReturnDate().format(DATE_FORMAT) : "—"));
        receiptInfo.setFont(Font.font("Segoe UI", 12));

        Label borrowerLabel = new Label("Borrower: " + (data.getBorrowerName() != null ? data.getBorrowerName() : "—"));
        borrowerLabel.setFont(Font.font("Segoe UI", 13));

        Separator separator2 = new Separator();

        VBox itemsBox = new VBox(6);
        itemsBox.setPadding(new Insets(6, 0, 6, 0));
        for (MultiReceiptData.ReceiptItem item : data.getItems()) {
            Label line = new Label(String.format("%s  •  %d day(s) late  •  %s",
                    item.bookTitle != null ? item.bookTitle : "—",
                    item.daysLate,
                    OverdueFine.formatPesos(item.fineAmount)));
            line.setFont(Font.font("Segoe UI", 12));
            itemsBox.getChildren().add(line);
        }

        Separator separator3 = new Separator();

        VBox totalBox = new VBox(4);
        totalBox.setAlignment(Pos.CENTER_RIGHT);

        Label countLabel = new Label("Items: " + data.getItemCount());
        countLabel.setFont(Font.font("Segoe UI", 12));

        Label totalLabel = new Label("TOTAL FINE DUE: " + OverdueFine.formatPesos(data.getTotalFine()));
        totalLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        totalLabel.setStyle("-fx-text-fill: " + ViewStyles.BRAND_BLUE + ";");

        totalBox.getChildren().addAll(countLabel, totalLabel);

        boolean hasFine = data.getTotalFine() > 0;
        if (data.getNotes() != null && !data.getNotes().isEmpty()) {
            Separator separator4 = new Separator();
            Label notesLabel = new Label("Notes: " + data.getNotes());
            notesLabel.setFont(Font.font("Segoe UI", 11));
            notesLabel.setStyle("-fx-text-fill: #666;");
            receiptContent.getChildren().addAll(headerBox, separator1, receiptInfo, borrowerLabel,
                    separator2, itemsBox,
                    separator3, totalBox, separator4, notesLabel, createButtonBox(hasFine));
        } else {
            receiptContent.getChildren().addAll(headerBox, separator1, receiptInfo, borrowerLabel,
                    separator2, itemsBox,
                    separator3, totalBox, createButtonBox(hasFine));
        }
    }

    private HBox createButtonBox(boolean hasFine) {
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(15, 0, 0, 0));

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> {
            System.out.println("[DEBUG] Close button clicked - NOT paying fine");
            paid = false;
            dialog.close();
        });
        closeButton.setPrefHeight(36);
        closeButton.setDefaultButton(false);

        if (hasFine) {
            Button payButton = new Button("Pay Fine");
            ViewStyles.stylePrimaryButton(payButton);
            payButton.setDefaultButton(false);
            payButton.setOnAction(e -> {
                paid = true;
                System.out.println("[DEBUG] Pay Fine button clicked - executing payListener");
                if (payListener != null) payListener.run();
                dialog.close();
            });

            Button printButton = new Button("Print Receipt");
            ViewStyles.stylePrimaryButton(printButton);
            printButton.setDefaultButton(false);
            printButton.setOnAction(e -> printReceipt());

            buttonBox.getChildren().addAll(closeButton, printButton, payButton);
        } else {
            buttonBox.getChildren().add(closeButton);
        }

        return buttonBox;
    }

    public void addPayListener(Runnable listener) {
        this.payListener = listener;
    }

    public boolean isPaid() {
        System.out.println("[DEBUG] isPaid() called, returning: " + paid);
        return paid;
    }

    private void printReceipt() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            ViewStyles.showErrorAlert("Could not create print job.");
            return;
        }

        if (job.showPrintDialog(dialog)) {
            boolean success = job.printPage(receiptContent);
            if (success) {
                job.endJob();
            } else {
                ViewStyles.showErrorAlert("Printing failed.");
            }
        }
    }

    public void show() {
        System.out.println("[DEBUG] ReceiptView.show() called - about to show dialog");
        dialog.showAndWait();
        System.out.println("[DEBUG] ReceiptView.show() returned - paid=" + paid);
    }
}
