package edu.univ.erp.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class InstructorGradeEntryPanel extends JPanel {
    private JComboBox<String> sectionBox;
    private DefaultTableModel gradeModel;
    private JTable gradeTable;
    private JButton addComponentBtn, saveGradesBtn;
    private int instructorId;

    public InstructorGradeEntryPanel(int instructorId) {
        this.instructorId = instructorId;
        setLayout(new BorderLayout());

        // Section selector (shows assigned sections only)
        sectionBox = new JComboBox<>();
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Section:"));
        topPanel.add(sectionBox);

        addComponentBtn = new JButton("Add Component");
        saveGradesBtn = new JButton("Save All Grades");
        topPanel.add(addComponentBtn);
        topPanel.add(saveGradesBtn);

        add(topPanel, BorderLayout.NORTH);

        String[] cols = {"Student", "Component", "Score", "Max Score", "Weight", "Final Grade"};
        gradeModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return c == 2 || c == 5; }};
        gradeTable = new JTable(gradeModel);
        add(new JScrollPane(gradeTable), BorderLayout.CENTER);

        // Populate section list
        loadSections();
        sectionBox.addActionListener(e -> loadGradeData());

        // UI for adding new component (assessment, quiz, midterm, etc.) to section
        addComponentBtn.addActionListener(e -> {
            if (sectionBox.getSelectedItem() == null) return;
            JTextField compField = new JTextField();
            JTextField maxScoreField = new JTextField();
            JTextField weightField = new JTextField();

            JPanel compPanel = new JPanel(new GridLayout(3, 2));
            compPanel.add(new JLabel("Component name:"));
            compPanel.add(compField);
            compPanel.add(new JLabel("Max Score:"));
            compPanel.add(maxScoreField);
            compPanel.add(new JLabel("Weight (%):"));
            compPanel.add(weightField);

            int opt = JOptionPane.showConfirmDialog(this, compPanel, "Define New Assessment", JOptionPane.OK_CANCEL_OPTION);
            if (opt == JOptionPane.OK_OPTION) {
                String comp = compField.getText().trim();
                double maxScore = Double.parseDouble(maxScoreField.getText().trim());
                double weight = Double.parseDouble(weightField.getText().trim());
                addComponentToSection(comp, maxScore, weight);
            }
        });

        saveGradesBtn.addActionListener(e -> saveGrades());
        if (sectionBox.getItemCount() > 0) loadGradeData();
    }

    // Loads instructor's sections
    private void loadSections() {
        sectionBox.removeAllItems();
        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT section_id, section_code FROM sections WHERE instructor_id=?")) {
            stmt.setInt(1, instructorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                sectionBox.addItem(rs.getInt(1) + ":" + rs.getString(2));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading sections: " + ex.getMessage());
        }
    }

    // Loads all grades for selected section
    private void loadGradeData() {
        gradeModel.setRowCount(0);
        if (sectionBox.getSelectedItem() == null) return;
        String secStr = (String) sectionBox.getSelectedItem();
        int sectionId = Integer.parseInt(secStr.split(":")[0].trim());

        // Get all enrolled students and components for this section
        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection()) {
            PreparedStatement st1 = conn.prepareStatement(
                "SELECT e.enrollment_id, st.roll_no FROM enrollments e JOIN students st ON e.student_id=st.user_id WHERE e.section_id=? AND e.status='ENROLLED'");
            st1.setInt(1, sectionId);
            ResultSet rs1 = st1.executeQuery();

            // Load all component definitions for this section
            PreparedStatement st2 = conn.prepareStatement(
                "SELECT DISTINCT component, max_score, weight FROM grades WHERE enrollment_id IN (SELECT enrollment_id FROM enrollments WHERE section_id=?)");
            st2.setInt(1, sectionId);
            ResultSet rs2 = st2.executeQuery();

            // Collect students and components
            java.util.List<String> components = new java.util.ArrayList<>();
            java.util.List<Double> maxScores = new java.util.ArrayList<>();
            java.util.List<Double> weights = new java.util.ArrayList<>();
            while (rs2.next()) {
                components.add(rs2.getString("component"));
                maxScores.add(rs2.getDouble("max_score"));
                weights.add(rs2.getDouble("weight"));
            }

            while (rs1.next()) {
                int enrollmentId = rs1.getInt("enrollment_id");
                String rollNo = rs1.getString("roll_no");
                if (components.isEmpty()) {
                    // No components defined yet, user must add
                    continue;
                }
                for (int i = 0; i < components.size(); i++) {
                    String comp = components.get(i);
                    double maxScore = maxScores.get(i);
                    double weight = weights.get(i);

                    PreparedStatement st3 = conn.prepareStatement(
                        "SELECT grade_id, score, final_grade FROM grades WHERE enrollment_id=? AND component=?");
                    st3.setInt(1, enrollmentId);
                    st3.setString(2, comp);
                    ResultSet rs3 = st3.executeQuery();
                    Double score = null;
                    String finalGrade = "";
                    if (rs3.next()) {
                        score = rs3.getDouble("score");
                        finalGrade = rs3.getString("final_grade");
                    }
                    gradeModel.addRow(new Object[] {
                        rollNo, comp, score == null ? "" : score, maxScore, weight, finalGrade
                    });
                    st3.close();
                }
            }
            rs1.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading grades: " + ex.getMessage());
        }
    }

    // Adds new component for all enrolled students in the section
    private void addComponentToSection(String component, double maxScore, double weight) {
        if (sectionBox.getSelectedItem() == null) return;
        String secStr = (String) sectionBox.getSelectedItem();
        int sectionId = Integer.parseInt(secStr.split(":")[0].trim());

        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection()) {
            // Find enrolled students
            PreparedStatement pst = conn.prepareStatement(
                "SELECT enrollment_id FROM enrollments WHERE section_id=? AND status='ENROLLED'");
            pst.setInt(1, sectionId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int enrollmentId = rs.getInt("enrollment_id");
                // Insert a component row with empty score/final
                PreparedStatement pi = conn.prepareStatement(
                    "INSERT INTO grades (enrollment_id, component, max_score, weight) VALUES (?, ?, ?, ?)");
                pi.setInt(1, enrollmentId);
                pi.setString(2, component);
                pi.setDouble(3, maxScore);
                pi.setDouble(4, weight);
                pi.executeUpdate();
                pi.close();
            }
            rs.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding component: " + ex.getMessage());
        }
        loadGradeData();
    }

    // Save all grades and compute weighted final for student in section
    private void saveGrades() {
        if (sectionBox.getSelectedItem() == null) return;
        String secStr = (String) sectionBox.getSelectedItem();
        int sectionId = Integer.parseInt(secStr.split(":")[0].trim());

        // Map: roll_no → enrollment_id
        java.util.Map<String, Integer> enrollMap = new java.util.HashMap<>();
        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection()) {
            PreparedStatement st1 = conn.prepareStatement(
                "SELECT enrollment_id, student_id FROM enrollments WHERE section_id=? AND status='ENROLLED'");
            st1.setInt(1, sectionId);
            ResultSet rs1 = st1.executeQuery();
            while (rs1.next()) {
                enrollMap.put(rs1.getString("student_id"), rs1.getInt("enrollment_id"));
            }
            rs1.close();

            java.util.Map<String, Double> scoreSum = new java.util.HashMap<>();
            java.util.Map<String, Double> weightSum = new java.util.HashMap<>();
            for (int i = 0; i < gradeModel.getRowCount(); i++) {
                String rollNo = (String) gradeModel.getValueAt(i, 0);
                String component = (String) gradeModel.getValueAt(i, 1);
                String scoreStr = gradeModel.getValueAt(i, 2).toString();
                double maxScore = Double.parseDouble(gradeModel.getValueAt(i, 3).toString());
                double weight = Double.parseDouble(gradeModel.getValueAt(i, 4).toString());
                double score = scoreStr.isEmpty() ? 0.0 : Double.parseDouble(scoreStr);

                // Use roll_no to get enrollment_id
                int enrollmentId = getEnrollmentIdFromRoll(rollNo, sectionId, conn);

                // Save grade row and accumulate for final calculation
                PreparedStatement stSave = conn.prepareStatement(
                    "UPDATE grades SET score=?, max_score=?, weight=? WHERE enrollment_id=? AND component=?");
                stSave.setDouble(1, score);
                stSave.setDouble(2, maxScore);
                stSave.setDouble(3, weight);
                stSave.setInt(4, enrollmentId);
                stSave.setString(5, component);
                stSave.executeUpdate();
                stSave.close();

                // sum for final (percentage weighted)
                String enrollCompKey = enrollmentId + "";
                scoreSum.put(enrollCompKey, scoreSum.getOrDefault(enrollCompKey, 0.0) + ((score / maxScore) * weight));
                weightSum.put(enrollCompKey, weightSum.getOrDefault(enrollCompKey, 0.0) + weight);
            }

            // Save weighted final_grade for each enrollment
            for (String enrollCompKey : scoreSum.keySet()) {
                double weightedPct = Math.round(scoreSum.get(enrollCompKey) / weightSum.get(enrollCompKey) * 100.0) / 100.0;
                String finalGradeString;
                if (weightedPct >= 90) finalGradeString = "A";
                else if (weightedPct >= 80) finalGradeString = "B";
                else if (weightedPct >= 70) finalGradeString = "C";
                else if (weightedPct >= 60) finalGradeString = "D";
                else finalGradeString = "F";
                // Store on all grade rows for this enrollment
                PreparedStatement stUpdate = conn.prepareStatement(
                    "UPDATE grades SET final_grade=? WHERE enrollment_id=?");
                stUpdate.setString(1, finalGradeString);
                stUpdate.setInt(2, Integer.parseInt(enrollCompKey));
                stUpdate.executeUpdate();
                stUpdate.close();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving grades: " + ex.getMessage());
        }
        JOptionPane.showMessageDialog(this, "Grades saved and final grades computed!");
        loadGradeData();
    }

    // Helper: get enrollment_id from roll_no
    private int getEnrollmentIdFromRoll(String rollNo, int sectionId, Connection conn) throws SQLException {
        try (PreparedStatement st = conn.prepareStatement(
            "SELECT e.enrollment_id FROM enrollments e JOIN students st ON e.student_id=st.user_id " +
            "WHERE st.roll_no=? AND e.section_id=?")) {
            st.setString(1, rollNo);
            st.setInt(2, sectionId);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return rs.getInt("enrollment_id");
        }
        return -1;
    }
}
