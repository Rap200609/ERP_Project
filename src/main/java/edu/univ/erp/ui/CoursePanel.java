package edu.univ.erp.ui;

import edu.univ.erp.api.admin.AdminCourseApi;
import edu.univ.erp.api.common.ApiResponse;
import edu.univ.erp.domain.CourseDetail;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Optional;

public class CoursePanel extends JPanel {
    private final AdminCourseApi courseApi = new AdminCourseApi();
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

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Course Code:"), gbc);
        gbc.gridx = 1;
        codeField = new JTextField(12);
        add(codeField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        titleField = new JTextField(18);
        add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Credits:"), gbc);
        gbc.gridx = 1;
        creditsField = new JTextField(5);
        add(creditsField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        descField = new JTextArea(3, 18);
        descField.setLineWrap(true);
        descField.setWrapStyleWord(true);
        add(new JScrollPane(descField), gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        JButton addButton = new JButton("Add Course");
        add(addButton, gbc);
        gbc.gridx = 1;
        messageLabel = new JLabel();
        add(messageLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.BOTH;
        String[] columns = {"ID", "Code", "Title", "Credits", "Description", "Edit", "Delete"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5 || column == 6;
            }
        };
        courseTable = new JTable(tableModel);
        add(new JScrollPane(courseTable), gbc);

        addButton.addActionListener(e -> addCourse());
        courseTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = courseTable.rowAtPoint(evt.getPoint());
                int col = courseTable.columnAtPoint(evt.getPoint());
                int courseId = (int) tableModel.getValueAt(row, 0);
                if (col == 5) {
                    editCourseDialog(courseId);
                } else if (col == 6) {
                    int confirm = JOptionPane.showConfirmDialog(CoursePanel.this,
                            "Delete course " + tableModel.getValueAt(row, 1) + "?");
                    if (confirm == JOptionPane.YES_OPTION) {
                        ApiResponse response = courseApi.deleteCourse(courseId);
                        if (!response.isSuccess()) {
                            JOptionPane.showMessageDialog(CoursePanel.this, response.getMessage());
                        }
                        loadCourseTable();
                    }
                }
            }
        });

        loadCourseTable();
    }

    private void loadCourseTable() {
        tableModel.setRowCount(0);
        for (edu.univ.erp.domain.CourseDetail course : courseApi.listCourses()) {
            tableModel.addRow(new Object[]{
                    course.getCourseId(),
                    course.getCode(),
                    course.getTitle(),
                    course.getCredits(),
                    course.getDescription(),
                    "Edit",
                    "Delete"
            });
        }
    }

    private void addCourse() {
        String code = codeField.getText().trim();
        String title = titleField.getText().trim();
        String creditsStr = creditsField.getText().trim();
        String desc = descField.getText().trim();

        int credits;
        try {
            credits = Integer.parseInt(creditsStr);
        } catch (NumberFormatException ex) {
            messageLabel.setText("Credits must be a number.");
            return;
        }

        ApiResponse response = courseApi.addCourse(code, title, credits, desc);
        messageLabel.setText(response.getMessage());
        if (response.isSuccess()) {
            codeField.setText("");
            titleField.setText("");
            creditsField.setText("");
            descField.setText("");
            loadCourseTable();
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

        Optional<CourseDetail> detailOpt = courseApi.loadCourse(courseId);
        if (detailOpt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Error loading course.");
            return;
        }
        CourseDetail detail = detailOpt.get();
        codeField.setText(detail.getCode());
        titleField.setText(detail.getTitle());
        creditsField.setText(String.valueOf(detail.getCredits()));
        descField.setText(detail.getDescription());

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
            ApiResponse response = courseApi.updateCourse(courseId, code, title, credits, desc);
            infoLabel.setText(response.getMessage());
            if (response.isSuccess()) {
                loadCourseTable();
            }
        });

        dialog.setVisible(true);
    }

}
