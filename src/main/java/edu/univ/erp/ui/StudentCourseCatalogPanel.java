package edu.univ.erp.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class StudentCourseCatalogPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;

    public StudentCourseCatalogPanel() {
        setLayout(new BorderLayout());
        String[] cols = {"Course Code", "Title", "Credits", "Instructor", "Capacity"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
        loadCatalog();
    }

    private void loadCatalog() {
        model.setRowCount(0);
        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                "SELECT c.code, c.title, c.credits, i.employee_id, s.capacity " +
                "FROM courses c " +
                "LEFT JOIN sections s ON c.course_id=s.course_id " +
                "LEFT JOIN instructors i ON s.instructor_id=i.user_id")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString(1),
                    rs.getString(2),
                    rs.getInt(3),
                    rs.getString(4),
                    rs.getInt(5)
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading catalog: " + ex.getMessage());
        }
    }
}
