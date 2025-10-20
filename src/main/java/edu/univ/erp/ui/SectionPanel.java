package edu.univ.erp.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import edu.univ.erp.data.DatabaseConfig;
import java.util.ArrayList;

public class SectionPanel extends JPanel {
    private JComboBox<String> courseBox, instructorBox;
    private JTextField dayField, timeField, roomField, semesterField, yearField, capacityField, secCodeField;
    private JLabel messageLabel;
    private DefaultTableModel tableModel;
    private JTable sectionTable;

    // For fast lookups
    private ArrayList<Integer> courseIds = new ArrayList<>();
    private ArrayList<Integer> instIds = new ArrayList<>();

    public SectionPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Course selector row
        gbc.gridx=0; gbc.gridy=0;
        add(new JLabel("Course:"), gbc);
        gbc.gridx=1;
        courseBox = new JComboBox<>();
        add(courseBox, gbc);

        // Instructor selector row
        gbc.gridx=0; gbc.gridy=1;
        add(new JLabel("Instructor:"), gbc);
        gbc.gridx=1;
        instructorBox = new JComboBox<>();
        add(instructorBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Section Code:"), gbc);
        gbc.gridx = 1;
        secCodeField = new JTextField(6);
        add(secCodeField, gbc);

        // Day, time, room
        gbc.gridx=0; gbc.gridy=3;
        add(new JLabel("Day:"), gbc);
        gbc.gridx=1;
        dayField = new JTextField(10);
        add(dayField, gbc);

        gbc.gridx=0; gbc.gridy=4;
        add(new JLabel("Time:"), gbc);
        gbc.gridx=1;
        timeField = new JTextField(10);
        add(timeField, gbc);

        gbc.gridx=0; gbc.gridy=5;
        add(new JLabel("Room:"), gbc);
        gbc.gridx=1;
        roomField = new JTextField(10);
        add(roomField, gbc);

        // Semester, year, capacity
        gbc.gridx=0; gbc.gridy=6;
        add(new JLabel("Semester:"), gbc);
        gbc.gridx=1;
        semesterField = new JTextField(10);
        add(semesterField, gbc);

        gbc.gridx=0; gbc.gridy=7;
        add(new JLabel("Year:"), gbc);
        gbc.gridx=1;
        yearField = new JTextField(8);
        add(yearField, gbc);

        gbc.gridx=0; gbc.gridy=8;
        add(new JLabel("Capacity:"), gbc);
        gbc.gridx=1;
        capacityField = new JTextField(8);
        add(capacityField, gbc);

        // Add button, message
        gbc.gridx=0; gbc.gridy=9;
        JButton addButton = new JButton("Add Section");
        add(addButton, gbc);
        gbc.gridx=1;
        messageLabel = new JLabel();
        add(messageLabel, gbc);

        // Section table
        gbc.gridx=0; gbc.gridy=10; gbc.gridwidth=2; gbc.fill=GridBagConstraints.BOTH;
        String[] columns = {"ID", "Course", "Instructor", "Day", "Time", "Room", "Semester", "Year", "Capacity", "Edit", "Delete"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return col == 9 || col == 10;
            }
        };
        sectionTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(sectionTable);
        add(scrollPane, gbc);

        loadCourseList();
        loadInstructorList();
        loadSectionTable();

        addButton.addActionListener(e -> {
            int selCourse = courseBox.getSelectedIndex();
            int selInst = instructorBox.getSelectedIndex();
            String day = dayField.getText().trim();
            String time = timeField.getText().trim();
            String room = roomField.getText().trim();
            String semester = semesterField.getText().trim();
            String yearStr = yearField.getText().trim();
            String capStr = capacityField.getText().trim();

            if(selCourse < 0 || selInst < 0 || day.isEmpty() || time.isEmpty()
                || room.isEmpty() || semester.isEmpty() || yearStr.isEmpty() || capStr.isEmpty()) {
                messageLabel.setText("All fields required.");
                return;
            }
            int year, capacity;
            try {
                year = Integer.parseInt(yearStr);
                capacity = Integer.parseInt(capStr);
            } catch(NumberFormatException ex) {
                messageLabel.setText("Year/Capacity must be numbers.");
                return;
            }
            String sql = "INSERT INTO sections (course_id, instructor_id, section_code, day, time, room, semester, year, capacity) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseConfig.getMainDataSource().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, courseIds.get(selCourse));
                stmt.setInt(2, instIds.get(selInst));
                stmt.setString(3, secCodeField.getText().trim());
                stmt.setString(4, dayField.getText().trim());
                stmt.setString(5, timeField.getText().trim());
                stmt.setString(6, roomField.getText().trim());
                stmt.setString(7, semesterField.getText().trim());
                stmt.setInt(8, Integer.parseInt(yearField.getText().trim()));
                stmt.setInt(9, Integer.parseInt(capacityField.getText().trim()));

                stmt.executeUpdate();
                messageLabel.setText("Section added successfully!");
                clearFields();
                loadSectionTable();
            } catch(Exception ex) {
                messageLabel.setText("Error: "+ex.getMessage());
            }
        });

        sectionTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = sectionTable.rowAtPoint(evt.getPoint());
                int col = sectionTable.columnAtPoint(evt.getPoint());
                int sectionId = (int)tableModel.getValueAt(row, 0);
                if(col == 9) { // Edit
                    editSectionDialog(sectionId);
                } else if(col == 10) { // Delete
                    int confirm = JOptionPane.showConfirmDialog(SectionPanel.this,
                        "Delete section?", "Confirm", JOptionPane.YES_NO_OPTION);
                    if(confirm == JOptionPane.YES_OPTION) {
                        deleteSection(sectionId);
                    }
                }
            }
        });
    }

    private void loadCourseList() {
        courseBox.removeAllItems();
        courseIds.clear();
        try (Connection conn = DatabaseConfig.getMainDataSource().getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT course_id, code, title FROM courses")) {
            while(rs.next()) {
                courseBox.addItem(rs.getString("code")+" - "+rs.getString("title"));
                courseIds.add(rs.getInt("course_id"));
            }
        } catch(Exception ex) {}
    }

    private void loadInstructorList() {
        instructorBox.removeAllItems();
        instIds.clear();
        try (Connection conn = DatabaseConfig.getMainDataSource().getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT user_id, employee_id, department FROM instructors")) {
            while(rs.next()) {
                instructorBox.addItem(rs.getString("employee_id")+" ("+rs.getString("department")+")");
                instIds.add(rs.getInt("user_id"));
            }
        } catch(Exception ex) {}
    }

    private void loadSectionTable() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConfig.getMainDataSource().getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT s.section_id, c.code, c.title, i.employee_id, i.department, s.day, s.time, s.room, s.semester, s.year, s.capacity " +
                "FROM sections s JOIN courses c ON s.course_id=c.course_id " +
                "JOIN instructors i ON s.instructor_id=i.user_id")) {
            while(rs.next()) {
                String course = rs.getString("code")+" - "+rs.getString("title");
                String instructor = rs.getString("employee_id")+" ("+rs.getString("department")+")";
                tableModel.addRow(new Object[]{
                    rs.getInt("section_id"), course, instructor,
                    rs.getString("day"), rs.getString("time"), rs.getString("room"),
                    rs.getString("semester"), rs.getInt("year"), rs.getInt("capacity"),
                    "Edit", "Delete"
                });
            }
        } catch(Exception ex){ }
    }

    private void clearFields() {
        dayField.setText(""); timeField.setText(""); roomField.setText("");
        semesterField.setText(""); yearField.setText(""); capacityField.setText("");
    }

    private void deleteSection(int sectionId) {
        try (Connection conn = DatabaseConfig.getMainDataSource().getConnection();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM sections WHERE section_id=?")) {
            stmt.setInt(1, sectionId);
            stmt.executeUpdate();
            loadSectionTable();
        } catch(Exception ex){
            JOptionPane.showMessageDialog(this, "Error deleting section: "+ex.getMessage());
        }
    }

    private void editSectionDialog(int sectionId) {
        // This can be expanded for a real modal edit dialog like previous panels
        JOptionPane.showMessageDialog(this,
            "Edit section dialog (expand to support updating fields).\nSection ID: "+sectionId);
        // TODO: Implement edit / update logic (similar to previous panels)
    }
}
