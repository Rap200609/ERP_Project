package edu.univ.erp.ui;

import edu.univ.erp.api.common.ApiResponse;
import edu.univ.erp.api.instructor.InstructorApi;
import edu.univ.erp.domain.GradeComponent;
import edu.univ.erp.domain.SectionDetail;
import edu.univ.erp.domain.StudentProfile;
import edu.univ.erp.util.MaintenanceManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstructorGradeEntryPanel extends JPanel {
    private final InstructorApi instructorApi = new InstructorApi();
    private final int instructorId;
    private final JComboBox<String> sectionCombo;
    private final JList<String> studentList;
    private final DefaultListModel<String> studentListModel;
    private final JTable gradeTable;
    private final DefaultTableModel gradeTableModel;
    private final JLabel studentInfoLabel;

    private final Map<String, Integer> sectionMap = new HashMap<>();
    private final Map<String, Integer> studentMap = new HashMap<>();
    private int selectedSectionId = -1;
    private int selectedStudentId = -1;

    public InstructorGradeEntryPanel(int instructorId) {
        this.instructorId = instructorId;

        setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout());

        // Top panel: Section selector
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(UITheme.BG_MAIN);
        topPanel.setBorder(new javax.swing.border.EmptyBorder(10, 10, 10, 10));
        JLabel sectionLabel = new JLabel("Select Section:");
        UITheme.styleLabel(sectionLabel, true);
        topPanel.add(sectionLabel);
        sectionCombo = new JComboBox<>();
        UITheme.styleComboBox(sectionCombo);
        sectionCombo.addActionListener(e -> onSectionSelected());
        topPanel.add(sectionCombo);
        add(topPanel, BorderLayout.NORTH);

        // Left panel: Student list
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(UITheme.BG_PANEL);
        leftPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_LIGHT), "Students"));
        leftPanel.setPreferredSize(new Dimension(250, 0));

        studentListModel = new DefaultListModel<>();
        studentList = new JList<>(studentListModel);
        studentList.setFont(UITheme.FONT_BODY);
        studentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (gradeTable.isEditing()) {
                        gradeTable.getCellEditor().stopCellEditing();
                    }
                    onStudentSelected();
                }
            }
        });
        leftPanel.add(new JScrollPane(studentList), BorderLayout.CENTER);
        add(leftPanel, BorderLayout.WEST);

        // Center panel: Grade entry table
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBackground(UITheme.BG_MAIN);
        centerPanel.setBorder(new javax.swing.border.EmptyBorder(10, 10, 10, 10));

        studentInfoLabel = new JLabel("Select a student to view/edit grades");
        UITheme.styleLabel(studentInfoLabel, true);
        centerPanel.add(studentInfoLabel, BorderLayout.NORTH);

        String[] columns = {"Component", "Score", "Max Score", "Weight (%)"};
        gradeTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };

        gradeTable = new JTable(gradeTableModel);
        UITheme.styleTable(gradeTable);
        JScrollPane scrollPane = new JScrollPane(gradeTable);
        UITheme.styleScrollPane(scrollPane);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(UITheme.BG_MAIN);
        buttonPanel.setBorder(new javax.swing.border.EmptyBorder(10, 10, 10, 10));
        JButton addComponentBtn = new JButton("Add Component");
        UITheme.stylePrimaryButton(addComponentBtn);
        JButton deleteComponentBtn = new JButton("Delete Component");
        UITheme.styleSecondaryButton(deleteComponentBtn);
        JButton saveBtn = new JButton("Save Grades");
        UITheme.stylePrimaryButton(saveBtn);

        addComponentBtn.addActionListener(e -> {
            if (MaintenanceManager.isMaintenanceModeOn()) {
                JOptionPane.showMessageDialog(this,
                        "Adding components is currently disabled.\nMaintenance mode is active.",
                        "Maintenance Mode",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            addComponent();
        });
        deleteComponentBtn.addActionListener(e -> {
            if (MaintenanceManager.isMaintenanceModeOn()) {
                JOptionPane.showMessageDialog(this,
                        "Deleting components is currently disabled.\nMaintenance mode is active.",
                        "Maintenance Mode",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            deleteComponent();
        });
        saveBtn.addActionListener(e -> {
            if (MaintenanceManager.isMaintenanceModeOn()) {
                JOptionPane.showMessageDialog(this,
                        "Grade saving is currently disabled.\nMaintenance mode is active.",
                        "Maintenance Mode",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            saveGrades();
        });

        buttonPanel.add(addComponentBtn);
        buttonPanel.add(deleteComponentBtn);
        buttonPanel.add(saveBtn);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        loadSections();

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                refreshData();
            }
        });
    }

    private void refreshData() {
        loadSections();
        if (selectedSectionId != -1) {
            loadStudents();
            if (selectedStudentId != -1) {
                loadGrades();
            }
        }
    }

    private void loadSections() {
        sectionCombo.removeAllItems();
        sectionMap.clear();

        List<SectionDetail> sections = instructorApi.listSections(instructorId);
        for (SectionDetail section : sections) {
            String display = section.getSectionCode() + " - " + section.getCourseTitle();
            sectionMap.put(display, section.getSectionId());
            sectionCombo.addItem(display);
        }
    }

    private void onSectionSelected() {
        String selected = (String) sectionCombo.getSelectedItem();
        if (selected == null) {
            return;
        }

        selectedSectionId = sectionMap.get(selected);
        selectedStudentId = -1;
        loadStudents();
    }

    private void loadStudents() {
        studentListModel.clear();
        studentMap.clear();
        gradeTableModel.setRowCount(0);
        studentInfoLabel.setText("Select a student to view/edit grades");

        if (selectedSectionId == -1) {
            return;
        }

        List<StudentProfile> students = instructorApi.listStudents(selectedSectionId);
        for (StudentProfile student : students) {
            String display = "Roll No: " + student.getRollNo();
            studentMap.put(display, student.getUserId());
            studentListModel.addElement(display);
        }
    }

    private void onStudentSelected() {
        String selected = studentList.getSelectedValue();
        if (selected == null) {
            selectedStudentId = -1;
            gradeTableModel.setRowCount(0);
            studentInfoLabel.setText("Select a student to view/edit grades");
            return;
        }

        selectedStudentId = studentMap.get(selected);
        studentInfoLabel.setText("Grades for: " + selected);
        loadGrades();
    }

    private void loadGrades() {
        gradeTableModel.setRowCount(0);

        if (selectedStudentId == -1 || selectedSectionId == -1) {
            return;
        }

    List<GradeComponent> grades = instructorApi.loadGrades(selectedStudentId, selectedSectionId);
    for (GradeComponent grade : grades) {
        gradeTableModel.addRow(new Object[]{
            grade.getComponentName(),
            String.valueOf(grade.getScore()),
            String.valueOf(grade.getMaxScore()),
            String.valueOf(grade.getWeight())
        });
    }
    }

    private void saveGrades() {
        if (selectedStudentId == -1 || selectedSectionId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student first");
            return;
        }

        if (gradeTable.isEditing()) {
            gradeTable.getCellEditor().stopCellEditing();
        }

        Map<String, Double> scores = new HashMap<>();
        for (int i = 0; i < gradeTableModel.getRowCount(); i++) {
            String component = (String) gradeTableModel.getValueAt(i, 0);
            String scoreStr = (String) gradeTableModel.getValueAt(i, 1);

            double score = 0.0;
            try {
                if (scoreStr != null && !scoreStr.trim().isEmpty()) {
                    score = Double.parseDouble(scoreStr.trim());
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Invalid score for component '" + component + "': " + scoreStr);
                return;
            }
            scores.put(component, score);
        }

        ApiResponse response = instructorApi.saveGrades(selectedStudentId, selectedSectionId, scores);
        if (response.isSuccess()) {
            String rollNo = studentList.getSelectedValue().replace("Roll No: ", "");
            JOptionPane.showMessageDialog(this,
                    "Grades saved successfully for Roll No: " + rollNo);
            loadGrades();
        } else {
            JOptionPane.showMessageDialog(this, response.getMessage());
        }
    }

    private void addComponent() {
        if (selectedSectionId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a section first");
            return;
        }

        JTextField componentField = new JTextField();
        JTextField maxScoreField = new JTextField();
        JTextField weightField = new JTextField();

        Object[] message = {
                "Component Name:", componentField,
                "Max Score:", maxScoreField,
                "Weight (%):", weightField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add Component", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String component = componentField.getText().trim();
                double maxScore = Double.parseDouble(maxScoreField.getText().trim());
                double weight = Double.parseDouble(weightField.getText().trim());

                if (component.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Component name cannot be empty");
                    return;
                }

                ApiResponse response = instructorApi.addComponent(selectedSectionId, component, maxScore, weight);
                if (response.isSuccess()) {
                    JOptionPane.showMessageDialog(this, response.getMessage());
                    if (selectedStudentId != -1) {
                        loadGrades();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, response.getMessage());
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number format");
            }
        }
    }

    private void deleteComponent() {
        if (selectedSectionId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a section first");
            return;
        }

        List<String> components = instructorApi.listComponentNames(selectedSectionId);
        if (components.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No components to delete");
            return;
        }

        String[] componentArray = components.toArray(new String[0]);
        String selected = (String) JOptionPane.showInputDialog(
                this, "Select component to delete:", "Delete Component",
                JOptionPane.QUESTION_MESSAGE, null, componentArray, componentArray[0]);

        if (selected != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete component '" + selected + "' for ALL students in this section?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                ApiResponse response = instructorApi.deleteComponent(selectedSectionId, selected);
                if (response.isSuccess()) {
                    JOptionPane.showMessageDialog(this, response.getMessage());
                    if (selectedStudentId != -1) {
                        loadGrades();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, response.getMessage());
                }
            }
        }
    }
}
