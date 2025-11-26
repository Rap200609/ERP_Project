package edu.univ.erp.ui;

import edu.univ.erp.api.admin.AdminApi;
import edu.univ.erp.api.common.ApiResponse;
import edu.univ.erp.domain.InstructorProfile;
import edu.univ.erp.domain.StudentProfile;
import edu.univ.erp.domain.UserAccount;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class AddUserPanel extends JPanel {
    private final AdminApi adminApi;
    private final JTextField userField;
    private final JPasswordField passField;
    private final JComboBox<String> roleBox;
    private final JLabel messageLabel;
    private final DefaultTableModel tableModel;
    private final JTable userTable;
    private final JPanel extraFieldsPanel;

    private final JTextField rollNoField;
    private final JTextField programField;
    private final JTextField yearField;
    private final JTextField studentEmailField;

    private final JTextField empIdField;
    private final JTextField deptField;
    private final JTextField instructorEmailField;

    // Using UITheme for consistent styling

    public AddUserPanel() {
        this(new AdminApi());
    }

    public AddUserPanel(AdminApi adminApi) {
        this.adminApi = adminApi;
        setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel();
        UITheme.styleCardPanel(formPanel);
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(UITheme.PRIMARY_DARK);
        userLabel.setFont(UITheme.FONT_BODY_BOLD);
        formPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        userField = createTextField(15);
        formPanel.add(userField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(UITheme.PRIMARY_DARK);
        passLabel.setFont(UITheme.FONT_BODY_BOLD);
        formPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        passField = new JPasswordField(15);
        styleField(passField);
        formPanel.add(passField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setForeground(UITheme.PRIMARY_DARK);
        roleLabel.setFont(UITheme.FONT_BODY_BOLD);
        formPanel.add(roleLabel, gbc);

        gbc.gridx = 1;
        roleBox = new JComboBox<>(new String[]{"STUDENT", "INSTRUCTOR", "ADMIN"});
        roleBox.setBackground(UITheme.BG_PANEL);
        roleBox.setForeground(UITheme.PRIMARY_DARK);
        roleBox.setFont(UITheme.FONT_BODY);
        roleBox.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_MEDIUM));
        formPanel.add(roleBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        extraFieldsPanel = new JPanel(new GridBagLayout());
        extraFieldsPanel.setOpaque(false);
        formPanel.add(extraFieldsPanel, gbc);
        gbc.gridwidth = 1;

        JButton addButton = styleButton(new JButton("Add User"));
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(addButton, gbc);

        gbc.gridx = 1;
        messageLabel = new JLabel();
        messageLabel.setFont(UITheme.FONT_BODY);
        formPanel.add(messageLabel, gbc);
        
        add(formPanel, BorderLayout.NORTH);

        // Table panel filling center
        tableModel = new DefaultTableModel(new String[]{"ID", "Username", "Role", "Status", "Edit", "Delete"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4 || column == 5;
            }
        };
        userTable = new JTable(tableModel);
        UITheme.styleTable(userTable);
        JScrollPane tableScroll = new JScrollPane(userTable);
        UITheme.styleScrollPane(tableScroll);
        add(tableScroll, BorderLayout.CENTER);

        rollNoField = createTextField(12);
        programField = createTextField(12);
        yearField = createTextField(5);
        studentEmailField = createTextField(18);
        empIdField = createTextField(12);
        deptField = createTextField(12);
        instructorEmailField = createTextField(18);

        roleBox.addActionListener(e -> showExtraFields());
        showExtraFields();

        addButton.addActionListener(e -> addUser());

        // Hover effect for add button
        addButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) { addButton.setBackground(UITheme.PRIMARY_LIGHT); }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) { addButton.setBackground(UITheme.PRIMARY); }
        });

        userTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = userTable.rowAtPoint(evt.getPoint());
                int col = userTable.columnAtPoint(evt.getPoint());
                int userId = (int) tableModel.getValueAt(row, 0);
                String username = (String) tableModel.getValueAt(row, 1);
                String role = (String) tableModel.getValueAt(row, 2);
                if (col == 4) {
                    editUserDialog(userId, role);
                } else if (col == 5) {
                    int confirm = JOptionPane.showConfirmDialog(AddUserPanel.this, 
                        "Delete user " + username + "?", "Confirm", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        ApiResponse response = adminApi.deleteUser(userId, role);
                        if (!response.isSuccess()) {
                            JOptionPane.showMessageDialog(AddUserPanel.this, response.getMessage());
                        }
                        loadUserTable();
                    }
                }
            }
        });

        loadUserTable();
    }

    // UI custom helpers
    private JTextField createTextField(int cols) {
        JTextField tf = new JTextField(cols);
        styleField(tf);
        return tf;
    }
    private void styleField(JTextField field) {
        UITheme.styleTextField(field);
    }
    private JButton styleButton(JButton btn) {
        UITheme.stylePrimaryButton(btn);
        return btn;
    }

    private void addUser() {
        AdminApi.AddUserCommand command = new AdminApi.AddUserCommand();
        command.username = userField.getText().trim();
        command.password = new String(passField.getPassword());
        command.role = (String) roleBox.getSelectedItem();
        command.rollNo = rollNoField.getText().trim();
        command.program = programField.getText().trim();
        command.year = parseInt(yearField);
        command.studentEmail = studentEmailField.getText().trim();
        command.employeeId = empIdField.getText().trim();
        command.department = deptField.getText().trim();
        command.instructorEmail = instructorEmailField.getText().trim();

        ApiResponse response = adminApi.addUser(command);
        messageLabel.setForeground(response.isSuccess() ? UITheme.ACCENT_SUCCESS : UITheme.ACCENT_ERROR);
        messageLabel.setText(response.getMessage());
        if (response.isSuccess()) {
            userField.setText(""); passField.setText("");
            rollNoField.setText(""); programField.setText(""); yearField.setText(""); studentEmailField.setText("");
            empIdField.setText(""); deptField.setText(""); instructorEmailField.setText("");
            loadUserTable();
        }
    }

    private void showExtraFields() {
        extraFieldsPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        String role = (String) roleBox.getSelectedItem();
        if ("ADMIN".equals(role)) {
            JLabel lbl = new JLabel("No additional fields required for Admin.");
            lbl.setFont(UITheme.FONT_BODY);
            lbl.setForeground(new Color(120, 120, 120));
            extraFieldsPanel.add(lbl, gbc);
        } else if ("STUDENT".equals(role)) {
            addFieldWithLabel(extraFieldsPanel, gbc, 0, "Roll No:", rollNoField);
            addFieldWithLabel(extraFieldsPanel, gbc, 1, "Program:", programField);
            addFieldWithLabel(extraFieldsPanel, gbc, 2, "Year:", yearField);
            addFieldWithLabel(extraFieldsPanel, gbc, 3, "Email:", studentEmailField);
        } else {
            addFieldWithLabel(extraFieldsPanel, gbc, 0, "Employee ID:", empIdField);
            addFieldWithLabel(extraFieldsPanel, gbc, 1, "Department:", deptField);
            addFieldWithLabel(extraFieldsPanel, gbc, 2, "Email:", instructorEmailField);
        }
        extraFieldsPanel.revalidate();
        extraFieldsPanel.repaint();
    }
    private void addFieldWithLabel(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_BODY);
        lbl.setForeground(UITheme.PRIMARY_DARK);
        panel.add(lbl, gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void loadUserTable() {
        tableModel.setRowCount(0);
        try {
            List<UserAccount> accounts = adminApi.listUsers();
            for (UserAccount account : accounts) {
                tableModel.addRow(new Object[]{
                    account.getUserId(),
                    account.getUsername(),
                    account.getRole(),
                    account.getStatus(),
                    "Edit",
                    "Delete"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load users: " + ex.getMessage());
        }
    }

    private void editUserDialog(int userId, String role) {
        if ("ADMIN".equals(role)) {
            editAdminDialog(userId);
            return;
        }
        Optional<AdminApi.UserDetailsView> maybeDetails = adminApi.loadUserDetails(userId);
        if (maybeDetails.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Failed to load user details.");
            return;
        }
        AdminApi.UserDetailsView view = maybeDetails.get();

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Edit User", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(550, 550);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(true);
        dialog.getContentPane().setBackground(UITheme.BG_MAIN);
        dialog.setLayout(new BorderLayout());
        
        JPanel scrollContent = new JPanel();
        UITheme.styleCardPanel(scrollContent);
        scrollContent.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField usernameField = createTextField(18);
        usernameField.setText(view.account.getUsername());
        JLabel roleLabel = new JLabel(role);
        roleLabel.setForeground(UITheme.PRIMARY_DARK);
        roleLabel.setFont(UITheme.FONT_BODY_BOLD);
        JPasswordField passField = new JPasswordField(16);
        styleField(passField);

        JTextField field1 = createTextField(15);
        JTextField field2 = createTextField(15);
        JTextField field3 = createTextField(15);
        JTextField field4 = createTextField(18);

        if ("STUDENT".equals(role) && view.studentProfile != null) {
            field1.setText(view.studentProfile.getRollNo());
            field2.setText(view.studentProfile.getProgram());
            field3.setText(String.valueOf(view.studentProfile.getYear()));
            field4.setText(view.studentProfile.getEmail());
        } else if ("INSTRUCTOR".equals(role) && view.instructorProfile != null) {
            field1.setText(view.instructorProfile.getEmployeeId());
            field2.setText(view.instructorProfile.getDepartment());
            field3.setText(view.instructorProfile.getEmail());
        }

        gbc.gridx = 0; gbc.gridy = 0;
        scrollContent.add(createLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        scrollContent.add(usernameField, gbc);
        gbc.weightx = 0;

        gbc.gridx = 0; gbc.gridy = 1;
        scrollContent.add(createLabel("Role:"), gbc);
        gbc.gridx = 1;
        scrollContent.add(roleLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        scrollContent.add(createLabel("New Password:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        scrollContent.add(passField, gbc);
        gbc.weightx = 0;

        gbc.gridx = 0; gbc.gridy = 3;
        scrollContent.add(createLabel("STUDENT".equals(role) ? "Roll No:" : "Employee ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        scrollContent.add(field1, gbc);
        gbc.weightx = 0;

        gbc.gridx = 0; gbc.gridy = 4;
        scrollContent.add(createLabel("STUDENT".equals(role) ? "Program:" : "Department:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        scrollContent.add(field2, gbc);
        gbc.weightx = 0;

        gbc.gridx = 0; gbc.gridy = 5;
        scrollContent.add(createLabel("STUDENT".equals(role) ? "Year:" : "Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        scrollContent.add(field3, gbc);
        gbc.weightx = 0;

        if ("STUDENT".equals(role)) {
            gbc.gridx = 0; gbc.gridy = 6;
            scrollContent.add(createLabel("Email:"), gbc);
            gbc.gridx = 1; gbc.weightx = 1.0;
            scrollContent.add(field4, gbc);
            gbc.weightx = 0;
        }

        JButton saveBtn = styleButton(new JButton("Save Changes"));
        JLabel infoLabel = new JLabel();
        infoLabel.setFont(UITheme.FONT_BODY);
        infoLabel.setForeground(UITheme.PRIMARY_DARK);
        // Hover for saveBtn
        saveBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { saveBtn.setBackground(UITheme.PRIMARY_LIGHT);}
            public void mouseExited(java.awt.event.MouseEvent evt) { saveBtn.setBackground(UITheme.PRIMARY);}
        });

        gbc.gridx = 0;
        gbc.gridy = "STUDENT".equals(role) ? 7 : 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        scrollContent.add(saveBtn, gbc);
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;
        scrollContent.add(infoLabel, gbc);
        
        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(UITheme.BG_PANEL);
        dialog.add(scrollPane, BorderLayout.CENTER);

        saveBtn.addActionListener(e -> {
            AdminApi.UpdateUserCommand command = new AdminApi.UpdateUserCommand();
            command.userId = userId;
            command.username = usernameField.getText().trim();
            command.role = role;
            command.newPassword = new String(passField.getPassword()).trim();

            if ("STUDENT".equals(role)) {
                try {
                    command.studentProfile = new StudentProfile(userId, field1.getText().trim(), field2.getText().trim(), Integer.parseInt(field3.getText().trim()), field4.getText().trim());
                } catch (NumberFormatException ex) {
                    infoLabel.setForeground(UITheme.ACCENT_ERROR);
                    infoLabel.setText("Year must be a number.");
                    return;
                }
            } else if ("INSTRUCTOR".equals(role)) {
                command.instructorProfile = new InstructorProfile(userId, field1.getText().trim(), field2.getText().trim(), field3.getText().trim());
            }

            ApiResponse response = adminApi.updateUser(command);
            infoLabel.setForeground(response.isSuccess() ? UITheme.ACCENT_SUCCESS : UITheme.ACCENT_ERROR);
            infoLabel.setText(response.getMessage());
            if (response.isSuccess()) {
                loadUserTable();
            }
        });
        dialog.setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_BODY);
        lbl.setForeground(UITheme.PRIMARY_DARK);
        return lbl;
    }

    private void editAdminDialog(int userId) {
        Optional<AdminApi.UserDetailsView> maybeDetails = adminApi.loadUserDetails(userId);
        if (maybeDetails.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Failed to load admin details.");
            return;
        }
        AdminApi.UserDetailsView view = maybeDetails.get();

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Edit Admin", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(500, 350);
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
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField usernameField = createTextField(18);
        usernameField.setText(view.account.getUsername());
        JPasswordField passField = new JPasswordField(18);
        styleField(passField);

        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(createLabel("Username:"), gbc); 
        gbc.gridx = 1; gbc.weightx = 1.0;
        contentPanel.add(usernameField, gbc);
        gbc.weightx = 0;

        gbc.gridx = 0; gbc.gridy = 1;
        contentPanel.add(createLabel("New Password:"), gbc); 
        gbc.gridx = 1; gbc.weightx = 1.0;
        contentPanel.add(passField, gbc);
        gbc.weightx = 0;

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel subLabel = new JLabel("(Leave blank to keep current)");
        subLabel.setForeground(UITheme.TEXT_SECONDARY);
        subLabel.setFont(UITheme.FONT_SMALL);
        contentPanel.add(subLabel, gbc);

        JButton saveBtn = styleButton(new JButton("Save Changes"));
        JLabel infoLabel = new JLabel();
        infoLabel.setFont(UITheme.FONT_BODY);
        infoLabel.setForeground(UITheme.PRIMARY_DARK);

        // Hover effect for save button
        saveBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { saveBtn.setBackground(UITheme.PRIMARY_LIGHT);}
            public void mouseExited(java.awt.event.MouseEvent evt) { saveBtn.setBackground(UITheme.PRIMARY);}
        });

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(saveBtn, gbc);
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        contentPanel.add(infoLabel, gbc);
        
        dialog.add(contentPanel, BorderLayout.CENTER);

        saveBtn.addActionListener(e -> {
            AdminApi.UpdateUserCommand command = new AdminApi.UpdateUserCommand();
            command.userId = userId;
            command.username = usernameField.getText().trim();
            command.role = "ADMIN";
            command.newPassword = new String(passField.getPassword()).trim();

            ApiResponse response = adminApi.updateUser(command);
            infoLabel.setForeground(response.isSuccess() ? UITheme.ACCENT_SUCCESS : UITheme.ACCENT_ERROR);
            infoLabel.setText(response.getMessage());
            if (response.isSuccess()) {
                loadUserTable();
            }
        });
        dialog.setVisible(true);
    }

    private Integer parseInt(JTextField field) {
        try {
            String value = field.getText().trim();
            return value.isEmpty() ? null : Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}