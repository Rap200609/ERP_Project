package edu.univ.erp.ui;

import edu.univ.erp.api.instructor.InstructorApi;
import edu.univ.erp.domain.GradeComponent;
import edu.univ.erp.domain.SectionDetail;
import edu.univ.erp.domain.StudentProfile;
import edu.univ.erp.service.student.GradeService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GradesExportPanel extends JPanel {
    private final InstructorApi instructorApi = new InstructorApi();
    private final GradeService gradeService = new GradeService();
    private final JComboBox<String> sectionBox;
    private final DefaultTableModel model;
    private final JTable table;
    private final JButton exportBtn;
    private final JLabel summaryLabel;
    private final int instructorId;

    private List<StudentSummary> currentSummaries = new ArrayList<>();
    private final Map<String, SectionDetail> sectionDisplayMap = new LinkedHashMap<>();

    public GradesExportPanel(int instructorId) {
        this.instructorId = instructorId;

        setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout());

        sectionBox = new JComboBox<>();
        UITheme.styleComboBox(sectionBox);

        JLabel sectionLabel = new JLabel("Section:");
        UITheme.styleLabel(sectionLabel, true);

        JButton refreshBtn = new JButton("Load");
        UITheme.styleSecondaryButton(refreshBtn);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(UITheme.BG_MAIN);
        topPanel.setBorder(new javax.swing.border.EmptyBorder(10, 10, 10, 10));
        topPanel.add(sectionLabel);
        topPanel.add(sectionBox);
        topPanel.add(refreshBtn);

        add(topPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"Student Roll No.", "Percentage", "Letter Grade"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        UITheme.styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        UITheme.styleScrollPane(scrollPane);

        summaryLabel = new JLabel("Select a section to view summary.");
        UITheme.styleLabel(summaryLabel, true);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(UITheme.BG_MAIN);
        centerPanel.setBorder(new javax.swing.border.EmptyBorder(10, 10, 10, 10));
        centerPanel.add(summaryLabel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        exportBtn = new JButton("Export CSV");
        UITheme.stylePrimaryButton(exportBtn);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(UITheme.BG_MAIN);
        bottomPanel.setBorder(new javax.swing.border.EmptyBorder(10, 10, 10, 10));
        bottomPanel.add(exportBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> populateSummary());
        sectionBox.addActionListener(e -> populateSummary());
        exportBtn.addActionListener(e -> exportCSV());

        loadSections();
        populateSummary();

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                loadSections();
                populateSummary();
            }
        });
    }

    private void loadSections() {
        sectionDisplayMap.clear();
        sectionBox.removeAllItems();
        List<SectionDetail> sections = instructorApi.listSections(instructorId);
        for (SectionDetail section : sections) {
            String display = String.format("%s - %s", section.getCourseDisplay(), section.getSectionCode());
            sectionDisplayMap.put(display, section);
            sectionBox.addItem(display);
        }
        if (sectionBox.getItemCount() == 0) {
            summaryLabel.setText("No sections available for export.");
        }
    }

    private void populateSummary() {
        currentSummaries.clear();
        model.setRowCount(0);
        String selected = (String) sectionBox.getSelectedItem();
        if (selected == null) {
            summaryLabel.setText("Select a section to view summary.");
            return;
        }
        SectionDetail detail = sectionDisplayMap.get(selected);
        if (detail == null) {
            summaryLabel.setText("Invalid section selection.");
            return;
        }

        List<StudentProfile> students = instructorApi.listStudents(detail.getSectionId());
        if (students.isEmpty()) {
            summaryLabel.setText("No students enrolled for " + selected + ".");
            return;
        }

        for (StudentProfile student : students) {
            List<GradeComponent> grades = instructorApi.loadGrades(student.getUserId(), detail.getSectionId());
            double totalWeight = grades.stream().mapToDouble(GradeComponent::getWeight).sum();
            double weightedScore = grades.stream().mapToDouble(GradeComponent::getWeightedContribution).sum();
            double percentage = totalWeight > 0 ? (weightedScore / totalWeight) * 100.0 : 0.0;
            String letter = gradeService.calculateLetterGrade(percentage);
            currentSummaries.add(new StudentSummary(student.getRollNo(), percentage, letter));
        }

        if (currentSummaries.isEmpty()) {
            summaryLabel.setText("No grade data available for " + selected + ".");
            return;
        }

        summaryLabel.setText("Grade summary for " + selected + " (Total students: " + currentSummaries.size() + ")");
        for (StudentSummary summary : currentSummaries) {
            model.addRow(new Object[]{
                    summary.rollNo,
                    String.format("%.2f%%", summary.percentage),
                    summary.letterGrade
            });
        }
    }

    private void exportCSV() {
        if (currentSummaries.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No data to export.");
            return;
        }
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Export to CSV");
            chooser.setSelectedFile(new File("grades_summary.csv"));

            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".csv")) {
                    file = new File(file.getAbsolutePath() + ".csv");
                }

                try (PrintWriter writer = new PrintWriter(file)) {
                    writer.println("Student Roll No.,Percentage,Letter Grade");
                    for (StudentSummary summary : currentSummaries) {
                        writer.printf("%s,%.2f,%s%n", summary.rollNo, summary.percentage, summary.letterGrade);
                    }
                }
                JOptionPane.showMessageDialog(this, "Exported successfully to:\n" + file.getAbsolutePath());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Export error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static class StudentSummary {
        final String rollNo;
        final double percentage;
        final String letterGrade;

        StudentSummary(String rollNo, double percentage, String letterGrade) {
            this.rollNo = rollNo;
            this.percentage = percentage;
            this.letterGrade = letterGrade;
        }
    }
}
