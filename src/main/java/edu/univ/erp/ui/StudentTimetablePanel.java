package edu.univ.erp.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class StudentTimetablePanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private int studentId;

    public StudentTimetablePanel(int studentId) {
        this.studentId = studentId;
        setLayout(new BorderLayout());
        String[] cols = {"Section", "Course", "Day", "Time", "Room"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
        loadTimetable();
    }

    private void loadTimetable() {
        model.setRowCount(0);
        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT s.section_code, c.title, s.day, s.time, s.room " +
                "FROM enrollments e JOIN sections s ON e.section_id=s.section_id " +
                "JOIN courses c ON s.course_id=c.course_id WHERE e.student_id=? AND e.status='ENROLLED'")) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5)
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading timetable: " + ex.getMessage());
        }
    }
}
