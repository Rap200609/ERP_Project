package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.FlowLayout;
import java.io.*;
import java.sql.*;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Chunk;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfWriter;


public class StudentTranscriptPanel extends JPanel {
    private int studentId;

    public StudentTranscriptPanel(int studentId) {
        this.studentId = studentId;
        setLayout(new FlowLayout());
        
        JButton csvBtn = new JButton("Download Transcript (CSV)");
        JButton pdfBtn = new JButton("Download Transcript (PDF)");
        
        add(csvBtn);
        add(pdfBtn);
        
        csvBtn.addActionListener(e -> exportTranscriptCSV());
        pdfBtn.addActionListener(e -> exportTranscriptPDF());
    }

    private void exportTranscriptCSV() {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save Transcript");
            chooser.setSelectedFile(new File("transcript.csv"));
            
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                
                // Add .csv extension if not present
                if (!file.getName().toLowerCase().endsWith(".csv")) {
                    file = new File(file.getAbsolutePath() + ".csv");
                }
                
                try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection();
                     PrintWriter out = new PrintWriter(new FileWriter(file));
                     PreparedStatement enrollStmt = conn.prepareStatement(
                        "SELECT e.enrollment_id, s.section_code, c.title " +
                        "FROM enrollments e " +
                        "JOIN sections s ON e.section_id = s.section_id " +
                        "JOIN courses c ON s.course_id = c.course_id " +
                        "WHERE e.student_id = ? AND e.status = 'ENROLLED' " +
                        "ORDER BY c.title, s.section_code")) {
                    
                    // Write CSV header
                    out.println("Section,Course,Final Percentage,Letter Grade");
                    
                    enrollStmt.setInt(1, studentId);
                    ResultSet enrollRs = enrollStmt.executeQuery();
                    
                    while (enrollRs.next()) {
                        int enrollmentId = enrollRs.getInt("enrollment_id");
                        String section = enrollRs.getString("section_code");
                        String course = enrollRs.getString("title");
                        
                        // Calculate final grade for this enrollment
                        double totalWeightedScore = 0.0;
                        double totalWeight = 0.0;
                        
                        PreparedStatement gradeStmt = conn.prepareStatement(
                            "SELECT score, max_score, weight FROM grades WHERE enrollment_id = ?");
                        gradeStmt.setInt(1, enrollmentId);
                        ResultSet gradeRs = gradeStmt.executeQuery();
                        
                        while (gradeRs.next()) {
                            double score = gradeRs.getDouble("score");
                            double maxScore = gradeRs.getDouble("max_score");
                            double weight = gradeRs.getDouble("weight");
                            
                            // Calculate weighted contribution
                            if (maxScore > 0) {
                                double weightedScore = (score / maxScore) * weight;
                                totalWeightedScore += weightedScore;
                            }
                            totalWeight += weight;
                        }
                        gradeRs.close();
                        gradeStmt.close();
                        
                        // Calculate final percentage
                        double finalPercentage = (totalWeight > 0) ? totalWeightedScore : 0.0;
                        String letterGrade = calculateLetterGrade(finalPercentage);
                        
                        // Write one row per course with proper CSV escaping
                        out.println(String.format("\"%s\",\"%s\",%.2f,\"%s\"", 
                            escapeCsv(section), 
                            escapeCsv(course), 
                            finalPercentage, 
                            letterGrade));
                    }
                }
                JOptionPane.showMessageDialog(this, "Transcript saved to:\n" + file.getAbsolutePath());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error exporting transcript: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Escape CSV field - handles quotes and special characters
     */
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        // If value contains comma, quote, or newline, wrap in quotes and escape internal quotes
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return value.replace("\"", "\"\""); // Escape quotes by doubling them
        }
        return value;
    }

    /**
     * Export transcript as PDF
     */
    private void exportTranscriptPDF() {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save Transcript as PDF");
            chooser.setSelectedFile(new File("transcript.pdf"));
            
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                
                // Add .pdf extension if not present
                if (!file.getName().toLowerCase().endsWith(".pdf")) {
                    file = new File(file.getAbsolutePath() + ".pdf");
                }
                
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();
                
                // Get student info
                String studentName = "";
                String rollNo = "";
                try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection();
                     PreparedStatement stmt = conn.prepareStatement(
                        "SELECT st.roll_no FROM students st WHERE st.user_id = ?")) {
                    stmt.setInt(1, studentId);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        rollNo = rs.getString("roll_no");
                    }
                }
                
                try (Connection conn = edu.univ.erp.data.DatabaseConfig.getAuthDataSource().getConnection();
                     PreparedStatement stmt = conn.prepareStatement(
                        "SELECT username FROM users_auth WHERE user_id = ?")) {
                    stmt.setInt(1, studentId);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        studentName = rs.getString("username");
                    }
                }
                
                // Title
                Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
                Paragraph title = new Paragraph("Academic Transcript", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(20);
                document.add(title);
                
                // Student Info
                Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
                Paragraph studentInfo = new Paragraph();
                studentInfo.add(new Chunk("Student: " + studentName + "\n", infoFont));
                if (!rollNo.isEmpty()) {
                    studentInfo.add(new Chunk("Roll No: " + rollNo + "\n\n", infoFont));
                }
                studentInfo.setSpacingAfter(15);
                document.add(studentInfo);
                
                // Create table
                PdfPTable table = new PdfPTable(4);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{2, 3, 2, 2});
                
                // Header row
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
                addTableHeader(table, "Section", headerFont);
                addTableHeader(table, "Course", headerFont);
                addTableHeader(table, "Percentage", headerFont);
                addTableHeader(table, "Grade", headerFont);
                
                // Data rows
                Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
                try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection();
                     PreparedStatement enrollStmt = conn.prepareStatement(
                        "SELECT e.enrollment_id, s.section_code, c.title " +
                        "FROM enrollments e " +
                        "JOIN sections s ON e.section_id = s.section_id " +
                        "JOIN courses c ON s.course_id = c.course_id " +
                        "WHERE e.student_id = ? AND e.status = 'ENROLLED' " +
                        "ORDER BY c.title, s.section_code")) {
                    
                    enrollStmt.setInt(1, studentId);
                    ResultSet enrollRs = enrollStmt.executeQuery();
                    
                    while (enrollRs.next()) {
                        int enrollmentId = enrollRs.getInt("enrollment_id");
                        String section = enrollRs.getString("section_code");
                        String course = enrollRs.getString("title");
                        
                        // Calculate final grade
                        double totalWeightedScore = 0.0;
                        double totalWeight = 0.0;
                        
                        PreparedStatement gradeStmt = conn.prepareStatement(
                            "SELECT score, max_score, weight FROM grades WHERE enrollment_id = ?");
                        gradeStmt.setInt(1, enrollmentId);
                        ResultSet gradeRs = gradeStmt.executeQuery();
                        
                        while (gradeRs.next()) {
                            double score = gradeRs.getDouble("score");
                            double maxScore = gradeRs.getDouble("max_score");
                            double weight = gradeRs.getDouble("weight");
                            
                            if (maxScore > 0) {
                                double weightedScore = (score / maxScore) * weight;
                                totalWeightedScore += weightedScore;
                            }
                            totalWeight += weight;
                        }
                        gradeRs.close();
                        gradeStmt.close();
                        
                        double finalPercentage = (totalWeight > 0) ? totalWeightedScore : 0.0;
                        String letterGrade = calculateLetterGrade(finalPercentage);
                        
                        // Add row to table
                        table.addCell(createTableCell(section, dataFont));
                        table.addCell(createTableCell(course, dataFont));
                        table.addCell(createTableCell(String.format("%.2f%%", finalPercentage), dataFont));
                        table.addCell(createTableCell(letterGrade, dataFont));
                    }
                }
                
                document.add(table);
                document.close();
                
                JOptionPane.showMessageDialog(this, "Transcript saved to:\n" + file.getAbsolutePath());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error exporting transcript: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void addTableHeader(PdfPTable table, String text, Font font) {
    PdfPCell cell = new PdfPCell(new Phrase(text, font));
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setPadding(5);
    // Use gray fill since BaseColor may not be available in older/newer iText
    cell.setGrayFill(0.9f); // 90% white -> light gray background
        table.addCell(cell);
    }
    
    private PdfPCell createTableCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        return cell;
    }
    
    /**
     * Convert percentage to letter grade
     * This applies to ALL courses and ALL students
     * Customize this grading scale as needed
     */
    private String calculateLetterGrade(double percentage) {
        if (percentage >= 90) return "A";
        else if (percentage >= 80) return "B";
        else if (percentage >= 70) return "C";
        else if (percentage >= 60) return "D";
        else return "F";
    }
}
