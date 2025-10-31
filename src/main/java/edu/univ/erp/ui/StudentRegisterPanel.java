package edu.univ.erp.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class StudentRegisterPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private int studentId;

    public StudentRegisterPanel(int studentId) {
        this.studentId = studentId;
        setLayout(new BorderLayout());
        String[] cols = {"Section Code", "Course", "Instructor", "Capacity", "Enroll"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
        loadAvailableSections();
        JButton registerBtn = new JButton("Register Selected");
        add(registerBtn, BorderLayout.SOUTH);
        registerBtn.addActionListener(e -> registerSections());
    }

    private void loadAvailableSections() {
        model.setRowCount(0);
        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT s.section_id, s.section_code, c.title, i.employee_id, s.capacity " +
                "FROM sections s JOIN courses c ON s.course_id=c.course_id " +
                "JOIN instructors i ON s.instructor_id=i.user_id")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("section_code"),
                    rs.getString("title"),
                    rs.getString("employee_id"),
                    rs.getInt("capacity"),
                    Boolean.FALSE
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading sections: " + ex.getMessage());
        }
    }

    private void registerSections() {
        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection()) {
            for (int i = 0; i < model.getRowCount(); i++) {
                boolean enroll = (Boolean) model.getValueAt(i, 4);
                if (enroll) {
                    String sectionCode = (String) model.getValueAt(i, 0);
                    PreparedStatement st1 = conn.prepareStatement(
                        "SELECT section_id, capacity FROM sections WHERE section_code=?");
                    st1.setString(1, sectionCode);
                    ResultSet rs1 = st1.executeQuery();
                    if (!rs1.next()) continue;
                    int sectionId = rs1.getInt("section_id");
                    PreparedStatement chk = conn.prepareStatement(
                        "SELECT COUNT(*) FROM enrollments WHERE student_id=? AND section_id=?");
                    chk.setInt(1, studentId); chk.setInt(2, sectionId);
                    ResultSet chkrs = chk.executeQuery();
                    chkrs.next();
                    if (chkrs.getInt(1) > 0) continue;

                    PreparedStatement st2 = conn.prepareStatement(
                        "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, 'ENROLLED')");
                    st2.setInt(1, studentId);
                    st2.setInt(2, sectionId);
                    st2.executeUpdate();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Registration error: " + ex.getMessage());
        }
        JOptionPane.showMessageDialog(this, "Registered successfully!");
        loadAvailableSections();
    }
}
