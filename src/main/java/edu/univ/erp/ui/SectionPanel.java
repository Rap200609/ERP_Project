package edu.univ.erp.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.univ.erp.api.SectionApi;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Section;

public class SectionPanel extends JPanel {
    private JComboBox<String> courseBox;
    private JTextField secCodeField, dayField, timeField, roomField, semesterField, yearField, capacityField;
    private JLabel messageLabel;
    private DefaultTableModel tableModel;
    private JTable sectionTable;
    private ArrayList<Integer> courseIds = new ArrayList<>();
    private final SectionApi sectionApi = new SectionApi();

    public SectionPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Section Code
        gbc.gridx=0; gbc.gridy=0;
        add(new JLabel("Section Code:"), gbc);
        gbc.gridx=1;
        secCodeField = new JTextField(6);
        add(secCodeField, gbc);

        // Course selector
        gbc.gridx=0; gbc.gridy=1;
        add(new JLabel("Course:"), gbc);
        gbc.gridx=1;
        courseBox = new JComboBox<>();
        add(courseBox, gbc);

        // Day, Time, Room
        gbc.gridx=0; gbc.gridy=2;
        add(new JLabel("Day:"), gbc);
        gbc.gridx=1;
        dayField = new JTextField(10);
        add(dayField, gbc);

        gbc.gridx=0; gbc.gridy=3;
        add(new JLabel("Time:"), gbc);
        gbc.gridx=1;
        timeField = new JTextField(12);
        add(timeField, gbc);

        gbc.gridx=0; gbc.gridy=4;
        add(new JLabel("Room:"), gbc);
        gbc.gridx=1;
        roomField = new JTextField(10);
        add(roomField, gbc);

        // Semester, Year, Capacity
        gbc.gridx=0; gbc.gridy=5;
        add(new JLabel("Semester:"), gbc);
        gbc.gridx=1;
        semesterField = new JTextField(10);
        add(semesterField, gbc);

        gbc.gridx=0; gbc.gridy=6;
        add(new JLabel("Year:"), gbc);
        gbc.gridx=1;
        yearField = new JTextField(8);
        add(yearField, gbc);

        gbc.gridx=0; gbc.gridy=7;
        add(new JLabel("Capacity:"), gbc);
        gbc.gridx=1;
        capacityField = new JTextField(8);
        add(capacityField, gbc);

        // Add button, message
        gbc.gridx=0; gbc.gridy=8;
        JButton addButton = new JButton("Add Section");
        add(addButton, gbc);
        gbc.gridx=1;
        messageLabel = new JLabel();
        add(messageLabel, gbc);

        // Section table
        gbc.gridx=0; gbc.gridy=9; gbc.gridwidth=2; gbc.fill=GridBagConstraints.BOTH;
        String[] columns = {"ID", "Section Code", "Course", "Day", "Time", "Room", "Semester", "Year", "Capacity", "Edit", "Delete"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return col == 9 || col == 10; }
        };
        sectionTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(sectionTable);
        add(scrollPane, gbc);

        // Live dropdown update (refreshes on panel show)
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                loadCourseList();
                loadSectionTable();
            }
        });

        loadCourseList();
        loadSectionTable();

        addButton.addActionListener(e -> {
            int selCourse = courseBox.getSelectedIndex();
            String secCode = secCodeField.getText().trim();
            String day = dayField.getText().trim();
            String time = timeField.getText().trim();
            String room = roomField.getText().trim();
            String semester = semesterField.getText().trim();
            String yearStr = yearField.getText().trim();
            String capStr = capacityField.getText().trim();

            if(secCode.isEmpty() || selCourse < 0 || day.isEmpty() || time.isEmpty()
                || room.isEmpty() || semester.isEmpty() || yearStr.isEmpty() || capStr.isEmpty()) {
                messageLabel.setText("All fields required.");
                return;
            }
            int year, capacity;
            try {
                year = Integer.parseInt(yearStr);
                capacity = Integer.parseInt(capStr);
                if (capacity <= 0) throw new NumberFormatException();
            } catch(NumberFormatException ex) {
                messageLabel.setText("Year/capacity must be numbers, capacity > 0.");
                return;
            }
            try {
                Section s = new Section(0, courseIds.get(selCourse), secCode, day, time, room, semester, year, capacity);
                sectionApi.addSection(s);
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
                if(col == 9) {
                    editSectionDialog(sectionId);
                } else if(col == 10) {
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
        List<Course> courses = sectionApi.getCourses();
        for (Course c : courses) {
            courseBox.addItem(c.getDisplayName());
            courseIds.add(c.getCourseId());
        }
    }

    private void loadSectionTable() {
        tableModel.setRowCount(0);
        try {
            List<Course> courses = sectionApi.getCourses();
            Map<Integer, String> courseIdToName = new HashMap<>();
            for (Course c : courses) courseIdToName.put(c.getCourseId(), c.getDisplayName());

            for (Section s : sectionApi.getSections()) {
                String courseName = courseIdToName.getOrDefault(s.getCourseId(), String.valueOf(s.getCourseId()));
                tableModel.addRow(new Object[]{
                    s.getSectionId(),
                    s.getSectionCode(),
                    courseName,
                    s.getDay(),
                    s.getTime(),
                    s.getRoom(),
                    s.getSemester(),
                    s.getYear(),
                    s.getCapacity(),
                    "Edit", "Delete"
                });
            }
        } catch (Exception ignored) { }
    }

    private void clearFields() {
        secCodeField.setText(""); dayField.setText(""); timeField.setText("");
        roomField.setText(""); semesterField.setText(""); yearField.setText("");
        capacityField.setText("");
    }

    private void deleteSection(int sectionId) {
        try {
            sectionApi.deleteSection(sectionId);
            loadSectionTable();
        } catch(Exception ex){
            JOptionPane.showMessageDialog(this, "Error deleting section: "+ex.getMessage());
        }
    }

    private void editSectionDialog(int sectionId) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Edit Section", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(450, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 7, 7, 7); 
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField codeField = new JTextField(15);
        JComboBox<String> courseField = new JComboBox<>();
        JTextField dayField = new JTextField(15);
        JTextField timeField = new JTextField(15);
        JTextField roomField = new JTextField(15);
        JTextField semesterField = new JTextField(15);
        JTextField yearField = new JTextField(15);
        JTextField capacityField = new JTextField(15);

        ArrayList<Integer> localCourseIds = new ArrayList<>();
        List<Course> allCourses = sectionApi.getCourses();
        for (Course c : allCourses) {
            courseField.addItem(c.getDisplayName());
            localCourseIds.add(c.getCourseId());
        }

        // Load current data
        int courseIdx = 0;
        try {
            Section s = sectionApi.getSection(sectionId);
            if (s != null) {
                codeField.setText(s.getSectionCode());
                courseIdx = localCourseIds.indexOf(s.getCourseId());
                dayField.setText(s.getDay());
                timeField.setText(s.getTime());
                roomField.setText(s.getRoom());
                semesterField.setText(s.getSemester());
                yearField.setText(String.valueOf(s.getYear()));
                capacityField.setText(String.valueOf(s.getCapacity()));
            }
        } catch(Exception ex){
            JOptionPane.showMessageDialog(this,"Error loading section: "+ex.getMessage());
            return;
        }
        if(courseIdx >= 0 && courseIdx < localCourseIds.size()) {
            courseField.setSelectedIndex(courseIdx);
        } else {
            courseField.setSelectedIndex(0);
        }

        gbc.weightx = 0.0;
        gbc.gridx=0; gbc.gridy=0; dialog.add(new JLabel("Section Code:"), gbc);
        gbc.weightx = 1.0;
        gbc.gridx=1; dialog.add(codeField, gbc);
        
        gbc.weightx = 0.0;
        gbc.gridx=0; gbc.gridy=1; dialog.add(new JLabel("Course:"), gbc);
        gbc.weightx = 1.0;
        gbc.gridx=1; dialog.add(courseField, gbc);
        
        gbc.weightx = 0.0;
        gbc.gridx=0; gbc.gridy=2; dialog.add(new JLabel("Day:"), gbc);
        gbc.weightx = 1.0;
        gbc.gridx=1; dialog.add(dayField, gbc);
        
        gbc.weightx = 0.0;
        gbc.gridx=0; gbc.gridy=3; dialog.add(new JLabel("Time:"), gbc);
        gbc.weightx = 1.0;
        gbc.gridx=1; dialog.add(timeField, gbc);
        
        gbc.weightx = 0.0;
        gbc.gridx=0; gbc.gridy=4; dialog.add(new JLabel("Room:"), gbc);
        gbc.weightx = 1.0;
        gbc.gridx=1; dialog.add(roomField, gbc);
        
        gbc.weightx = 0.0;
        gbc.gridx=0; gbc.gridy=5; dialog.add(new JLabel("Semester:"), gbc);
        gbc.weightx = 1.0;
        gbc.gridx=1; dialog.add(semesterField, gbc);
        
        gbc.weightx = 0.0;
        gbc.gridx=0; gbc.gridy=6; dialog.add(new JLabel("Year:"), gbc);
        gbc.weightx = 1.0;
        gbc.gridx=1; dialog.add(yearField, gbc);
        
        gbc.weightx = 0.0;
        gbc.gridx=0; gbc.gridy=7; dialog.add(new JLabel("Capacity:"), gbc);
        gbc.weightx = 1.0;
        gbc.gridx=1; dialog.add(capacityField, gbc);

        JButton saveBtn = new JButton("Save Changes");
        JLabel infoLabel = new JLabel();

        gbc.weightx = 1.0;
        gbc.gridx=0; gbc.gridy=8; gbc.gridwidth=2; dialog.add(saveBtn, gbc);
        gbc.gridy=9; gbc.gridwidth=2; dialog.add(infoLabel, gbc);

        saveBtn.addActionListener(e -> {
            int courseIdxSel = courseField.getSelectedIndex();
            String secCode = codeField.getText().trim();
            String day = dayField.getText().trim();
            String time = timeField.getText().trim();
            String room = roomField.getText().trim();
            String semester = semesterField.getText().trim();
            String yearStr = yearField.getText().trim();
            String capStr = capacityField.getText().trim();
            if(secCode.isEmpty() || courseIdxSel<0 || day.isEmpty() || time.isEmpty()
                || room.isEmpty() || semester.isEmpty() || yearStr.isEmpty() || capStr.isEmpty()) {
                infoLabel.setText("All fields required.");
                return;
            }
            int year, capacity;
            try {
                year = Integer.parseInt(yearStr);
                capacity = Integer.parseInt(capStr);
                if (capacity <= 0) throw new NumberFormatException();
            } catch(NumberFormatException ex) {
                infoLabel.setText("Year/capacity must be numbers, cap>0.");
                return;
            }
            try {
                Section updated = new Section(sectionId, localCourseIds.get(courseIdxSel), secCode, day, time, room, semester, year, capacity);
                sectionApi.updateSection(updated);
                infoLabel.setText("Section updated!");
                loadSectionTable();
            } catch(Exception ex) {
                infoLabel.setText("Error: "+ex.getMessage());
            }
        });
        dialog.setVisible(true);
    }

}
