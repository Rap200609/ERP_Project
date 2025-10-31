package edu.univ.erp.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class InstructorSectionsPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;

    public InstructorSectionsPanel(int instructorId) {
        setLayout(new BorderLayout());
        String[] cols = { "Section", "Course", "Semester", "Year", "Capacity", "Room", "Day", "Time" };
        model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
        loadSections(instructorId);
    }

    private void loadSections(int instructorId) {
        model.setRowCount(0);
        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT s.section_code, c.code, c.title, s.semester, s.year, s.capacity, s.room, s.day, s.time " +
                "FROM sections s JOIN courses c ON s.course_id=c.course_id " +
                "WHERE s.instructor_id=?")) {
            stmt.setInt(1, instructorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString(1),
                    rs.getString(2) + " - " + rs.getString(3),
                    rs.getString(4),
                    rs.getInt(5),
                    rs.getInt(6),
                    rs.getString(7),
                    rs.getString(8),
                    rs.getString(9)
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading instructor sections: " + ex.getMessage());
        }
    }
}
