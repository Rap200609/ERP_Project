package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.sql.*;

public class StudentTranscriptPanel extends JPanel {
    private int studentId;
    private JButton downloadBtn;

    public StudentTranscriptPanel(int studentId) {
        this.studentId = studentId;
        setLayout(new FlowLayout());
        downloadBtn = new JButton("Download Transcript (CSV)");
        add(downloadBtn);
        downloadBtn.addActionListener(e -> exportTranscript());
    }

    private void exportTranscript() {
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
