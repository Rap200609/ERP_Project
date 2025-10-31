package edu.univ.erp.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class StudentDropPanel extends JPanel {
    private int studentId;
    private JTable enrolledTable;
    private DefaultTableModel enrolledModel;
    private JButton dropBtn;

    public StudentDropPanel(int studentId) {
        this.studentId = studentId;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Section Code", "Course", "Instructor", "Drop"};
        enrolledModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) return Boolean.class;
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only checkbox column is editable
            }
        };

        enrolledTable = new JTable(enrolledModel);
        enrolledTable.setRowHeight(25);
        add(new JScrollPane(enrolledTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        dropBtn = new JButton("Drop Selected Sections");
        dropBtn.addActionListener(e -> dropSelectedSections());
        bottomPanel.add(dropBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        loadEnrolledSections();

        // Real-time refresh when panel becomes visible
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                loadEnrolledSections();
            }
        });
    }

    private void loadEnrolledSections() {
        enrolledModel.setRowCount(0);

        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT s.section_code, c.title, u.username AS instructor " +
                             "FROM enrollments e " +
                             "JOIN sections s ON e.section_id = s.section_id " +
                             "JOIN courses c ON s.course_id = c.course_id " +
                             "JOIN instructors i ON s.instructor_id = i.user_id " +
                             "JOIN erp_auth.users_auth u ON i.user_id = u.user_id " +
                             "WHERE e.student_id = ? AND e.status = 'ENROLLED' " +
                             "ORDER BY c.title")) {

            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                enrolledModel.addRow(new Object[]{
                        rs.getString("section_code"),
                        rs.getString("title"),
                        rs.getString("instructor"),
                        false // Checkbox unchecked by default
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading enrolled sections: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void dropSelectedSections() {
        // CHECK MAINTENANCE MODE FIRST
        if (edu.univ.erp.util.MaintenanceManager.isMaintenanceModeOn()) {
            JOptionPane.showMessageDialog(this,
                    "Drop section is currently disabled.\nMaintenance mode is active.",
                    "Maintenance Mode",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int droppedCount = 0;

        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection()) {
            for (int i = 0; i < enrolledModel.getRowCount(); i++) {
                Boolean selected = (Boolean) enrolledModel.getValueAt(i, 3);
                if (selected != null && selected) {
                    String sectionCode = (String) enrolledModel.getValueAt(i, 0);

                    PreparedStatement stmt = conn.prepareStatement(
                            "DELETE e FROM enrollments e " +
                                    "JOIN sections s ON e.section_id = s.section_id " +
                                    "WHERE e.student_id = ? AND s.section_code = ?");
                    stmt.setInt(1, studentId);
                    stmt.setString(2, sectionCode);
                    droppedCount += stmt.executeUpdate();
                    stmt.close();
                }
            }

            if (droppedCount > 0) {
                JOptionPane.showMessageDialog(this,
                        "Successfully dropped " + droppedCount + " section(s).",
                        "Drop Successful",
                        JOptionPane.INFORMATION_MESSAGE);
                loadEnrolledSections(); // Refresh
            } else {
                JOptionPane.showMessageDialog(this, "No sections were selected to drop.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error dropping sections: " + ex.getMessage(),
                    "Drop Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
