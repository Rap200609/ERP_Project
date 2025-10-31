package edu.univ.erp.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.sql.*;

public class GradesExportPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private JButton exportBtn;
    private int instructorId; // Store instructor ID for refresh

    public GradesExportPanel(int instructorId) {
        this.instructorId = instructorId; // Save for later refresh
        
        setLayout(new BorderLayout());
        String[] cols = { "Section", "Student Roll No.", "Component", "Score", "Final Grade" };
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        exportBtn = new JButton("Export Grades as CSV");

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(exportBtn, BorderLayout.SOUTH);

        loadData(instructorId);

        exportBtn.addActionListener(e -> exportCSV());
        
        // Add component listener for real-time refresh
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                // Refresh data when panel becomes visible
                loadData(GradesExportPanel.this.instructorId);
            }
        });
    }

    private void loadData(int instructorId) {
        model.setRowCount(0);
        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                    "SELECT s.section_code, st.roll_no, g.component, g.score, g.final_grade " +
                    "FROM grades g JOIN enrollments e ON g.enrollment_id=e.enrollment_id " +
                    "JOIN students st ON e.student_id=st.user_id " +
                    "JOIN sections s ON e.section_id=s.section_id " +
                    "WHERE s.instructor_id=? " +
                    "ORDER BY s.section_code, st.roll_no, g.component")) {
            stmt.setInt(1, instructorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getString("section_code"),
                        rs.getString("roll_no"),
                        rs.getString("component"),
                        rs.getDouble("score"),
                        rs.getObject("final_grade")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading grades for export: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void exportCSV() {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Export to CSV");
            chooser.setSelectedFile(new File("grades_export.csv")); // Default filename
            
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                
                // Add .csv extension if not present
                if (!file.getName().toLowerCase().endsWith(".csv")) {
                    file = new File(file.getAbsolutePath() + ".csv");
                }
                
                try (PrintWriter writer = new PrintWriter(file)) {
                    // Write headers
                    for (int i = 0; i < model.getColumnCount(); i++) {
                        writer.print(model.getColumnName(i));
                        if (i < model.getColumnCount() - 1) writer.print(",");
                    }
                    writer.println();
                    
                    // Write data
                    for (int r = 0; r < model.getRowCount(); r++) {
                        for (int c = 0; c < model.getColumnCount(); c++) {
                            Object value = model.getValueAt(r, c);
                            if (value != null) {
                                writer.print(value.toString());
                            }
                            if (c < model.getColumnCount() - 1) writer.print(",");
                        }
                        writer.println();
                    }
                }
                JOptionPane.showMessageDialog(this, "Exported successfully to:\n" + file.getAbsolutePath());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Export error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
