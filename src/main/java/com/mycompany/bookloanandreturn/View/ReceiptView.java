package com.mycompany.bookloanandreturn.View;

import java.time.format.DateTimeFormatter;

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
        dialog.setTitle("Fine Payment Receipt");
        dialog.setResizable(false);

        receiptContent = new VBox(8);
        receiptContent.setPadding(new Insets(20));
        receiptContent.setAlignment(Pos.TOP_LEFT);
        receiptContent.setStyle("-fx-background-color: white;");
        receiptContent.setPrefWidth(400);

        Scene scene = new Scene(receiptContent);
        dialog.setScene(scene);
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

        Label fineLabel = new Label("TOTAL FINE PAID: " + OverdueFine.formatPesos(data.getFineAmount()));
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

    private HBox createButtonBox(boolean hasFine) {
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(15, 0, 0, 0));

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> dialog.close());
        closeButton.setPrefHeight(36);

        if (hasFine) {
            Button payButton = new Button("Pay Fine");
            ViewStyles.stylePrimaryButton(payButton);
            payButton.setOnAction(e -> {
                paid = true;
                if (payListener != null) payListener.run();
                dialog.close();
            });

            Button printButton = new Button("Print Receipt");
            ViewStyles.stylePrimaryButton(printButton);
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
        dialog.showAndWait();
    }
}
