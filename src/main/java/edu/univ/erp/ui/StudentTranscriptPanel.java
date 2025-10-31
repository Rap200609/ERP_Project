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
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection();
                     PreparedStatement stmt = conn.prepareStatement(
                        "SELECT s.section_code, c.title, g.component, g.score, g.max_score, g.weight, g.final_grade " +
                        "FROM grades g JOIN enrollments e ON g.enrollment_id=e.enrollment_id " +
                        "JOIN sections s ON e.section_id=s.section_id JOIN courses c ON s.course_id=c.course_id WHERE e.student_id=?")) {
                    stmt.setInt(1, studentId);
                    ResultSet rs = stmt.executeQuery();
                    PrintWriter out = new PrintWriter(new FileWriter(file));
                    out.println("Section,Course,Component,Score,Max,Weight,Final Grade");
                    while (rs.next()) {
                        out.println(rs.getString(1) + "," + rs.getString(2) + "," +
                                    rs.getString(3) + "," + rs.getDouble(4) + "," +
                                    rs.getDouble(5) + "," + rs.getDouble(6) + "," + rs.getString(7));
                    }
                    out.close();
                }
                JOptionPane.showMessageDialog(this, "Transcript saved.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error exporting: " + ex.getMessage());
        }
    }
}
