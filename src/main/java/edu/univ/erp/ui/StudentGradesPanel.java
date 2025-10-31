package edu.univ.erp.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class StudentGradesPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private JComboBox<String> courseCombo;
    private JLabel finalGradeLabel;
    private JLabel courseInfoLabel;
    private int studentId;
    private Map<String, Integer> sectionMap; // Maps display string to section_id
    private int selectedSectionId = -1;

    public StudentGradesPanel(int studentId) {
        this.studentId = studentId;
        this.sectionMap = new HashMap<>();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel with course selector
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        
        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectorPanel.add(new JLabel("Select Course:"));
        courseCombo = new JComboBox<>();
        courseCombo.addActionListener(e -> onCourseSelected());
        selectorPanel.add(courseCombo);
        
        topPanel.add(selectorPanel, BorderLayout.NORTH);
        
        // Course info and final grade display
        JPanel infoPanel = new JPanel(new BorderLayout());
        courseInfoLabel = new JLabel("Select a course to view grades");
        courseInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        finalGradeLabel = new JLabel("Final Grade: N/A");
        finalGradeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        finalGradeLabel.setForeground(new Color(33, 128, 141)); // Teal color
        
        infoPanel.add(courseInfoLabel, BorderLayout.WEST);
        infoPanel.add(finalGradeLabel, BorderLayout.EAST);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        topPanel.add(infoPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);
        
        // Table for component breakdown
        String[] cols = {"Component", "Score", "Max Score", "Weight (%)", "Percentage"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only
            }
        };
        table = new JTable(model);
        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        loadCourses();
        
        // Add component listener for real-time refresh
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
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
        
        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT s.section_id, s.section_code, c.title " +
                "FROM enrollments e " +
                "JOIN sections s ON e.section_id=s.section_id " +
                "JOIN courses c ON s.course_id=c.course_id " +
                "WHERE e.student_id=? AND e.status='ENROLLED' " +
                "ORDER BY c.title, s.section_code")) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int sectionId = rs.getInt("section_id");
                String sectionCode = rs.getString("section_code");
                String courseTitle = rs.getString("title");
                String display = courseTitle + " (Section " + sectionCode + ")";
                
                sectionMap.put(display, sectionId);
                courseCombo.addItem(display);
            }
            
            if (courseCombo.getItemCount() == 0) {
                courseInfoLabel.setText("No enrolled courses found");
                finalGradeLabel.setText("");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading courses: " + ex.getMessage());
        }
    }

    private void onCourseSelected() {
        String selected = (String) courseCombo.getSelectedItem();
        if (selected == null) return;
        
        selectedSectionId = sectionMap.get(selected);
        courseInfoLabel.setText(selected);
        loadGrades();
    }

    private void loadGrades() {
        model.setRowCount(0);
        
        if (selectedSectionId == -1) {
            finalGradeLabel.setText("Final Grade: N/A");
            return;
        }
        
        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection()) {
            // Get enrollment_id for this student and section
            PreparedStatement enrollStmt = conn.prepareStatement(
                "SELECT enrollment_id FROM enrollments WHERE student_id=? AND section_id=?");
            enrollStmt.setInt(1, studentId);
            enrollStmt.setInt(2, selectedSectionId);
            ResultSet enrollRs = enrollStmt.executeQuery();
            
            if (!enrollRs.next()) {
                JOptionPane.showMessageDialog(this, "Enrollment not found");
                return;
            }
            int enrollmentId = enrollRs.getInt("enrollment_id");
            enrollStmt.close();
            
            // Load all grade components for this enrollment
            PreparedStatement gradeStmt = conn.prepareStatement(
                "SELECT component, score, max_score, weight FROM grades " +
                "WHERE enrollment_id=? ORDER BY component");
            gradeStmt.setInt(1, enrollmentId);
            ResultSet gradeRs = gradeStmt.executeQuery();
            
            double totalWeightedScore = 0.0;
            double totalWeight = 0.0;
            
            while (gradeRs.next()) {
                String component = gradeRs.getString("component");
                double score = gradeRs.getDouble("score");
                double maxScore = gradeRs.getDouble("max_score");
                double weight = gradeRs.getDouble("weight");
                
                // Calculate percentage for this component
                double percentage = (maxScore > 0) ? (score / maxScore) * 100 : 0.0;
                
                // Calculate weighted contribution to final grade
                double weightedScore = (maxScore > 0) ? (score / maxScore) * weight : 0.0;
                totalWeightedScore += weightedScore;
                totalWeight += weight;
                
                model.addRow(new Object[]{
                    component,
                    String.format("%.1f", score),
                    String.format("%.1f", maxScore),
                    String.format("%.1f", weight),
                    String.format("%.2f%%", percentage)
                });
            }
            gradeStmt.close();
            
            // Calculate and display final grade
            if (totalWeight > 0) {
                double finalGrade = totalWeightedScore;
                String letterGrade = calculateLetterGrade(finalGrade);
                finalGradeLabel.setText(String.format("Final Grade: %.2f%% (%s)", 
                    finalGrade, letterGrade));
            } else {
                finalGradeLabel.setText("Final Grade: N/A (No grades posted)");
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading grades: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Convert percentage to letter grade
     * You can customize this grading scale
     */
    private String calculateLetterGrade(double percentage) {
        if (percentage >= 90) return "A";
        else if (percentage >= 80) return "B";
        else if (percentage >= 70) return "C";
        else if (percentage >= 60) return "D";
        else return "F";
    }
}
