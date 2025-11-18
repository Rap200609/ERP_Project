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
        setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(UITheme.BG_MAIN);
        headerPanel.setBorder(new javax.swing.border.EmptyBorder(0, 0, 20, 0));
        JLabel titleLabel = new JLabel("Manage Courses");
        UITheme.styleHeadingLabel(titleLabel);
        titleLabel.setFont(UITheme.FONT_HEADING);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(UITheme.BG_MAIN);
        mainPanel.setBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0));
        
        // Form panel (card style)
        JPanel formPanel = new JPanel();
        UITheme.styleCardPanel(formPanel);
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel codeLabel = new JLabel("Course Code:");
        UITheme.styleLabel(codeLabel, true);
        formPanel.add(codeLabel, gbc);
        gbc.gridx = 1;
        codeField = new JTextField(15);
        UITheme.styleTextField(codeField);
        formPanel.add(codeField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel titleLabel2 = new JLabel("Title:");
        UITheme.styleLabel(titleLabel2, true);
        formPanel.add(titleLabel2, gbc);
        gbc.gridx = 1;
        titleField = new JTextField(20);
        UITheme.styleTextField(titleField);
        formPanel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel creditsLabel = new JLabel("Credits:");
        UITheme.styleLabel(creditsLabel, true);
        formPanel.add(creditsLabel, gbc);
        gbc.gridx = 1;
        creditsField = new JTextField(5);
        UITheme.styleTextField(creditsField);
        formPanel.add(creditsField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        JLabel descLabel = new JLabel("Description:");
        UITheme.styleLabel(descLabel, true);
        formPanel.add(descLabel, gbc);
        gbc.gridx = 1;
        descField = new JTextArea(3, 20);
        descField.setLineWrap(true);
        descField.setWrapStyleWord(true);
        descField.setFont(UITheme.FONT_BODY);
        descField.setBorder(UITheme.BORDER_FIELD);
        formPanel.add(new JScrollPane(descField), gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        JButton addButton = new JButton("Add Course");
        UITheme.stylePrimaryButton(addButton);
        formPanel.add(addButton, gbc);
        gbc.gridx = 1;
        messageLabel = new JLabel();
        messageLabel.setFont(UITheme.FONT_SMALL);
        formPanel.add(messageLabel, gbc);
        
        mainPanel.add(formPanel, BorderLayout.NORTH);

        // Table panel - fills center
        String[] columns = {"ID", "Code", "Title", "Credits", "Description", "Edit", "Delete"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5 || column == 6;
            }
        };
        courseTable = new JTable(tableModel);
        UITheme.styleTable(courseTable);
        JScrollPane scrollPane = new JScrollPane(courseTable);
        UITheme.styleScrollPane(scrollPane);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);

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
        messageLabel.setForeground(response.isSuccess() ? UITheme.ACCENT_SUCCESS : UITheme.ACCENT_ERROR);
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
        dialog.setSize(550, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(true);
        dialog.getContentPane().setBackground(UITheme.BG_MAIN);
        dialog.setLayout(new BorderLayout());
        
        JPanel contentPanel = new JPanel();
        UITheme.styleCardPanel(contentPanel);
        contentPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField codeField = new JTextField(15);
        UITheme.styleTextField(codeField);
        JTextField titleField = new JTextField(20);
        UITheme.styleTextField(titleField);
        JTextField creditsField = new JTextField(5);
        UITheme.styleTextField(creditsField);
        JTextArea descField = new JTextArea(3, 20);
        descField.setLineWrap(true);
        descField.setWrapStyleWord(true);
        descField.setFont(UITheme.FONT_BODY);
        descField.setBorder(UITheme.BORDER_FIELD);

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

        gbc.gridx=0; gbc.gridy=0;
        JLabel codeLabel = new JLabel("Code:");
        UITheme.styleLabel(codeLabel, true);
        contentPanel.add(codeLabel, gbc);
        gbc.gridx=1; contentPanel.add(codeField, gbc);
        gbc.gridx=0; gbc.gridy=1;
        JLabel titleLabel = new JLabel("Title:");
        UITheme.styleLabel(titleLabel, true);
        contentPanel.add(titleLabel, gbc);
        gbc.gridx=1; contentPanel.add(titleField, gbc);
        gbc.gridx=0; gbc.gridy=2;
        JLabel creditsLabel = new JLabel("Credits:");
        UITheme.styleLabel(creditsLabel, true);
        contentPanel.add(creditsLabel, gbc);
        gbc.gridx=1; contentPanel.add(creditsField, gbc);
        gbc.gridx=0; gbc.gridy=3;
        JLabel descLabel = new JLabel("Description:");
        UITheme.styleLabel(descLabel, true);
        contentPanel.add(descLabel, gbc);
        gbc.gridx=1; contentPanel.add(new JScrollPane(descField), gbc);

        JButton saveBtn = new JButton("Save Changes");
        UITheme.stylePrimaryButton(saveBtn);
        JLabel infoLabel = new JLabel();
        infoLabel.setFont(UITheme.FONT_SMALL);
        gbc.gridx=0; gbc.gridy=4; gbc.gridwidth=2; gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(saveBtn, gbc);
        gbc.gridy=5; gbc.anchor = GridBagConstraints.WEST;
        contentPanel.add(infoLabel, gbc);
        
        dialog.add(contentPanel, BorderLayout.CENTER);

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
            infoLabel.setForeground(response.isSuccess() ? UITheme.ACCENT_SUCCESS : UITheme.ACCENT_ERROR);
            if (response.isSuccess()) {
                loadCourseTable();
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }

}
