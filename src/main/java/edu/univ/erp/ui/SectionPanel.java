package edu.univ.erp.ui;

import edu.univ.erp.api.admin.AdminSectionApi;
import edu.univ.erp.api.common.ApiResponse;
import edu.univ.erp.domain.CourseDetail;
import edu.univ.erp.domain.SectionDetail;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class SectionPanel extends JPanel {
    private final AdminSectionApi sectionApi = new AdminSectionApi();

    private JComboBox<String> courseBox;
    private JTextField sectionCodeField;
    private JTextField dayField;
    private JTextField timeField;
    private JTextField roomField;
    private JTextField semesterField;
    private JTextField yearField;
    private JTextField capacityField;
    private JLabel messageLabel;
    private DefaultTableModel tableModel;
    private JTable sectionTable;
    private List<CourseDetail> courses = List.of();

    public SectionPanel() {
        setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout());
        
        // Form panel at top
        JPanel formPanel = new JPanel();
        UITheme.styleCardPanel(formPanel);
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel codeLabel = new JLabel("Section Code:");
        UITheme.styleLabel(codeLabel, true);
        formPanel.add(codeLabel, gbc);
        gbc.gridx = 1;
        sectionCodeField = new JTextField(10);
        UITheme.styleTextField(sectionCodeField);
        formPanel.add(sectionCodeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel courseLabel = new JLabel("Course:");
        UITheme.styleLabel(courseLabel, true);
        formPanel.add(courseLabel, gbc);
        gbc.gridx = 1;
        courseBox = new JComboBox<>();
        UITheme.styleComboBox(courseBox);
        formPanel.add(courseBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel dayLabel = new JLabel("Day:");
        UITheme.styleLabel(dayLabel, true);
        formPanel.add(dayLabel, gbc);
        gbc.gridx = 1;
        dayField = new JTextField(12);
        UITheme.styleTextField(dayField);
        formPanel.add(dayField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel timeLabel = new JLabel("Time:");
        UITheme.styleLabel(timeLabel, true);
        formPanel.add(timeLabel, gbc);
        gbc.gridx = 1;
        timeField = new JTextField(12);
        UITheme.styleTextField(timeField);
        formPanel.add(timeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel roomLabel = new JLabel("Room:");
        UITheme.styleLabel(roomLabel, true);
        formPanel.add(roomLabel, gbc);
        gbc.gridx = 1;
        roomField = new JTextField(12);
        UITheme.styleTextField(roomField);
        formPanel.add(roomField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel semesterLabel = new JLabel("Semester:");
        UITheme.styleLabel(semesterLabel, true);
        formPanel.add(semesterLabel, gbc);
        gbc.gridx = 1;
        semesterField = new JTextField(12);
        UITheme.styleTextField(semesterField);
        formPanel.add(semesterField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel yearLabel = new JLabel("Year:");
        UITheme.styleLabel(yearLabel, true);
        formPanel.add(yearLabel, gbc);
        gbc.gridx = 1;
        yearField = new JTextField(8);
        UITheme.styleTextField(yearField);
        formPanel.add(yearField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        JLabel capacityLabel = new JLabel("Capacity:");
        UITheme.styleLabel(capacityLabel, true);
        formPanel.add(capacityLabel, gbc);
        gbc.gridx = 1;
        capacityField = new JTextField(8);
        UITheme.styleTextField(capacityField);
        formPanel.add(capacityField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        JButton addButton = new JButton("Add Section");
        UITheme.stylePrimaryButton(addButton);
        formPanel.add(addButton, gbc);
        gbc.gridx = 1;
        messageLabel = new JLabel();
        messageLabel.setFont(UITheme.FONT_BODY);
        formPanel.add(messageLabel, gbc);
        
        add(formPanel, BorderLayout.NORTH);

        // Table panel filling center
        String[] columns = {
                "ID", "Section Code", "Course", "Day", "Time", "Room",
                "Semester", "Year", "Capacity", "Edit", "Delete"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 9 || column == 10;
            }
        };
        sectionTable = new JTable(tableModel);
        UITheme.styleTable(sectionTable);
        JScrollPane scrollPane = new JScrollPane(sectionTable);
        UITheme.styleScrollPane(scrollPane);
        add(scrollPane, BorderLayout.CENTER);

        addButton.addActionListener(e -> addSection());
        sectionTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = sectionTable.rowAtPoint(evt.getPoint());
                int col = sectionTable.columnAtPoint(evt.getPoint());
                if (row < 0) {
                    return;
                }
                int sectionId = (int) tableModel.getValueAt(row, 0);
                if (col == 9) {
                    editSectionDialog(sectionId);
                } else if (col == 10) {
                    confirmDeleteSection(sectionId);
                }
            }
        });

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                loadCourseList();
                loadSectionTable();
            }
        });

        loadCourseList();
        loadSectionTable();
    }

    private void addSection() {
        AdminSectionApi.SectionCommand command = buildCommandFromFields();
        if (command == null) {
            return;
        }
        ApiResponse response = sectionApi.addSection(command);
        messageLabel.setText(response.getMessage());
        messageLabel.setForeground(response.isSuccess() ? UITheme.ACCENT_SUCCESS : UITheme.ACCENT_ERROR);
        if (response.isSuccess()) {
            clearFields();
            loadSectionTable();
        }
    }

    private AdminSectionApi.SectionCommand buildCommandFromFields() {
        Integer courseId = resolveSelectedCourseId();
        if (courseId == null) {
            messageLabel.setText("Select a course.");
            return null;
        }
        String sectionCode = sectionCodeField.getText().trim();
        String day = dayField.getText().trim();
        String time = timeField.getText().trim();
        String room = roomField.getText().trim();
        String semester = semesterField.getText().trim();
        String yearText = yearField.getText().trim();
        String capacityText = capacityField.getText().trim();

        if (sectionCode.isBlank() || day.isBlank() || time.isBlank()
                || room.isBlank() || semester.isBlank() || yearText.isBlank() || capacityText.isBlank()) {
            messageLabel.setText("All fields are required.");
            return null;
        }

        AdminSectionApi.SectionCommand command = new AdminSectionApi.SectionCommand();
        command.courseId = courseId;
        command.sectionCode = sectionCode;
        command.day = day;
        command.time = time;
        command.room = room;
        command.semester = semester;
        try {
            command.year = Integer.parseInt(yearText);
            command.capacity = Integer.parseInt(capacityText);
        } catch (NumberFormatException ex) {
            messageLabel.setText("Year and capacity must be numbers.");
            return null;
        }
        return command;
    }

    private void loadCourseList() {
        courses = sectionApi.listCourses();
        courseBox.removeAllItems();
        for (CourseDetail detail : courses) {
            courseBox.addItem(detail.getCourseDisplay());
        }
        if (!courses.isEmpty()) {
            courseBox.setSelectedIndex(0);
        }
    }

    private void loadSectionTable() {
        tableModel.setRowCount(0);
        for (SectionDetail detail : sectionApi.listSections()) {
            tableModel.addRow(new Object[]{
                    detail.getSectionId(),
                    detail.getSectionCode(),
                    detail.getCourseDisplay(),
                    detail.getDay(),
                    detail.getTime(),
                    detail.getRoom(),
                    detail.getSemester(),
                    detail.getYear(),
                    detail.getCapacity(),
                    "Edit",
                    "Delete"
            });
        }
    }

    private void clearFields() {
        sectionCodeField.setText("");
        dayField.setText("");
        timeField.setText("");
        roomField.setText("");
        semesterField.setText("");
        yearField.setText("");
        capacityField.setText("");
        if (courseBox.getItemCount() > 0) {
            courseBox.setSelectedIndex(0);
        }
    }

    private Integer resolveSelectedCourseId() {
        int index = courseBox.getSelectedIndex();
        if (index < 0 || index >= courses.size()) {
            return null;
        }
        return courses.get(index).getCourseId();
    }

    private void confirmDeleteSection(int sectionId) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete section?",
                "Confirm",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            ApiResponse response = sectionApi.deleteSection(sectionId);
            if (!response.isSuccess()) {
                JOptionPane.showMessageDialog(this, response.getMessage());
            }
            loadSectionTable();
        }
    }

    private void editSectionDialog(int sectionId) {
        Optional<SectionDetail> detailOpt = sectionApi.loadSection(sectionId);
        if (detailOpt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Section not found.");
            return;
        }
        SectionDetail detail = detailOpt.get();

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Edit Section", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(600, 650);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(true);
        dialog.getContentPane().setBackground(UITheme.BG_MAIN);
        dialog.setLayout(new BorderLayout());
        
        JPanel contentPanel = new JPanel();
        UITheme.styleCardPanel(contentPanel);
        contentPanel.setLayout(new GridBagLayout());
        GridBagConstraints base = new GridBagConstraints();
        base.insets = new Insets(10, 10, 10, 10);
        base.anchor = GridBagConstraints.WEST;
        base.fill = GridBagConstraints.HORIZONTAL;

        JTextField codeField = new JTextField(detail.getSectionCode(), 15);
        UITheme.styleTextField(codeField);
        JComboBox<String> courseField = new JComboBox<>();
        UITheme.styleComboBox(courseField);
        courses.forEach(c -> courseField.addItem(c.getCourseDisplay()));
        int selectedIndex = findCourseIndex(detail.getCourseId());
        if (selectedIndex >= 0) {
            courseField.setSelectedIndex(selectedIndex);
        }
        JTextField dayField = new JTextField(detail.getDay(), 15);
        UITheme.styleTextField(dayField);
        JTextField timeField = new JTextField(detail.getTime(), 15);
        UITheme.styleTextField(timeField);
        JTextField roomField = new JTextField(detail.getRoom(), 15);
        UITheme.styleTextField(roomField);
        JTextField semesterField = new JTextField(detail.getSemester(), 15);
        UITheme.styleTextField(semesterField);
        JTextField yearField = new JTextField(String.valueOf(detail.getYear()), 10);
        UITheme.styleTextField(yearField);
        JTextField capacityField = new JTextField(String.valueOf(detail.getCapacity()), 10);
        UITheme.styleTextField(capacityField);

        int row = 0;
        addRow(contentPanel, base, row++, "Section Code:", codeField);
        addRow(contentPanel, base, row++, "Course:", courseField);
        addRow(contentPanel, base, row++, "Day:", dayField);
        addRow(contentPanel, base, row++, "Time:", timeField);
        addRow(contentPanel, base, row++, "Room:", roomField);
        addRow(contentPanel, base, row++, "Semester:", semesterField);
        addRow(contentPanel, base, row++, "Year:", yearField);
        addRow(contentPanel, base, row++, "Capacity:", capacityField);

        JButton saveBtn = new JButton("Save Changes");
        UITheme.stylePrimaryButton(saveBtn);
        JLabel infoLabel = new JLabel();
        infoLabel.setFont(UITheme.FONT_BODY);
        GridBagConstraints btnConstraints = (GridBagConstraints) base.clone();
        btnConstraints.gridx = 0;
        btnConstraints.gridy = row;
        btnConstraints.gridwidth = 2;
        btnConstraints.anchor = GridBagConstraints.CENTER;
        btnConstraints.fill = GridBagConstraints.NONE;
        contentPanel.add(saveBtn, btnConstraints);
        GridBagConstraints infoConstraints = (GridBagConstraints) base.clone();
        infoConstraints.gridx = 0;
        infoConstraints.gridy = row + 1;
        infoConstraints.gridwidth = 2;
        infoConstraints.anchor = GridBagConstraints.WEST;
        contentPanel.add(infoLabel, infoConstraints);
        
        // Make content scrollable
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(UITheme.BG_PANEL);
        dialog.add(scrollPane, BorderLayout.CENTER);

        saveBtn.addActionListener(e -> {
            AdminSectionApi.SectionCommand command = new AdminSectionApi.SectionCommand();
            command.courseId = resolveSelectedCourseId(courseField.getSelectedIndex());
            command.sectionCode = codeField.getText().trim();
            command.day = dayField.getText().trim();
            command.time = timeField.getText().trim();
            command.room = roomField.getText().trim();
            command.semester = semesterField.getText().trim();
            try {
                command.year = Integer.parseInt(yearField.getText().trim());
                command.capacity = Integer.parseInt(capacityField.getText().trim());
            } catch (NumberFormatException ex) {
                infoLabel.setText("Year/capacity must be numeric.");
                return;
            }
            ApiResponse response = sectionApi.updateSection(sectionId, command);
            infoLabel.setText(response.getMessage());
            infoLabel.setForeground(response.isSuccess() ? UITheme.ACCENT_SUCCESS : UITheme.ACCENT_ERROR);
            if (response.isSuccess()) {
                loadSectionTable();
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }

    private Integer resolveSelectedCourseId(int index) {
        if (index < 0 || index >= courses.size()) {
            return null;
        }
        return courses.get(index).getCourseId();
    }

    private int findCourseIndex(int courseId) {
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getCourseId() == courseId) {
                return i;
            }
        }
        return -1;
    }

    private void addRow(JPanel panel, GridBagConstraints base, int row, String labelText, JComponent field) {
        GridBagConstraints labelConstraints = (GridBagConstraints) base.clone();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.weightx = 0;
        JLabel label = new JLabel(labelText);
        UITheme.styleLabel(label, true);
        panel.add(label, labelConstraints);

        GridBagConstraints fieldConstraints = (GridBagConstraints) base.clone();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = row;
        fieldConstraints.weightx = 1.0;
        panel.add(field, fieldConstraints);
    }
}

