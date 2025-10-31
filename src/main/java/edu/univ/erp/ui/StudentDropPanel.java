package edu.univ.erp.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class StudentDropPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private int studentId;

    public StudentDropPanel(int studentId) {
        this.studentId = studentId;
        setLayout(new BorderLayout());
        model = new DefaultTableModel(new String[]{"Section", "Course", "Status", "Drop"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
        loadMyCourses();

        JButton dropBtn = new JButton("Drop Selected");
        add(dropBtn, BorderLayout.SOUTH);
        dropBtn.addActionListener(e -> dropSelected());
    }

    private void loadMyCourses() {
        model.setRowCount(0);
        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT s.section_code, c.title, e.status " +
                "FROM enrollments e JOIN sections s ON e.section_id=s.section_id " +
                "JOIN courses c ON s.course_id=c.course_id WHERE e.student_id=?")) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString(1),
                    rs.getString(2),
                    rs.getString(3),
                    Boolean.FALSE
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading courses: " + ex.getMessage());
        }
    }

    private void dropSelected() {
        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection()) {
            for (int i = 0; i < model.getRowCount(); i++) {
                boolean drop = (Boolean) model.getValueAt(i, 3);
                if (drop) {
                    String code = (String) model.getValueAt(i, 0);
                    PreparedStatement st = conn.prepareStatement(
                        "UPDATE enrollments e JOIN sections s ON e.section_id=s.section_id " +
                        "SET e.status='DROPPED' WHERE e.student_id=? AND s.section_code=?");
                    st.setInt(1, studentId);
                    st.setString(2, code);
                    st.executeUpdate();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error dropping: " + ex.getMessage());
        }
        loadMyCourses();
    }
}
