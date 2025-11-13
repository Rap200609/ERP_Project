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

    public AddUserPanel() {
        this(new AdminApi());
    }

    public AddUserPanel(AdminApi adminApi) {
        this.adminApi = adminApi;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        userField = new JTextField(15);
        add(userField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passField = new JPasswordField(15);
        add(passField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        roleBox = new JComboBox<>(new String[]{"STUDENT", "INSTRUCTOR", "ADMIN"});
        add(roleBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        extraFieldsPanel = new JPanel(new GridBagLayout());
        add(extraFieldsPanel, gbc);
        gbc.gridwidth = 1;

        JButton addButton = new JButton("Add User");
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(addButton, gbc);
        gbc.gridx = 1;
        messageLabel = new JLabel();
        add(messageLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        tableModel = new DefaultTableModel(new String[]{"ID", "Username", "Role", "Status", "Edit", "Delete"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4 || column == 5;
            }
        };
        userTable = new JTable(tableModel);
        add(new JScrollPane(userTable), gbc);

        rollNoField = new JTextField(12);
        programField = new JTextField(12);
        yearField = new JTextField(5);
        studentEmailField = new JTextField(18);

        empIdField = new JTextField(12);
        deptField = new JTextField(12);
        instructorEmailField = new JTextField(18);

        roleBox.addActionListener(e -> showExtraFields());
        showExtraFields();

        addButton.addActionListener(e -> addUser());

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
                    int confirm = JOptionPane.showConfirmDialog(
                            AddUserPanel.this,
                            "Delete user " + username + "?",
                            "Confirm",
                            JOptionPane.YES_NO_OPTION
                    );
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
        messageLabel.setText(response.getMessage());
        if (response.isSuccess()) {
            userField.setText("");
            passField.setText("");
            rollNoField.setText("");
            programField.setText("");
            yearField.setText("");
            studentEmailField.setText("");
            empIdField.setText("");
            deptField.setText("");
            instructorEmailField.setText("");
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
            extraFieldsPanel.add(new JLabel("No additional fields required for Admin."), gbc);
        } else if ("STUDENT".equals(role)) {
            gbc.gridx = 0;
            gbc.gridy = 0;
            extraFieldsPanel.add(new JLabel("Roll No:"), gbc);
            gbc.gridx = 1;
            extraFieldsPanel.add(rollNoField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 1;
            extraFieldsPanel.add(new JLabel("Program:"), gbc);
            gbc.gridx = 1;
            extraFieldsPanel.add(programField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 2;
            extraFieldsPanel.add(new JLabel("Year:"), gbc);
            gbc.gridx = 1;
            extraFieldsPanel.add(yearField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 3;
            extraFieldsPanel.add(new JLabel("Email:"), gbc);
            gbc.gridx = 1;
            extraFieldsPanel.add(studentEmailField, gbc);
        } else {
            gbc.gridx = 0;
            gbc.gridy = 0;
            extraFieldsPanel.add(new JLabel("Employee ID:"), gbc);
            gbc.gridx = 1;
            extraFieldsPanel.add(empIdField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 1;
            extraFieldsPanel.add(new JLabel("Department:"), gbc);
            gbc.gridx = 1;
            extraFieldsPanel.add(deptField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 2;
            extraFieldsPanel.add(new JLabel("Email:"), gbc);
            gbc.gridx = 1;
            extraFieldsPanel.add(instructorEmailField, gbc);
        }
        extraFieldsPanel.revalidate();
        extraFieldsPanel.repaint();
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
        dialog.setSize(400, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField usernameField = new JTextField(view.account.getUsername(), 18);
        JLabel roleLabel = new JLabel(role);
        JPasswordField passField = new JPasswordField(16);

        JTextField field1 = new JTextField(15);
        JTextField field2 = new JTextField(15);
        JTextField field3 = new JTextField(15);
        JTextField field4 = new JTextField(18);

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

        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        dialog.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        dialog.add(roleLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1;
        dialog.add(passField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        dialog.add(new JLabel("STUDENT".equals(role) ? "Roll No:" : "Employee ID:"), gbc);
        gbc.gridx = 1;
        dialog.add(field1, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        dialog.add(new JLabel("STUDENT".equals(role) ? "Program:" : "Department:"), gbc);
        gbc.gridx = 1;
        dialog.add(field2, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        dialog.add(new JLabel("STUDENT".equals(role) ? "Year:" : "Email:"), gbc);
        gbc.gridx = 1;
        dialog.add(field3, gbc);

        if ("STUDENT".equals(role)) {
            gbc.gridx = 0;
            gbc.gridy = 6;
            dialog.add(new JLabel("Email:"), gbc);
            gbc.gridx = 1;
            dialog.add(field4, gbc);
        }

        JButton saveBtn = new JButton("Save Changes");
        JLabel infoLabel = new JLabel();
        gbc.gridx = 0;
        gbc.gridy = "STUDENT".equals(role) ? 7 : 6;
        gbc.gridwidth = 2;
        dialog.add(saveBtn, gbc);
        gbc.gridy++;
        dialog.add(infoLabel, gbc);

        saveBtn.addActionListener(e -> {
            AdminApi.UpdateUserCommand command = new AdminApi.UpdateUserCommand();
            command.userId = userId;
            command.username = usernameField.getText().trim();
            command.role = role;
            command.newPassword = new String(passField.getPassword()).trim();

            if ("STUDENT".equals(role)) {
                try {
                    command.studentProfile = new StudentProfile(
                            userId,
                            field1.getText().trim(),
                            field2.getText().trim(),
                            Integer.parseInt(field3.getText().trim()),
                            field4.getText().trim()
                    );
                } catch (NumberFormatException ex) {
                    infoLabel.setText("Year must be a number.");
                    return;
                }
            } else if ("INSTRUCTOR".equals(role)) {
                command.instructorProfile = new InstructorProfile(
                        userId,
                        field1.getText().trim(),
                        field2.getText().trim(),
                        field3.getText().trim()
                );
            }

            ApiResponse response = adminApi.updateUser(command);
            infoLabel.setText(response.getMessage());
            if (response.isSuccess()) {
                loadUserTable();
            }
        });

        dialog.setVisible(true);
    }

    private void editAdminDialog(int userId) {
        Optional<AdminApi.UserDetailsView> maybeDetails = adminApi.loadUserDetails(userId);
        if (maybeDetails.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Failed to load admin details.");
            return;
        }
        AdminApi.UserDetailsView view = maybeDetails.get();

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Edit Admin", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField usernameField = new JTextField(view.account.getUsername(), 18);
        JPasswordField passField = new JPasswordField(18);

        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        dialog.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1;
        dialog.add(passField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(new JLabel("(Leave blank to keep current)"), gbc);

        JButton saveBtn = new JButton("Save Changes");
        JLabel infoLabel = new JLabel();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        dialog.add(saveBtn, gbc);
        gbc.gridy = 4;
        dialog.add(infoLabel, gbc);

        saveBtn.addActionListener(e -> {
            AdminApi.UpdateUserCommand command = new AdminApi.UpdateUserCommand();
            command.userId = userId;
            command.username = usernameField.getText().trim();
            command.role = "ADMIN";
            command.newPassword = new String(passField.getPassword()).trim();

            ApiResponse response = adminApi.updateUser(command);
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
