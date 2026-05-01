package com.mycompany.bookloanandreturn.View;

import com.mycompany.bookloanandreturn.Models.FineCollectionReport;
import com.mycompany.bookloanandreturn.View.common.ViewStyles;
import com.mycompany.bookloanandreturn.util.OverdueFine;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PaymentDialog {
    private final Stage dialog;
    private final TextField amountField;
    private boolean confirmed = false;
    private int paymentAmount = 0;

    public PaymentDialog(Stage owner) {
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(owner);
        dialog.setResizable(false);
        dialog.setTitle("Record Payment");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        amountField = new TextField();
        amountField.setPromptText("Enter payment amount");
        ViewStyles.styleInput(amountField);
        amountField.setPrefWidth(200);

        Button payButton = new Button("Pay");
        ViewStyles.stylePrimaryButton(payButton);
        payButton.setPrefWidth(100);
        payButton.setDefaultButton(true);
        payButton.setOnAction(e -> confirmPayment());

        Button cancelButton = new Button("Cancel");
        ViewStyles.stylePrimaryButton(cancelButton);
        cancelButton.setPrefWidth(100);
        cancelButton.setOnAction(e -> dialog.close());

        HBox buttonBox = new HBox(10, cancelButton, payButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        content.getChildren().addAll(amountField, buttonBox);

        Scene scene = new Scene(content);
        dialog.setScene(scene);
    }

    public void display(FineCollectionReport fine) {
        VBox content = (VBox) dialog.getScene().getRoot();
        content.getChildren().clear();

        Label titleLabel = new Label("Fine Payment");
        titleLabel.setStyle(ViewStyles.TITLE_STYLE);

        Label loanLabel = new Label("Loan #" + fine.getLoanId());
        loanLabel.setStyle(ViewStyles.LABEL_STYLE);

        Label bookLabel = new Label("Book: " + fine.getBookTitle());
        bookLabel.setStyle(ViewStyles.LABEL_STYLE);

        Label borrowerLabel = new Label("Borrower: " + fine.getBorrowerName());
        borrowerLabel.setStyle(ViewStyles.LABEL_STYLE);

        Label fineLabel = new Label("Total Fine: " + OverdueFine.formatPesos(fine.getFineAmount()));
        fineLabel.setStyle(ViewStyles.LABEL_STYLE);

        Label paidLabel = new Label("Already Paid: " + OverdueFine.formatPesos(fine.getAmountPaid()));
        paidLabel.setStyle(ViewStyles.LABEL_STYLE);

        Label remainingLabel = new Label("Remaining Balance: " + OverdueFine.formatPesos(fine.getRemainingBalance()));
        remainingLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + ViewStyles.BRAND_BLUE + ";");

        Label amountLabel = new Label("Payment Amount:");
        amountLabel.setStyle(ViewStyles.LABEL_STYLE);

        amountField.clear();
        amountField.setPromptText("Enter amount (max " + OverdueFine.formatPesos(fine.getRemainingBalance()) + ")");

        Button payButton = new Button("Pay");
        ViewStyles.stylePrimaryButton(payButton);
        payButton.setPrefWidth(100);
        payButton.setDefaultButton(true);
        payButton.setOnAction(e -> confirmPayment());

        Button cancelButton = new Button("Cancel");
        ViewStyles.stylePrimaryButton(cancelButton);
        cancelButton.setPrefWidth(100);
        cancelButton.setOnAction(e -> {
            confirmed = false;
            dialog.close();
        });

        HBox buttonBox = new HBox(10, cancelButton, payButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        content.getChildren().addAll(
            titleLabel,
            loanLabel,
            bookLabel,
            borrowerLabel,
            fineLabel,
            paidLabel,
            remainingLabel,
            amountLabel,
            amountField,
            buttonBox
        );

        confirmed = false;
        dialog.showAndWait();
    }

    private void confirmPayment() {
        try {
            String text = amountField.getText().trim();
            if (text.isEmpty()) {
                ViewStyles.showErrorAlert("Please enter a payment amount.");
                return;
            }

            paymentAmount = Integer.parseInt(text);
            if (paymentAmount <= 0) {
                ViewStyles.showErrorAlert("Payment amount must be greater than 0.");
                return;
            }

            confirmed = true;
            dialog.close();
        } catch (NumberFormatException e) {
            ViewStyles.showErrorAlert("Please enter a valid number.");
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public int getPaymentAmount() {
        return paymentAmount;
    }
}
