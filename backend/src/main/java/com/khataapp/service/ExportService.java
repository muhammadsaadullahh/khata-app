package com.khataapp.service;

import com.khataapp.dto.TransactionResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExportService {
    public byte[] excel(List<TransactionResponse> transactions) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet("Transactions");
            Row header = sheet.createRow(0);
            String[] columns = {"Date", "Party", "Type", "Category", "Amount", "Currency", "Note"};
            for (int i = 0; i < columns.length; i++) header.createCell(i).setCellValue(columns[i]);
            for (int rowIndex = 0; rowIndex < transactions.size(); rowIndex++) {
                TransactionResponse item = transactions.get(rowIndex);
                Row row = sheet.createRow(rowIndex + 1);
                row.createCell(0).setCellValue(item.createdAt());
                row.createCell(1).setCellValue(item.partyName());
                row.createCell(2).setCellValue(item.type().name());
                row.createCell(3).setCellValue(item.category());
                row.createCell(4).setCellValue(item.amount().doubleValue());
                row.createCell(5).setCellValue(item.currency());
                row.createCell(6).setCellValue(item.note() == null ? "" : item.note());
            }
            for (int i = 0; i < columns.length; i++) sheet.autoSizeColumn(i);
            workbook.write(output);
            return output.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to create Excel export", ex);
        }
    }

    public byte[] pdf(List<TransactionResponse> transactions) {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                content.beginText();
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
                content.newLineAtOffset(40, 790);
                content.showText("Khata transactions");
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
                content.newLineAtOffset(0, -24);
                for (TransactionResponse item : transactions) {
                    String line = String.format("%s | %s | %s | %s %.2f %s",
                            item.createdAt().substring(0, 10), item.partyName(), item.category(),
                            item.type().name(), item.amount(), item.currency());
                    content.showText(line.substring(0, Math.min(line.length(), 110)));
                    content.newLineAtOffset(0, -15);
                }
                content.endText();
            }
            document.save(output);
            return output.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to create PDF export", ex);
        }
    }
}
