package edu.univ.erp.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class InstructorGradeEntryPanel extends JPanel {
    private int instructorId;
    private JComboBox<String> sectionCombo;
    private JList<String> studentList;
    private DefaultListModel<String> studentListModel;
    private JTable gradeTable;
    private DefaultTableModel gradeTableModel;
    private JLabel studentInfoLabel;
    
    private Map<String, Integer> sectionMap;
    private Map<String, Integer> studentMap;
    private int selectedSectionId = -1;
    private int selectedStudentId = -1;

    public InstructorGradeEntryPanel(int instructorId) {
        this.instructorId = instructorId;
        this.sectionMap = new HashMap<>();
        this.studentMap = new HashMap<>();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel: Section selector
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Section:"));
        sectionCombo = new JComboBox<>();
        sectionCombo.addActionListener(e -> onSectionSelected());
        topPanel.add(sectionCombo);
        add(topPanel, BorderLayout.NORTH);
        
        // Left panel: Student list
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Students"));
        leftPanel.setPreferredSize(new Dimension(250, 0));
        
        studentListModel = new DefaultListModel<>();
        studentList = new JList<>(studentListModel);
        studentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    // Stop any ongoing table editing before switching
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
        
        studentInfoLabel = new JLabel("Select a student to view/edit grades");
        studentInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        centerPanel.add(studentInfoLabel, BorderLayout.NORTH);
        
        String[] columns = {"Component", "Score", "Max Score", "Weight (%)"};
        gradeTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1; // Only Score column is editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class; // Use String for all to avoid type conversion issues
            }
        };
        
        gradeTable = new JTable(gradeTableModel);
        gradeTable.setRowHeight(25);
        centerPanel.add(new JScrollPane(gradeTable), BorderLayout.CENTER);
        
        // Bottom buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addComponentBtn = new JButton("Add Component");
        JButton deleteComponentBtn = new JButton("Delete Component");
        JButton saveBtn = new JButton("Save Grades");
        
        addComponentBtn.addActionListener(e -> {
            if (edu.univ.erp.util.MaintenanceManager.isMaintenanceModeOn()) {
                JOptionPane.showMessageDialog(this,
                        "Adding components is currently disabled.\nMaintenance mode is active.",
                        "Maintenance Mode",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            addComponent();
        });
        deleteComponentBtn.addActionListener(e -> {
            if (edu.univ.erp.util.MaintenanceManager.isMaintenanceModeOn()) {
                JOptionPane.showMessageDialog(this,
                        "Deleting components is currently disabled.\nMaintenance mode is active.",
                        "Maintenance Mode",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            deleteComponent();
        });
        saveBtn.addActionListener(e -> {
            // CHECK MAINTENANCE MODE FIRST
            if (edu.univ.erp.util.MaintenanceManager.isMaintenanceModeOn()) {
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
        
        // Add component listener for real-time refresh
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
        
        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT s.section_id, s.section_code, c.title " +
                "FROM sections s JOIN courses c ON s.course_id=c.course_id " +
                "WHERE s.instructor_id=?")) {
            stmt.setInt(1, instructorId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int sectionId = rs.getInt("section_id");
                String display = rs.getString("section_code") + " - " + rs.getString("title");
                sectionMap.put(display, sectionId);
                sectionCombo.addItem(display);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading sections: " + ex.getMessage());
        }
    }

    private void onSectionSelected() {
        String selected = (String) sectionCombo.getSelectedItem();
        if (selected == null) return;
        
        selectedSectionId = sectionMap.get(selected);
        selectedStudentId = -1; // Reset student selection
        loadStudents();
    }

    private void loadStudents() {
        studentListModel.clear();
        studentMap.clear();
        gradeTableModel.setRowCount(0);
        studentInfoLabel.setText("Select a student to view/edit grades");
        
        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT st.user_id, st.roll_no " +
                "FROM enrollments e " +
                "JOIN students st ON e.student_id=st.user_id " +
                "WHERE e.section_id=? AND e.status='ENROLLED' " +
                "ORDER BY st.roll_no")) {
            stmt.setInt(1, selectedSectionId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String rollNo = rs.getString("roll_no");
                String display = "Roll No: " + rollNo;
                studentMap.put(display, userId);
                studentListModel.addElement(display);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + ex.getMessage());
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
        // Clear the table completely
        gradeTableModel.setRowCount(0);
        
        if (selectedStudentId == -1 || selectedSectionId == -1) {
            return;
        }
        
        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection()) {
            // Get enrollment_id for this specific student
            PreparedStatement enrollStmt = conn.prepareStatement(
                "SELECT enrollment_id FROM enrollments WHERE student_id=? AND section_id=?");
            enrollStmt.setInt(1, selectedStudentId);
            enrollStmt.setInt(2, selectedSectionId);
            ResultSet enrollRs = enrollStmt.executeQuery();
            
            if (!enrollRs.next()) {
                JOptionPane.showMessageDialog(this, "Enrollment not found for this student");
                return;
            }
            int enrollmentId = enrollRs.getInt("enrollment_id");
            enrollStmt.close();
            
            // Load grades for THIS SPECIFIC student's enrollment
            PreparedStatement gradeStmt = conn.prepareStatement(
                "SELECT component, score, max_score, weight FROM grades " +
                "WHERE enrollment_id=? ORDER BY component");
            gradeStmt.setInt(1, enrollmentId);
            ResultSet gradeRs = gradeStmt.executeQuery();
            
            while (gradeRs.next()) {
                // Add fresh row for this student
                gradeTableModel.addRow(new Object[]{
                    gradeRs.getString("component"),
                    String.valueOf(gradeRs.getDouble("score")), // Convert to String
                    String.valueOf(gradeRs.getDouble("max_score")),
                    String.valueOf(gradeRs.getDouble("weight"))
                });
            }
            gradeStmt.close();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading grades: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void saveGrades() {
        if (selectedStudentId == -1 || selectedSectionId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student first");
            return;
        }
        
        // Stop any ongoing editing
        if (gradeTable.isEditing()) {
            gradeTable.getCellEditor().stopCellEditing();
        }
        
        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection()) {
            // Get enrollment_id for THIS SPECIFIC STUDENT
            PreparedStatement enrollStmt = conn.prepareStatement(
                "SELECT enrollment_id FROM enrollments WHERE student_id=? AND section_id=?");
            enrollStmt.setInt(1, selectedStudentId);
            enrollStmt.setInt(2, selectedSectionId);
            ResultSet enrollRs = enrollStmt.executeQuery();
            
            if (!enrollRs.next()) {
                JOptionPane.showMessageDialog(this, "Enrollment not found");
                return;
            }
            int enrollmentId = enrollRs.getInt("enrollment_id");
            enrollStmt.close();
            
            // Update each grade FOR THIS SPECIFIC STUDENT ONLY
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
                    continue;
                }
                
                // Update using BOTH enrollment_id AND component - this targets one specific grade record
                PreparedStatement updateStmt = conn.prepareStatement(
                    "UPDATE grades SET score=? WHERE enrollment_id=? AND component=?");
                updateStmt.setDouble(1, score);
                updateStmt.setInt(2, enrollmentId);
                updateStmt.setString(3, component);
                int updated = updateStmt.executeUpdate();
                updateStmt.close();
                
                if (updated == 0) {
                    System.err.println("Warning: No grade updated for enrollment_id=" + 
                        enrollmentId + ", component=" + component);
                }
            }
            
            JOptionPane.showMessageDialog(this, 
                "Grades saved successfully for Roll No: " + 
                studentList.getSelectedValue().replace("Roll No: ", ""));
            
            // Reload to confirm save
            loadGrades();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving grades: " + ex.getMessage());
            ex.printStackTrace();
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
                
                try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection()) {
                    PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO grades (enrollment_id, component, score, max_score, weight) " +
                        "SELECT e.enrollment_id, ?, 0.0, ?, ? FROM enrollments e " +
                        "WHERE e.section_id=? AND e.status='ENROLLED'");
                    stmt.setString(1, component);
                    stmt.setDouble(2, maxScore);
                    stmt.setDouble(3, weight);
                    stmt.setInt(4, selectedSectionId);
                    int added = stmt.executeUpdate();
                    
                    JOptionPane.showMessageDialog(this, "Component added to " + added + " students!");
                    if (selectedStudentId != -1) {
                        loadGrades();
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number format");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding component: " + ex.getMessage());
            }
        }
    }

    private void deleteComponent() {
        if (selectedSectionId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a section first");
            return;
        }
        
        if (gradeTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No components to delete");
            return;
        }
        
        Set<String> components = new HashSet<>();
        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT DISTINCT g.component FROM grades g " +
                "JOIN enrollments e ON g.enrollment_id=e.enrollment_id " +
                "WHERE e.section_id=? ORDER BY g.component")) {
            stmt.setInt(1, selectedSectionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                components.add(rs.getString("component"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading components: " + ex.getMessage());
            return;
        }
        
        if (components.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No components found");
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
                try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection()) {
                    PreparedStatement stmt = conn.prepareStatement(
                        "DELETE g FROM grades g " +
                        "JOIN enrollments e ON g.enrollment_id=e.enrollment_id " +
                        "WHERE e.section_id=? AND g.component=?");
                    stmt.setInt(1, selectedSectionId);
                    stmt.setString(2, selected);
                    int deleted = stmt.executeUpdate();
                    
                    JOptionPane.showMessageDialog(this, "Deleted component from " + deleted + " students!");
                    if (selectedStudentId != -1) {
                        loadGrades();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting component: " + ex.getMessage());
                }
            }
        }
    }
}
