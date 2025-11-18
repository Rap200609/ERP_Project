package edu.univ.erp.ui;

import edu.univ.erp.api.instructor.InstructorApi;
import edu.univ.erp.domain.GradeExportRow;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;

public class GradesExportPanel extends JPanel {
    private final InstructorApi instructorApi = new InstructorApi();
    private final DefaultTableModel model;
    private final JTable table;
    private final JButton exportBtn;
    private final int instructorId;

    public GradesExportPanel(int instructorId) {
        this.instructorId = instructorId;

        setLayout(new BorderLayout());
        String[] cols = {"Section", "Student Roll No.", "Component", "Score", "Final Grade"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        exportBtn = new JButton("Export Grades as CSV");

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(exportBtn, BorderLayout.SOUTH);

        loadData();

        exportBtn.addActionListener(e -> exportCSV());

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                loadData();
            }
        });
    }

    private void loadData() {
        model.setRowCount(0);
        for (GradeExportRow row : instructorApi.loadGradesForExport(instructorId)) {
            model.addRow(new Object[]{
                    row.getSectionCode(),
                    row.getRollNo(),
                    row.getComponent(),
                    row.getScore(),
                    row.getFinalGrade()
            });
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
