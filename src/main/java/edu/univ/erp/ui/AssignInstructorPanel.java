package edu.univ.erp.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class AssignInstructorPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private ArrayList<Integer> sectionIds = new ArrayList<>();
    private ArrayList<Integer> instructorIds = new ArrayList<>();
    private ArrayList<String> instructorNames = new ArrayList<>();

    public AssignInstructorPanel() {
        setLayout(new BorderLayout());
        String[] cols = {"Section ID", "Section", "Course", "Semester", "Year", "Current Instructor", "Update"};
        tableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int row, int col) { return col == 6; }};
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Live updates for instructors/sections
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                loadAll();
            }
        });
        loadAll();

        // Handle Update button
        table.getColumn("Update").setCellRenderer(new ButtonRenderer());
        table.getColumn("Update").setCellEditor(new ButtonEditor(new JCheckBox()));
    }

    private void loadAll() {
        loadInstructors();
        loadSections();
    }

    private void loadInstructors() {
        instructorNames.clear();
        instructorIds.clear();
        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT user_id, employee_id, department FROM instructors")) {
            while (rs.next()) {
                instructorIds.add(rs.getInt("user_id"));
                instructorNames.add(rs.getString("employee_id") + " (" + rs.getString("department") + ")");
            }
        } catch (Exception ex) { }
    }

    private void loadSections() {
        tableModel.setRowCount(0);
        sectionIds.clear();
        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT s.section_id, s.section_code, c.code, c.title, s.semester, s.year, s.instructor_id FROM sections s JOIN courses c ON s.course_id=c.course_id")) {
            while (rs.next()) {
                sectionIds.add(rs.getInt("section_id"));
                String courseName = rs.getString("code") + " - " + rs.getString("title");
                String sectionCode = rs.getString("section_code");
                String currentInstructor = getInstructorNameById(rs.getInt("instructor_id"));
                tableModel.addRow(new Object[] {
                        rs.getInt("section_id"),
                        sectionCode,
                        courseName,
                        rs.getString("semester"),
                        rs.getInt("year"),
                        currentInstructor,
                        "Update"
                });
            }
        } catch (Exception ex) { }
    }

    private String getInstructorNameById(int id) {
        if (id == 0) return "(none)";
        int idx = instructorIds.indexOf(id);
        if (idx >= 0) return instructorNames.get(idx);
        return "(none)";
    }

    // Update logic: show instructor selection dialog, update DB, reload table
    public void assignInstructorToSection(int row) {
        int sectionId = (int) tableModel.getValueAt(row, 0);
        String oldInstructor = (String) tableModel.getValueAt(row, 5);

        JComboBox<String> combo = new JComboBox<>(instructorNames.toArray(new String[0]));
        int option = JOptionPane.showConfirmDialog(this, combo,
                "Select Instructor for Section", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) return;

        int idx = combo.getSelectedIndex();
        if (idx < 0 || idx >= instructorIds.size()) return;
        int instructorId = instructorIds.get(idx);

        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE sections SET instructor_id=? WHERE section_id=?")) {
            stmt.setInt(1, instructorId);
            stmt.setInt(2, sectionId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Instructor updated!");
            loadSections();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed: " + ex.getMessage());
        }
    }

    // Renderer and Editor for "Update" button
    private class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() { setText("Update"); }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) { return this; }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int row;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Update");
            button.addActionListener(e -> {
                assignInstructorToSection(row);
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            return button;
        }
        @Override
        public Object getCellEditorValue() { return "Update"; }
    }
}
