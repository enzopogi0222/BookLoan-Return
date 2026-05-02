package com.mycompany.bookloanandreturn.View;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import com.mycompany.bookloanandreturn.Models.MultiReceiptData;
import com.mycompany.bookloanandreturn.Models.ReceiptData;
import com.mycompany.bookloanandreturn.View.common.ViewStyles;
import com.mycompany.bookloanandreturn.util.OverdueFine;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
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
    private int paymentAmount = 0;
    private int totalFine = 0;
    private String borrowerName = "";
    private String studentId = "";

    public ReceiptView(Stage owner) {
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(owner);
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

        fineBox.getChildren().addAll(daysLateLabel);

        if (data.getBookCondition() != null && ("damaged".equals(data.getBookCondition()) || "lost".equals(data.getBookCondition()))) {
            if (data.getOverdueFine() > 0) {
                Label lateFineLabel = new Label("Late Fine: " + OverdueFine.formatPesos(data.getOverdueFine()));
                lateFineLabel.setFont(Font.font("Segoe UI", 12));
                fineBox.getChildren().add(lateFineLabel);
            }
            Label conditionLabel = new Label(String.format("%s — Book Cost: %s",
                    data.getBookCondition().toUpperCase(),
                    OverdueFine.formatPesos((int) Math.round(data.getBookCost()))));
            conditionLabel.setFont(Font.font("Segoe UI", 12));
            conditionLabel.setStyle("-fx-text-fill: #D32F2F;");
            fineBox.getChildren().add(conditionLabel);
        }

        fineBox.getChildren().add(fineLabel);

        boolean hasFine = data.getFineAmount() > 0;
        this.totalFine = data.getFineAmount();
        VBox paymentBox = createPaymentBox(hasFine, data.getFineAmount());

        if (data.getNotes() != null && !data.getNotes().isEmpty()) {
            Separator separator4 = new Separator();
            Label notesLabel = new Label("Notes: " + data.getNotes());
            notesLabel.setFont(Font.font("Segoe UI", 11));
            notesLabel.setStyle("-fx-text-fill: #666;");
            receiptContent.getChildren().addAll(headerBox, separator1, receiptInfo, borrowerLabel,
                    separator2, bookLabel, loanInfo, returnInfo,
                    separator3, fineBox, paymentBox, separator4, notesLabel, createButtonBox(hasFine));
        } else {
            receiptContent.getChildren().addAll(headerBox, separator1, receiptInfo, borrowerLabel,
                    separator2, bookLabel, loanInfo, returnInfo,
                    separator3, fineBox, paymentBox, createButtonBox(hasFine));
        }
    }

    public void displayMultiReceipt(MultiReceiptData data) {
        receiptContent.getChildren().clear();
        this.borrowerName = data.getBorrowerName() != null ? data.getBorrowerName() : "";
        this.studentId = data.getStudentId() != null ? data.getStudentId() : "";

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
            String baseText = String.format("%s  •  %d day(s) late",
                    item.bookTitle != null ? item.bookTitle : "—",
                    item.daysLate);
            Label line = new Label(baseText);
            line.setFont(Font.font("Segoe UI", 12));
            itemsBox.getChildren().add(line);

            if (item.bookCondition != null && ("damaged".equals(item.bookCondition) || "lost".equals(item.bookCondition))) {
                if (item.overdueFine > 0) {
                    Label lateFineLine = new Label("    Late Fine: " + OverdueFine.formatPesos(item.overdueFine));
                    lateFineLine.setFont(Font.font("Segoe UI", 12));
                    itemsBox.getChildren().add(lateFineLine);
                }
                Label condLine = new Label(String.format("    %s — Book Cost: %s",
                        item.bookCondition.toUpperCase(),
                        OverdueFine.formatPesos((int) Math.round(item.bookCost))));
                condLine.setFont(Font.font("Segoe UI", 12));
                condLine.setStyle("-fx-text-fill: #D32F2F;");
                itemsBox.getChildren().add(condLine);
            } else {
                // Append fine amount on same line for good condition
                line.setText(baseText + "  •  " + OverdueFine.formatPesos(item.fineAmount));
            }
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
        this.totalFine = data.getTotalFine();
        VBox paymentBox = createPaymentBox(hasFine, data.getTotalFine());

        if (data.getNotes() != null && !data.getNotes().isEmpty()) {
            Separator separator4 = new Separator();
            Label notesLabel = new Label("Notes: " + data.getNotes());
            notesLabel.setFont(Font.font("Segoe UI", 11));
            notesLabel.setStyle("-fx-text-fill: #666;");
            receiptContent.getChildren().addAll(headerBox, separator1, receiptInfo, borrowerLabel,
                    separator2, itemsBox,
                    separator3, totalBox, paymentBox, separator4, notesLabel, createButtonBox(hasFine));
        } else {
            receiptContent.getChildren().addAll(headerBox, separator1, receiptInfo, borrowerLabel,
                    separator2, itemsBox,
                    separator3, totalBox, paymentBox, createButtonBox(hasFine));
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
                if (!validatePayment()) {
                    return;
                }
                paid = true;
                System.out.println("[DEBUG] Pay Fine button clicked - executing payListener");
                if (payListener != null) payListener.run();
                dialog.close();
            });

            Button printButton = new Button("Print");
            ViewStyles.stylePrimaryButton(printButton);
            printButton.setDefaultButton(false);
            printButton.setOnAction(e -> printToWord());

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

    public int getPaymentAmount() {
        return paymentAmount;
    }

    private VBox createPaymentBox(boolean hasFine, int totalFine) {
        VBox paymentBox = new VBox(8);
        paymentBox.setPadding(new Insets(10, 0, 0, 0));

        if (hasFine) {
            Label paymentLabel = new Label("Payment Amount (pesos):");
            paymentLabel.setFont(Font.font("Segoe UI", 12));

            TextField paymentField = new TextField();
            paymentField.setPromptText("Enter payment amount");
            paymentField.setText(String.valueOf(totalFine));
            ViewStyles.styleInput(paymentField);
            paymentField.setMaxWidth(200);

            // Change label - updates dynamically
            Label changeLabel = new Label("Change: ₱0");
            changeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            changeLabel.setStyle("-fx-text-fill: #4CAF50;");
            changeLabel.setVisible(false);

            paymentField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.matches("\\d*")) {
                    paymentField.setText(newVal.replaceAll("[^\\d]", ""));
                    return;
                }
                try {
                    int payment = newVal.isEmpty() ? 0 : Integer.parseInt(newVal);
                    int change = payment - this.totalFine;
                    if (change > 0) {
                        changeLabel.setText("Change: " + OverdueFine.formatPesos(change));
                        changeLabel.setVisible(true);
                    } else {
                        changeLabel.setVisible(false);
                    }
                } catch (NumberFormatException e) {
                    changeLabel.setVisible(false);
                }
            });

            // Initial check
            try {
                int initialPayment = Integer.parseInt(paymentField.getText());
                int initialChange = initialPayment - this.totalFine;
                if (initialChange > 0) {
                    changeLabel.setText("Change: " + OverdueFine.formatPesos(initialChange));
                    changeLabel.setVisible(true);
                }
            } catch (NumberFormatException ignored) {}

            paymentBox.getChildren().addAll(paymentLabel, paymentField, changeLabel);
        }

        return paymentBox;
    }

    private boolean validatePayment() {
        for (javafx.scene.Node node : receiptContent.getChildren()) {
            if (node instanceof VBox vbox) {
                for (javafx.scene.Node child : vbox.getChildren()) {
                    if (child instanceof TextField field) {
                        String text = field.getText().trim();
                        if (text.isEmpty()) {
                            ViewStyles.showErrorAlert("Please enter a payment amount.");
                            return false;
                        }
                        try {
                            int enteredAmount = Integer.parseInt(text);
                            if (enteredAmount <= 0) {
                                ViewStyles.showErrorAlert("Payment amount must be greater than 0.");
                                return false;
                            }
                            // Cap payment at total fine (no refunds)
                            paymentAmount = Math.min(enteredAmount, totalFine);
                            return true;
                        } catch (NumberFormatException e) {
                            ViewStyles.showErrorAlert("Invalid payment amount.");
                            return false;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void printToWord() {
        try {
            // Create temp directory
            File tempDir = new File(System.getProperty("java.io.tmpdir"), "BookLoanReceipts");
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }

            // Generate temp filename
            String filename = studentId.isEmpty() ? "Receipt.docx" : studentId + ".docx";
            File tempFile = new File(tempDir, filename);

            // Create Word document with standard paper size to avoid issues
            try (XWPFDocument document = new XWPFDocument();
                 FileOutputStream out = new FileOutputStream(tempFile)) {

                // Add logo
                try {
                    InputStream logoStream = getClass().getResourceAsStream("/images/RMMC1960_400x400.jpg");
                    if (logoStream != null) {
                        byte[] logoBytes = logoStream.readAllBytes();
                        logoStream.close();
                        int pictureType = org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_JPEG;
                        document.addPictureData(logoBytes, pictureType);

                        XWPFParagraph logoPara = document.createParagraph();
                        logoPara.setAlignment(org.apache.poi.xwpf.usermodel.ParagraphAlignment.CENTER);
                        XWPFRun logoRun = logoPara.createRun();
                        logoRun.addPicture(new java.io.ByteArrayInputStream(logoBytes), pictureType, "logo.jpg",
                            org.apache.poi.util.Units.toEMU(40), org.apache.poi.util.Units.toEMU(40));
                    }
                } catch (Exception e) {
                    // Logo not found, continue without it
                }

                // Title
                XWPFParagraph title = document.createParagraph();
                title.setAlignment(org.apache.poi.xwpf.usermodel.ParagraphAlignment.CENTER);
                XWPFRun titleRun = title.createRun();
                titleRun.setText("RMMC Library");
                titleRun.setBold(true);
                titleRun.setFontSize(14);

                XWPFParagraph subtitle = document.createParagraph();
                subtitle.setAlignment(org.apache.poi.xwpf.usermodel.ParagraphAlignment.CENTER);
                XWPFRun subtitleRun = subtitle.createRun();
                subtitleRun.setText("RECEIPT");
                subtitleRun.setBold(true);
                subtitleRun.setFontSize(12);

                XWPFParagraph dash = document.createParagraph();
                dash.setAlignment(org.apache.poi.xwpf.usermodel.ParagraphAlignment.CENTER);
                XWPFRun dashRun = dash.createRun();
                dashRun.setText("----------------------------------------");
                dashRun.setFontSize(10);

                // Extract receipt data from UI components
                for (javafx.scene.Node node : receiptContent.getChildren()) {
                    if (node instanceof Label label) {
                        XWPFParagraph p = document.createParagraph();
                        p.setAlignment(org.apache.poi.xwpf.usermodel.ParagraphAlignment.LEFT);
                        XWPFRun run = p.createRun();
                        run.setText(label.getText());
                        run.setFontSize(9);
                    } else if (node instanceof VBox vbox) {
                        for (javafx.scene.Node child : vbox.getChildren()) {
                            if (child instanceof Label label) {
                                XWPFParagraph p = document.createParagraph();
                                p.setAlignment(org.apache.poi.xwpf.usermodel.ParagraphAlignment.LEFT);
                                XWPFRun run = p.createRun();
                                run.setText(label.getText());
                                run.setFontSize(9);
                            }
                        }
                    }
                }

                // Footer dash
                XWPFParagraph dash2 = document.createParagraph();
                dash2.setAlignment(org.apache.poi.xwpf.usermodel.ParagraphAlignment.CENTER);
                XWPFRun dashRun2 = dash2.createRun();
                dashRun2.setText("----------------------------------------");
                dashRun2.setFontSize(10);

                // Thank you message
                XWPFParagraph thanks = document.createParagraph();
                thanks.setAlignment(org.apache.poi.xwpf.usermodel.ParagraphAlignment.CENTER);
                XWPFRun thanksRun = thanks.createRun();
                thanksRun.setText("Thank you for using RMMC Library!");
                thanksRun.setFontSize(9);

                // Set receipt paper size at the END (after all content)
                org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
                org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz pgSz = sectPr.addNewPgSz();
                pgSz.setW(java.math.BigInteger.valueOf(4320)); // 3 inches
                pgSz.setH(java.math.BigInteger.valueOf(8640)); // 6 inches

                // Set narrow margins
                org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar pgMar = sectPr.addNewPgMar();
                pgMar.setLeft(java.math.BigInteger.valueOf(288));
                pgMar.setRight(java.math.BigInteger.valueOf(288));
                pgMar.setTop(java.math.BigInteger.valueOf(288));
                pgMar.setBottom(java.math.BigInteger.valueOf(288));

                document.write(out);
            }

            // Open the Word file directly with Word
            Desktop.getDesktop().open(tempFile);

        } catch (IOException e) {
            ViewStyles.showErrorAlert("Failed to print receipt: " + e.getMessage());
        }
    }

    public void show() {
        System.out.println("[DEBUG] ReceiptView.show() called - about to show dialog");
        dialog.showAndWait();
        System.out.println("[DEBUG] ReceiptView.show() returned - paid=" + paid);
    }
}
