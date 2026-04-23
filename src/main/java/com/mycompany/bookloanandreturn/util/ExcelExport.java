package com.mycompany.bookloanandreturn.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.mycompany.bookloanandreturn.Models.FineCollectionReport;
import com.mycompany.bookloanandreturn.Models.MonthlyStatistics;
import com.mycompany.bookloanandreturn.Models.OverdueBookReport;

public class ExcelExport {

    public static void exportOverdueBooks(List<OverdueBookReport> data, File file) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Overdue Books");
            CellStyle headerStyle = createHeaderStyle(workbook);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Loan no:", "Book Title", "Borrower", "Student Name", "Phone",
                              "Loan Date", "Due Date", "Days Overdue", "Estimated Fine"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (OverdueBookReport r : data) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(r.getLoanId());
                row.createCell(1).setCellValue(r.getBookTitle());
                row.createCell(2).setCellValue(r.getBorrowerName());
                row.createCell(3).setCellValue(r.getStudentName() != null ? r.getStudentName() : "");
                row.createCell(4).setCellValue(r.getPhone() != null ? r.getPhone() : "");
                row.createCell(5).setCellValue(r.getLoanDate());
                row.createCell(6).setCellValue(r.getDueDate());
                row.createCell(7).setCellValue(r.getDaysOverdue());
                row.createCell(8).setCellValue(OverdueFine.formatPesos(r.getEstimatedFine()));
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
        }
    }

    public static void exportMonthlyStatistics(List<MonthlyStatistics> data, File file) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Monthly Statistics");
            CellStyle headerStyle = createHeaderStyle(workbook);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Period", "Loans", "Returns", "Overdue Returns", "Fines Collected"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (MonthlyStatistics s : data) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(s.getMonthYear());
                row.createCell(1).setCellValue(s.getLoansCount());
                row.createCell(2).setCellValue(s.getReturnsCount());
                row.createCell(3).setCellValue(s.getOverdueReturns());
                row.createCell(4).setCellValue(OverdueFine.formatPesos(s.getTotalFinesCollected()));
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
        }
    }

    public static void exportFineCollection(List<FineCollectionReport> data, File file) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Fine Collection");
            CellStyle headerStyle = createHeaderStyle(workbook);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Loan no:", "Book Title", "Borrower", "Return Date", "Fine Amount", "Status"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (FineCollectionReport r : data) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(r.getLoanId());
                row.createCell(1).setCellValue(r.getBookTitle());
                row.createCell(2).setCellValue(r.getBorrowerName());
                row.createCell(3).setCellValue(r.getReturnDate());
                row.createCell(4).setCellValue(OverdueFine.formatPesos(r.getFineAmount()));
                row.createCell(5).setCellValue(r.getPaymentStatus());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
        }
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    public static String generateFilename(String reportType) {
        String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String sanitized = reportType.replaceAll("\\s+", "_").toLowerCase();
        return sanitized + "_" + timestamp + ".xlsx";
    }
}
