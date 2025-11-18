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

    // ----- Begin color and font palette -----
    private static final Color BG_COLOR = new Color(245,247,250);
    private static final Color HEADER_BG = new Color(32, 56, 100);
    private static final Color HEADER_FG = Color.WHITE;
    private static final Color LABEL_COLOR = new Color(32, 56, 100);
    private static final Color FIELD_BG = new Color(236, 240, 249);
    private static final Color FIELD_BORDER = new Color(115, 143, 188);
    private static final Color BTN_BG = new Color(115,143,188);
    private static final Color BTN_BG_HOVER = new Color(93,120,165);
    private static final Color BTN_FG = Color.WHITE;
    private static final Color SUCCESS = new Color(0,128,128);
    private static final Color ERROR = new Color(183,28,28);

    private static final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 13);
    private static final Font FIELD_FONT = new Font("SansSerif", Font.PLAIN, 14);
    // ----- End color and font palette -----

    public AddUserPanel() {
        this(new AdminApi());
    }

    public AddUserPanel(AdminApi adminApi) {
        this.adminApi = adminApi;
        setBackground(BG_COLOR);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(LABEL_COLOR);
        userLabel.setFont(LABEL_FONT);
        add(userLabel, gbc);

        gbc.gridx = 1;
        userField = createTextField(15);
        add(userField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(LABEL_COLOR);
        passLabel.setFont(LABEL_FONT);
        add(passLabel, gbc);

        gbc.gridx = 1;
        passField = new JPasswordField(15);
        styleField(passField);
        add(passField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setForeground(LABEL_COLOR);
        roleLabel.setFont(LABEL_FONT);
        add(roleLabel, gbc);

        gbc.gridx = 1;
        roleBox = new JComboBox<>(new String[]{"STUDENT", "INSTRUCTOR", "ADMIN"});
        roleBox.setBackground(FIELD_BG);
        roleBox.setForeground(LABEL_COLOR);
        roleBox.setFont(FIELD_FONT);
        roleBox.setBorder(BorderFactory.createLineBorder(FIELD_BORDER));
        add(roleBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        extraFieldsPanel = new JPanel(new GridBagLayout());
        extraFieldsPanel.setOpaque(false);
        add(extraFieldsPanel, gbc);
        gbc.gridwidth = 1;

        JButton addButton = styleButton(new JButton("Add User"));
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(addButton, gbc);

        gbc.gridx = 1;
        messageLabel = new JLabel();
        messageLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
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
        userTable.setRowHeight(26);
        userTable.setFont(FIELD_FONT);
        userTable.setSelectionBackground(new Color(200,215,240));
        userTable.setSelectionForeground(LABEL_COLOR);
        userTable.setBackground(BG_COLOR);
        userTable.getTableHeader().setBackground(HEADER_BG);
        userTable.getTableHeader().setForeground(HEADER_FG);
        userTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        JScrollPane tableScroll = new JScrollPane(userTable);
        tableScroll.getViewport().setBackground(BG_COLOR);
        add(tableScroll, gbc);

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
            public void mouseEntered(java.awt.event.MouseEvent evt) { addButton.setBackground(BTN_BG_HOVER); }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) { addButton.setBackground(BTN_BG); }
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

    // ---- UI custom helpers ----
    private JTextField createTextField(int cols) {
        JTextField tf = new JTextField(cols);
        styleField(tf);
        return tf;
    }
    private void styleField(JTextField field) {
        field.setBackground(FIELD_BG);
        field.setForeground(LABEL_COLOR);
        field.setBorder(BorderFactory.createLineBorder(FIELD_BORDER));
        field.setFont(FIELD_FONT);
        field.setCaretColor(new Color(70, 104, 134));
    }
    private JButton styleButton(JButton btn) {
        btn.setBackground(BTN_BG);
        btn.setForeground(BTN_FG);
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));
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
        messageLabel.setForeground(response.isSuccess() ? SUCCESS : ERROR);
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
            lbl.setFont(FIELD_FONT);
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
        lbl.setFont(FIELD_FONT);
        lbl.setForeground(LABEL_COLOR);
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
        dialog.setSize(400, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        dialog.getContentPane().setBackground(BG_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField usernameField = createTextField(18);
        usernameField.setText(view.account.getUsername());
        JLabel roleLabel = new JLabel(role);
        roleLabel.setForeground(LABEL_COLOR);
        roleLabel.setFont(LABEL_FONT);
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
        dialog.add(createLabel("Username:"), gbc);
        gbc.gridx = 1; dialog.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(createLabel("Role:"), gbc);
        gbc.gridx = 1; dialog.add(roleLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(createLabel("New Password:"), gbc);
        gbc.gridx = 1; dialog.add(passField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(createLabel("STUDENT".equals(role) ? "Roll No:" : "Employee ID:"), gbc);
        gbc.gridx = 1; dialog.add(field1, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        dialog.add(createLabel("STUDENT".equals(role) ? "Program:" : "Department:"), gbc);
        gbc.gridx = 1; dialog.add(field2, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        dialog.add(createLabel("STUDENT".equals(role) ? "Year:" : "Email:"), gbc);
        gbc.gridx = 1; dialog.add(field3, gbc);

        if ("STUDENT".equals(role)) {
            gbc.gridx = 0; gbc.gridy = 6;
            dialog.add(createLabel("Email:"), gbc);
            gbc.gridx = 1; dialog.add(field4, gbc);
        }

        JButton saveBtn = styleButton(new JButton("Save Changes"));
        JLabel infoLabel = new JLabel();
        infoLabel.setFont(FIELD_FONT);
        infoLabel.setForeground(LABEL_COLOR);
        // Hover for saveBtn
        saveBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { saveBtn.setBackground(BTN_BG_HOVER);}
            public void mouseExited(java.awt.event.MouseEvent evt) { saveBtn.setBackground(BTN_BG);}
        });

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
                    command.studentProfile = new StudentProfile(userId, field1.getText().trim(), field2.getText().trim(), Integer.parseInt(field3.getText().trim()), field4.getText().trim());
                } catch (NumberFormatException ex) {
                    infoLabel.setForeground(ERROR);
                    infoLabel.setText("Year must be a number.");
                    return;
                }
            } else if ("INSTRUCTOR".equals(role)) {
                command.instructorProfile = new InstructorProfile(userId, field1.getText().trim(), field2.getText().trim(), field3.getText().trim());
            }

            ApiResponse response = adminApi.updateUser(command);
            infoLabel.setForeground(response.isSuccess() ? SUCCESS : ERROR);
            infoLabel.setText(response.getMessage());
            if (response.isSuccess()) {
                loadUserTable();
            }
        });
        dialog.setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FIELD_FONT);
        lbl.setForeground(LABEL_COLOR);
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
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        dialog.getContentPane().setBackground(BG_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField usernameField = createTextField(18);
        usernameField.setText(view.account.getUsername());
        JPasswordField passField = new JPasswordField(18);
        styleField(passField);

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(createLabel("Username:"), gbc); gbc.gridx = 1;
        dialog.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(createLabel("New Password:"), gbc); gbc.gridx = 1;
        dialog.add(passField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel subLabel = new JLabel("(Leave blank to keep current)");
        subLabel.setForeground(new Color(120,120,120));
        subLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        dialog.add(subLabel, gbc);

        JButton saveBtn = styleButton(new JButton("Save Changes"));
        JLabel infoLabel = new JLabel();
        infoLabel.setFont(FIELD_FONT);
        infoLabel.setForeground(LABEL_COLOR);

        // Hover effect for save button
        saveBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { saveBtn.setBackground(BTN_BG_HOVER);}
            public void mouseExited(java.awt.event.MouseEvent evt) { saveBtn.setBackground(BTN_BG);}
        });

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
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
            infoLabel.setForeground(response.isSuccess() ? SUCCESS : ERROR);
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