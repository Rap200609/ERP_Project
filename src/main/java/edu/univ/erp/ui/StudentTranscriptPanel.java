package edu.univ.erp.ui;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import edu.univ.erp.api.student.StudentApi;
import edu.univ.erp.domain.TranscriptRow;
import edu.univ.erp.domain.UserAccount;
import edu.univ.erp.service.student.TranscriptService;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Optional;

public class StudentTranscriptPanel extends JPanel {
    private final int studentId;
    private final StudentApi studentApi;

    public StudentTranscriptPanel(int studentId) {
        this(studentId, new StudentApi());
    }

    public StudentTranscriptPanel(int studentId, StudentApi studentApi) {
        this.studentId = studentId;
        this.studentApi = studentApi;
        setLayout(new FlowLayout());

        JButton csvBtn = new JButton("Download Transcript (CSV)");
        JButton pdfBtn = new JButton("Download Transcript (PDF)");

        add(csvBtn);
        add(pdfBtn);

        csvBtn.addActionListener(e -> exportTranscriptCSV());
        pdfBtn.addActionListener(e -> exportTranscriptPDF());
    }

    private Optional<TranscriptService.TranscriptData> loadTranscript() {
        Optional<TranscriptService.TranscriptData> data = studentApi.buildTranscript(studentId);
        if (data.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Unable to load transcript data.");
        }
        return data;
    }

    private void exportTranscriptCSV() {
        Optional<TranscriptService.TranscriptData> maybeData = loadTranscript();
        if (maybeData.isEmpty()) {
            return;
        }
        TranscriptService.TranscriptData data = maybeData.get();

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Transcript");
        chooser.setSelectedFile(new File("transcript.csv"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new File(file.getAbsolutePath() + ".csv");
        }

        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            out.println("Section,Course,Final Percentage,Letter Grade");
            for (TranscriptRow row : data.rows) {
                out.printf("\"%s\",\"%s\",%.2f,\"%s\"%n",
                        escapeCsv(row.getSectionCode()),
                        escapeCsv(row.getCourseTitle()),
                        row.getFinalPercentage(),
                        escapeCsv(row.getLetterGrade()));
            }
            JOptionPane.showMessageDialog(this, "Transcript saved to:\n" + file.getAbsolutePath());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error exporting transcript: " + ex.getMessage());
        }
    }

    private void exportTranscriptPDF() {
        Optional<TranscriptService.TranscriptData> maybeData = loadTranscript();
        if (maybeData.isEmpty()) {
            return;
        }
        TranscriptService.TranscriptData data = maybeData.get();

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Transcript as PDF");
        chooser.setSelectedFile(new File("transcript.pdf"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".pdf")) {
            file = new File(file.getAbsolutePath() + ".pdf");
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            Document document = new Document();
            PdfWriter.getInstance(document, fos);
            document.open();

            UserAccount account = data.account;
            String studentName = account != null ? account.getUsername() : "Student";
            String rollNo = data.studentProfile != null ? data.studentProfile.getRollNo() : "";

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Academic Transcript", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Paragraph studentInfo = new Paragraph("Student: " + studentName + "\n", infoFont);
            if (!rollNo.isBlank()) {
                studentInfo.add("Roll No: " + rollNo + "\n");
            }
            studentInfo.setSpacingAfter(15);
            document.add(studentInfo);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2f, 3f, 2f, 2f});

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            addTableHeader(table, "Section", headerFont);
            addTableHeader(table, "Course", headerFont);
            addTableHeader(table, "Percentage", headerFont);
            addTableHeader(table, "Grade", headerFont);

            Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            for (TranscriptRow row : data.rows) {
                table.addCell(createCell(row.getSectionCode(), dataFont));
                table.addCell(createCell(row.getCourseTitle(), dataFont));
                table.addCell(createCell(String.format("%.2f%%", row.getFinalPercentage()), dataFont));
                table.addCell(createCell(row.getLetterGrade(), dataFont));
            }

            document.add(table);
            document.close();

            JOptionPane.showMessageDialog(this, "Transcript saved to:\n" + file.getAbsolutePath());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error exporting transcript: " + ex.getMessage());
        }
    }

    private void addTableHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        cell.setGrayFill(0.9f);
        table.addCell(cell);
    }

    private PdfPCell createCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        return cell;
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return value.replace("\"", "\"\"");
        }
        return value;
    }
}
