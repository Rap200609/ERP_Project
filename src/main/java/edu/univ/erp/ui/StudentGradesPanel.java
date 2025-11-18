package edu.univ.erp.ui;

import edu.univ.erp.api.student.StudentApi;
import edu.univ.erp.domain.GradeComponent;
import edu.univ.erp.domain.StudentCourseOption;
import edu.univ.erp.service.student.GradeService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StudentGradesPanel extends JPanel {
    private final int studentId;
    private final StudentApi studentApi;
    private final DefaultTableModel model;
    private final JComboBox<String> courseCombo;
    private final JLabel finalGradeLabel;
    private final JLabel courseInfoLabel;
    private final Map<String, Integer> sectionMap = new LinkedHashMap<>();
    private int selectedSectionId = -1;

    public StudentGradesPanel(int studentId) {
        this(studentId, new StudentApi());
    }

    public StudentGradesPanel(int studentId, StudentApi studentApi) {
        this.studentId = studentId;
        this.studentApi = studentApi;

        setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(UITheme.BG_MAIN);
        topPanel.setBorder(new javax.swing.border.EmptyBorder(10, 10, 10, 10));
        
        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectorPanel.setBackground(UITheme.BG_MAIN);
        JLabel selectLabel = new JLabel("Select Course:");
        UITheme.styleLabel(selectLabel, true);
        selectorPanel.add(selectLabel);
        courseCombo = new JComboBox<>();
        UITheme.styleComboBox(courseCombo);
        courseCombo.addActionListener(e -> onCourseSelected());
        selectorPanel.add(courseCombo);
        topPanel.add(selectorPanel, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(UITheme.BG_MAIN);
        courseInfoLabel = new JLabel("Select a course to view grades");
        UITheme.styleLabel(courseInfoLabel, true);
        finalGradeLabel = new JLabel("Final Grade: N/A");
        finalGradeLabel.setFont(UITheme.FONT_SUBHEADING);
        finalGradeLabel.setForeground(UITheme.PRIMARY);
        infoPanel.add(courseInfoLabel, BorderLayout.WEST);
        infoPanel.add(finalGradeLabel, BorderLayout.EAST);
        infoPanel.setBorder(new javax.swing.border.EmptyBorder(10, 0, 10, 0));
        topPanel.add(infoPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"Component", "Score", "Max Score", "Weight (%)", "Percentage"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        UITheme.styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        UITheme.styleScrollPane(scrollPane);
        add(scrollPane, BorderLayout.CENTER);

        loadCourses();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                refreshData();
            }
        });
    }

    private void refreshData() {
        loadCourses();
        if (selectedSectionId != -1) {
            loadGrades();
        }
    }

    private void loadCourses() {
        courseCombo.removeAllItems();
        sectionMap.clear();

        List<StudentCourseOption> options = studentApi.loadCourseOptions(studentId);
        for (StudentCourseOption option : options) {
            String display = option.getDisplayName();
            sectionMap.put(display, option.getSectionId());
            courseCombo.addItem(display);
        }

        if (courseCombo.getItemCount() == 0) {
            courseInfoLabel.setText("No enrolled courses found");
            finalGradeLabel.setText("");
        }
    }

    private void onCourseSelected() {
        String selected = (String) courseCombo.getSelectedItem();
        if (selected == null) {
            return;
        }
        selectedSectionId = sectionMap.getOrDefault(selected, -1);
        courseInfoLabel.setText(selected);
        loadGrades();
    }

    private void loadGrades() {
        model.setRowCount(0);
        if (selectedSectionId == -1) {
            finalGradeLabel.setText("Final Grade: N/A");
            return;
        }

        Optional<GradeService.GradeSummary> summaryOpt = studentApi.loadGrades(studentId, selectedSectionId);
        if (summaryOpt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enrollment not found or no grades posted.");
            finalGradeLabel.setText("Final Grade: N/A");
            return;
        }
        GradeService.GradeSummary summary = summaryOpt.get();
        for (GradeComponent component : summary.components) {
            model.addRow(new Object[]{
                    component.getComponentName(),
                    String.format("%.1f", component.getScore()),
                    String.format("%.1f", component.getMaxScore()),
                    String.format("%.1f", component.getWeight()),
                    String.format("%.2f%%", component.getPercentage())
            });
        }

        if (summary.totalWeight > 0) {
            double finalGrade = summary.weightedScore;
            String letterGrade = studentApi.calculateLetterGrade(finalGrade);
            finalGradeLabel.setText(String.format("Final Grade: %.2f%% (%s)", finalGrade, letterGrade));
        } else {
            finalGradeLabel.setText("Final Grade: N/A (No grades posted)");
        }
    }
}
