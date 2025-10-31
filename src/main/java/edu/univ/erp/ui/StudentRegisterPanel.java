package edu.univ.erp.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentRegisterPanel extends JPanel {
    private int studentId;
    private JTable catalogTable;
    private DefaultTableModel catalogModel;
    private JButton registerBtn;

    public StudentRegisterPanel(int studentId) {
        this.studentId = studentId;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table setup
        String[] columns = {"Section Code", "Course", "Instructor", "Available Seats", "Enroll"};
        catalogModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) return Boolean.class;
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only checkbox column is editable
            }
        };

        catalogTable = new JTable(catalogModel);
        catalogTable.setRowHeight(25);
        add(new JScrollPane(catalogTable), BorderLayout.CENTER);

        // Bottom panel with register button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        registerBtn = new JButton("Register Selected Sections");
        registerBtn.addActionListener(e -> registerSelectedSections());
        bottomPanel.add(registerBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        loadCatalog();

        // Real-time refresh when panel becomes visible
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                loadCatalog();
            }
        });
    }

    private void loadCatalog() {
        catalogModel.setRowCount(0);

        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT s.section_id, s.section_code, c.title, " +
                             "u.username AS instructor, " +
                             "s.capacity, " +
                             "(SELECT COUNT(*) FROM enrollments WHERE section_id = s.section_id AND status = 'ENROLLED') AS enrolled " +
                             "FROM sections s " +
                             "JOIN courses c ON s.course_id = c.course_id " +
                             "JOIN instructors i ON s.instructor_id = i.user_id " +
                             "JOIN erp_auth.users_auth u ON i.user_id = u.user_id " +
                             "ORDER BY c.title, s.section_code")) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int sectionId = rs.getInt("section_id");
                String sectionCode = rs.getString("section_code");
                String courseTitle = rs.getString("title");
                String instructor = rs.getString("instructor");
                int capacity = rs.getInt("capacity");
                int enrolled = rs.getInt("enrolled");

                int availableSeats = capacity - enrolled;
                String seatsDisplay = availableSeats > 0 ? String.valueOf(availableSeats) : "FULL";

                catalogModel.addRow(new Object[]{
                        sectionCode,
                        courseTitle,
                        instructor,
                        seatsDisplay,
                        false // Checkbox unchecked by default
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading catalog: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void registerSelectedSections() {
        // CHECK MAINTENANCE MODE FIRST
        if (edu.univ.erp.util.MaintenanceManager.isMaintenanceModeOn()) {
            JOptionPane.showMessageDialog(this,
                    "Registration is currently disabled.\nMaintenance mode is active.",
                    "Maintenance Mode",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<String> selectedSections = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection()) {
            for (int i = 0; i < catalogModel.getRowCount(); i++) {
                Boolean selected = (Boolean) catalogModel.getValueAt(i, 4);
                if (selected != null && selected) {
                    String sectionCode = (String) catalogModel.getValueAt(i, 0);
                    String courseTitle = (String) catalogModel.getValueAt(i, 1);
                    String seatsDisplay = (String) catalogModel.getValueAt(i, 3);

                    // Check if section is full
                    if ("FULL".equals(seatsDisplay)) {
                        errors.add("• " + sectionCode + " (" + courseTitle + "): Section is full");
                        continue;
                    }

                    // Get section_id
                    PreparedStatement sectionStmt = conn.prepareStatement(
                            "SELECT section_id, capacity FROM sections WHERE section_code = ?");
                    sectionStmt.setString(1, sectionCode);
                    ResultSet sectionRs = sectionStmt.executeQuery();

                    if (!sectionRs.next()) {
                        errors.add("• " + sectionCode + ": Section not found");
                        sectionStmt.close();
                        continue;
                    }

                    int sectionId = sectionRs.getInt("section_id");
                    int capacity = sectionRs.getInt("capacity");
                    sectionStmt.close();

                    // Check if already enrolled
                    PreparedStatement checkStmt = conn.prepareStatement(
                            "SELECT enrollment_id FROM enrollments WHERE student_id = ? AND section_id = ?");
                    checkStmt.setInt(1, studentId);
                    checkStmt.setInt(2, sectionId);
                    ResultSet checkRs = checkStmt.executeQuery();

                    if (checkRs.next()) {
                        errors.add("• " + sectionCode + " (" + courseTitle + "): Already enrolled in this exact section");
                        checkStmt.close();
                        continue;
                    }
                    checkStmt.close();

                    // Check current enrollment count
                    PreparedStatement countStmt = conn.prepareStatement(
                            "SELECT COUNT(*) AS enrolled FROM enrollments WHERE section_id = ? AND status = 'ENROLLED'");
                    countStmt.setInt(1, sectionId);
                    ResultSet countRs = countStmt.executeQuery();
                    countRs.next();
                    int currentEnrolled = countRs.getInt("enrolled");
                    countStmt.close();

                    if (currentEnrolled >= capacity) {
                        errors.add("• " + sectionCode + " (" + courseTitle + "): Section is full");
                        continue;
                    }

                    // Register the student
                    PreparedStatement insertStmt = conn.prepareStatement(
                            "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, 'ENROLLED')");
                    insertStmt.setInt(1, studentId);
                    insertStmt.setInt(2, sectionId);
                    insertStmt.executeUpdate();
                    insertStmt.close();

                    selectedSections.add(sectionCode + " - " + courseTitle);
                }
            }

            // Show results
            StringBuilder message = new StringBuilder();
            if (!selectedSections.isEmpty()) {
                message.append("Successfully registered for:\n");
                for (String section : selectedSections) {
                    message.append("✓ ").append(section).append("\n");
                }
            }

            if (!errors.isEmpty()) {
                if (message.length() > 0) message.append("\n");
                message.append("Registration failed:\n");
                for (String error : errors) {
                    message.append(error).append("\n");
                }
            }

            if (message.length() > 0) {
                JOptionPane.showMessageDialog(this, message.toString(),
                        "Registration Results",
                        errors.isEmpty() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No sections were selected for registration.");
            }

            // Refresh catalog
            loadCatalog();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error during registration: " + ex.getMessage(),
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
