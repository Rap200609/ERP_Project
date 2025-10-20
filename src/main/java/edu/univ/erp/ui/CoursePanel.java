package edu.univ.erp.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import edu.univ.erp.data.DatabaseConfig;

public class CoursePanel extends JPanel {
    private JTextField codeField, titleField, creditsField;
    private JTextArea descField;
    private JLabel messageLabel;
    private DefaultTableModel tableModel;
    private JTable courseTable;

    public CoursePanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Course code row
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Course Code:"), gbc);
        gbc.gridx = 1;
        codeField = new JTextField(12);
        add(codeField, gbc);

        // Title row
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        titleField = new JTextField(18);
        add(titleField, gbc);

        // Credits row
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Credits:"), gbc);
        gbc.gridx = 1;
        creditsField = new JTextField(5);
        add(creditsField, gbc);

        // Description row
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        descField = new JTextArea(3, 18);
        descField.setLineWrap(true);
        descField.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descField);
        add(descScroll, gbc);

        // Button row
        gbc.gridx = 0; gbc.gridy = 4;
        JButton addButton = new JButton("Add Course");
        add(addButton, gbc);
        gbc.gridx = 1;
        messageLabel = new JLabel();
        add(messageLabel, gbc);

        // Table section
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.BOTH;
        String[] columns = {"ID", "Code", "Title", "Credits", "Description", "Edit", "Delete"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return col == 5 || col == 6;
            }
        };
        courseTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(courseTable);
        add(scrollPane, gbc);

        loadCourseTable();

        // Add course logic
        addButton.addActionListener(e -> {
            String code = codeField.getText().trim();
            String title = titleField.getText().trim();
            String creditsStr = creditsField.getText().trim();
            String desc = descField.getText().trim();

            if (code.isEmpty() || title.isEmpty() || creditsStr.isEmpty()) {
                messageLabel.setText("All fields except description are required.");
                return;
            }

            int credits;
            try {
                credits = Integer.parseInt(creditsStr);
            } catch (NumberFormatException ex) {
                messageLabel.setText("Credits must be a number.");
                return;
            }

            String sql = "INSERT INTO courses (code, title, credits, description) VALUES (?, ?, ?, ?)";
            try (Connection conn = DatabaseConfig.getMainDataSource().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, code);
                stmt.setString(2, title);
                stmt.setInt(3, credits);
                stmt.setString(4, desc);
                stmt.executeUpdate();
                messageLabel.setText("Course added successfully!");
                codeField.setText(""); titleField.setText(""); creditsField.setText(""); descField.setText("");
                loadCourseTable();
            } catch (Exception ex) {
                messageLabel.setText("Error: " + ex.getMessage());
            }
        });

        // Table mouse click logic for Edit/Delete
        courseTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = courseTable.rowAtPoint(evt.getPoint());
                int col = courseTable.columnAtPoint(evt.getPoint());
                int courseId = (int) tableModel.getValueAt(row, 0);
                String code = (String) tableModel.getValueAt(row, 1);
                if (col == 5) { // Edit
                    editCourseDialog(courseId);
                } else if (col == 6) { // Delete
                    int confirm = JOptionPane.showConfirmDialog(CoursePanel.this,
                        "Delete course " + code + "?");
                    if (confirm == JOptionPane.YES_OPTION) {
                        deleteCourse(courseId);
                    }
                }
            }
        });
    }

    private void loadCourseTable() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConfig.getMainDataSource().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                "SELECT course_id, code, title, credits, description FROM courses")) {
            while (rs.next()) {
                tableModel.addRow(new Object[] {
                    rs.getInt("course_id"),
                    rs.getString("code"),
                    rs.getString("title"),
                    rs.getInt("credits"),
                    rs.getString("description"),
                    "Edit",
                    "Delete"
                });
            }
        } catch (Exception e) {
            // Could show/log error here if needed
        }
    }

    private void deleteCourse(int courseId) {
        try (Connection conn = DatabaseConfig.getMainDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM courses WHERE course_id = ?")) {
            stmt.setInt(1, courseId);
            stmt.executeUpdate();
            loadCourseTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting course: " + e.getMessage());
        }
    }

    private void editCourseDialog(int courseId) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Edit Course", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(320, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 7, 7, 7); gbc.anchor = GridBagConstraints.WEST;

        JTextField codeField = new JTextField(14);
        JTextField titleField = new JTextField(18);
        JTextField creditsField = new JTextField(5);
        JTextArea descField = new JTextArea(3,18);
        descField.setLineWrap(true); descField.setWrapStyleWord(true);

        // Load course info
        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT code, title, credits, description FROM courses WHERE course_id=?")) {
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                codeField.setText(rs.getString("code"));
                titleField.setText(rs.getString("title"));
                creditsField.setText(String.valueOf(rs.getInt("credits")));
                descField.setText(rs.getString("description"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading course: " + ex.getMessage());
            return;
        }

        gbc.gridx=0; gbc.gridy=0; dialog.add(new JLabel("Code:"), gbc);
        gbc.gridx=1; dialog.add(codeField, gbc);
        gbc.gridx=0; gbc.gridy=1; dialog.add(new JLabel("Title:"), gbc);
        gbc.gridx=1; dialog.add(titleField, gbc);
        gbc.gridx=0; gbc.gridy=2; dialog.add(new JLabel("Credits:"), gbc);
        gbc.gridx=1; dialog.add(creditsField, gbc);
        gbc.gridx=0; gbc.gridy=3; dialog.add(new JLabel("Description:"), gbc);
        gbc.gridx=1; dialog.add(new JScrollPane(descField), gbc);

        JButton saveBtn = new JButton("Save Changes");
        JLabel infoLabel = new JLabel();
        gbc.gridx=0; gbc.gridy=4; gbc.gridwidth=2; dialog.add(saveBtn, gbc);
        gbc.gridy=5; dialog.add(infoLabel, gbc);

        saveBtn.addActionListener(e -> {
            String code = codeField.getText().trim();
            String title = titleField.getText().trim();
            String creditsStr = creditsField.getText().trim();
            String desc = descField.getText().trim();

            if (code.isEmpty() || title.isEmpty() || creditsStr.isEmpty()) {
                infoLabel.setText("All fields except description required.");
                return;
            }
            int credits;
            try {
                credits = Integer.parseInt(creditsStr);
            } catch (NumberFormatException ex2) {
                infoLabel.setText("Credits must be a number.");
                return;
            }
            try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE courses SET code=?, title=?, credits=?, description=? WHERE course_id=?")) {
                stmt.setString(1, code);
                stmt.setString(2, title);
                stmt.setInt(3, credits);
                stmt.setString(4, desc);
                stmt.setInt(5, courseId);
                stmt.executeUpdate();
                infoLabel.setText("Course updated!");
                loadCourseTable();
            } catch (Exception ex2) {
                infoLabel.setText("Error: " + ex2.getMessage());
            }
        });

        dialog.setVisible(true);
    }

}
