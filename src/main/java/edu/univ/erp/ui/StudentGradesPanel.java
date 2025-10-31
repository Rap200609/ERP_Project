package edu.univ.erp.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class StudentGradesPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private int studentId;

    public StudentGradesPanel(int studentId) {
        this.studentId = studentId;
        setLayout(new BorderLayout());
        String[] cols = {"Section", "Component", "Score", "Max", "Weight", "Final Grade"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
        loadGrades();
    }

    private void loadGrades() {
        model.setRowCount(0);
        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT s.section_code, g.component, g.score, g.max_score, g.weight, g.final_grade " +
                "FROM grades g JOIN enrollments e ON g.enrollment_id=e.enrollment_id " +
                "JOIN sections s ON e.section_id=s.section_id WHERE e.student_id=?")) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString(1),
                    rs.getString(2),
                    rs.getDouble(3),
                    rs.getDouble(4),
                    rs.getDouble(5),
                    rs.getString(6)
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading grades: " + ex.getMessage());
        }
    }
}
