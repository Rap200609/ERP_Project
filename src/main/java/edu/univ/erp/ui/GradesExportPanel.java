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


    public GradesExportPanel(int instructorId) {
        setLayout(new BorderLayout());
        String[] cols = { "Section", "Student Roll No.", "Component", "Score", "Final Grade" };
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        exportBtn = new JButton("Export Grades as CSV");


        add(new JScrollPane(table), BorderLayout.CENTER);
        add(exportBtn, BorderLayout.SOUTH);


        loadData(instructorId);


        exportBtn.addActionListener(e -> exportCSV());
    }


    private void loadData(int instructorId) {
        model.setRowCount(0);
        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                    "SELECT s.section_code, st.roll_no, g.component, g.score, g.final_grade " +
                    "FROM grades g JOIN enrollments e ON g.enrollment_id=e.enrollment_id " +
                    "JOIN students st ON e.student_id=st.user_id " +
                    "JOIN sections s ON e.section_id=s.section_id " +
                    "WHERE s.instructor_id=?")) {
            stmt.setInt(1, instructorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getObject(4),
                        rs.getObject(5)
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading grades for export: " + ex.getMessage());
        }
    }


    private void exportCSV() {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Export to CSV");
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try (PrintWriter writer = new PrintWriter(file)) {
                    for (int i = 0; i < model.getColumnCount(); i++) {
                        writer.print(model.getColumnName(i));
                        if (i < model.getColumnCount() - 1) writer.print(",");
                    }
                    writer.println();
                    for (int r = 0; r < model.getRowCount(); r++) {
                        for (int c = 0; c < model.getColumnCount(); c++) {
                            writer.print(model.getValueAt(r, c));
                            if (c < model.getColumnCount() - 1) writer.print(",");
                        }
                        writer.println();
                    }
                }
                JOptionPane.showMessageDialog(this, "Exported to " + file.getAbsolutePath());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Export error: " + ex.getMessage());
        }
    }
}