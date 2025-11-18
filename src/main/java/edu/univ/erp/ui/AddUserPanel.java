package edu.univ.erp.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import edu.univ.erp.data.DatabaseConfig;
import org.mindrot.jbcrypt.BCrypt;

public class AddUserPanel extends JPanel {
    private JTextField userField, rollNoField, programField, yearField, studentEmailField;
    private JPasswordField passField;
    private JTextField empIdField, deptField, instructorEmailField;
    private JComboBox<String> roleBox;
    private JLabel messageLabel;
    private DefaultTableModel tableModel;
    private JTable userTable;
    private JPanel extraFieldsPanel;

    public AddUserPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Username row
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        userField = new JTextField(15);
        add(userField, gbc);

        // Password row
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passField = new JPasswordField(15);
        add(passField, gbc);

        // Role row - NOW WITH ADMIN!
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        roleBox = new JComboBox<>(new String[]{"STUDENT", "INSTRUCTOR", "ADMIN"});  // ← ADDED ADMIN
        add(roleBox, gbc);

        // Extra fields Panel (Student/Instructor info)
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        extraFieldsPanel = new JPanel(new GridBagLayout());
        add(extraFieldsPanel, gbc);
        gbc.gridwidth = 1;

        // Button row
        gbc.gridx = 0; gbc.gridy = 4;
        JButton addButton = new JButton("Add User");
        add(addButton, gbc);
        gbc.gridx = 1;
        messageLabel = new JLabel();
        add(messageLabel, gbc);

        // User table section
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.BOTH;
        String[] columns = {"ID", "Username", "Role", "Status", "Edit", "Delete"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return col == 4 || col == 5;
            }
        };
        userTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);
        add(scrollPane, gbc);

        loadUserTable();

        // Student fields
        rollNoField = new JTextField(12);
        programField = new JTextField(12);
        yearField = new JTextField(5);
        studentEmailField = new JTextField(18);

        // Instructor fields
        empIdField = new JTextField(12);
        deptField = new JTextField(12);
        instructorEmailField = new JTextField(18);

        // Initial display
        showExtraFields();

        // Handle role change
        roleBox.addActionListener(e -> showExtraFields());

        // Add user logic
        addButton.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());
            String role = (String) roleBox.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Username and password required.");
                return;
            }

            int userId = -1;
            String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt(10));
            String sql = "INSERT INTO users_auth (username, role, password_hash) VALUES (?, ?, ?)";
            try (
                Connection connAuth = DatabaseConfig.getAuthDataSource().getConnection();
                PreparedStatement stmt = connAuth.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            ) {
                stmt.setString(1, username);
                stmt.setString(2, role);
                stmt.setString(3, passwordHash);
                stmt.executeUpdate();

                // Get user_id
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) userId = keys.getInt(1);

                // ← ADMIN HANDLING: No extra fields needed
                if ("ADMIN".equals(role)) {
                    // Admin only needs entry in users_auth, nothing else
                    messageLabel.setText("Admin user added successfully!");
                    userField.setText(""); 
                    passField.setText("");
                    loadUserTable();
                    return;
                }

                // Now insert into erp_main for role-specific info
                if ("STUDENT".equals(role)) {
                    String rollNo = rollNoField.getText().trim();
                    String program = programField.getText().trim();
                    Integer year = parseInt(yearField);
                    String email = studentEmailField.getText().trim();
                    if (rollNo.isEmpty() || program.isEmpty() || year == null) {
                        messageLabel.setText("All student fields required.");
                        return;
                    }
                    try (
                        Connection connMain = DatabaseConfig.getMainDataSource().getConnection();
                        PreparedStatement studentStmt = connMain.prepareStatement(
                            "INSERT INTO students (user_id, roll_no, program, year, email) VALUES (?, ?, ?, ?, ?)")
                    ) {
                        studentStmt.setInt(1, userId);
                        studentStmt.setString(2, rollNo);
                        studentStmt.setString(3, program);
                        studentStmt.setInt(4, year);
                        studentStmt.setString(5, email);
                        studentStmt.executeUpdate();
                    }
                }
                if ("INSTRUCTOR".equals(role)) {
                    String empId = empIdField.getText().trim();
                    String dept = deptField.getText().trim();
                    String email = instructorEmailField.getText().trim();
                    if (empId.isEmpty() || dept.isEmpty()) {
                        messageLabel.setText("All instructor fields required.");
                        return;
                    }
                    try (
                        Connection connMain = DatabaseConfig.getMainDataSource().getConnection();
                        PreparedStatement instrStmt = connMain.prepareStatement(
                            "INSERT INTO instructors (user_id, employee_id, department, email) VALUES (?, ?, ?, ?)")
                    ) 
                    {
                        instrStmt.setInt(1, userId);
                        instrStmt.setString(2, empId);
                        instrStmt.setString(3, dept);
                        instrStmt.setString(4, email);
                        instrStmt.executeUpdate();
                    }
                }

                messageLabel.setText("User added successfully!");
                userField.setText(""); passField.setText("");
                rollNoField.setText(""); programField.setText(""); yearField.setText(""); studentEmailField.setText("");
                empIdField.setText(""); deptField.setText(""); instructorEmailField.setText("");
                loadUserTable();
            } 
            catch (Exception ex) {
                messageLabel.setText("Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Table mouse click logic for Edit/Delete
        userTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = userTable.rowAtPoint(evt.getPoint());
                int col = userTable.columnAtPoint(evt.getPoint());
                int userId = (int) tableModel.getValueAt(row, 0);
                String username = (String) tableModel.getValueAt(row, 1);
                String role = (String) tableModel.getValueAt(row, 2);
                if (col == 4) { // Edit
                    editUserDialog(userId, role);
                } 
                else if (col == 5) { // Delete
                    int confirm = JOptionPane.showConfirmDialog(AddUserPanel.this,
                        "Delete user " + username + "?");
                    if (confirm == JOptionPane.YES_OPTION) {
                        deleteUser(userId, role);
                    }
                }
            }
        });
    }

    // Helper to set extra fields panel based on role
    private void showExtraFields() {
        extraFieldsPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4); gbc.anchor = GridBagConstraints.WEST;

        String role = (String) roleBox.getSelectedItem();
        
        // ← ADMIN HANDLING: No extra fields
        if ("ADMIN".equals(role)) {
            // No extra fields needed for admin
            extraFieldsPanel.add(new JLabel("No additional fields required for Admin."), gbc);
        } 
        else if ("STUDENT".equals(role)) {
            gbc.gridx = 0; gbc.gridy = 0; extraFieldsPanel.add(new JLabel("Roll No:"), gbc);
            gbc.gridx = 1; extraFieldsPanel.add(rollNoField, gbc);
            gbc.gridx = 0; gbc.gridy = 1; extraFieldsPanel.add(new JLabel("Program:"), gbc);
            gbc.gridx = 1; extraFieldsPanel.add(programField, gbc);
            gbc.gridx = 0; gbc.gridy = 2; extraFieldsPanel.add(new JLabel("Year:"), gbc);
            gbc.gridx = 1; extraFieldsPanel.add(yearField, gbc);
            gbc.gridx = 0; gbc.gridy = 3; extraFieldsPanel.add(new JLabel("Email:"), gbc);
            gbc.gridx = 1; extraFieldsPanel.add(studentEmailField, gbc);
        } 
        else if ("INSTRUCTOR".equals(role)) {
            gbc.gridx = 0; gbc.gridy = 0; extraFieldsPanel.add(new JLabel("Employee ID:"), gbc);
            gbc.gridx = 1; extraFieldsPanel.add(empIdField, gbc);
            gbc.gridx = 0; gbc.gridy = 1; extraFieldsPanel.add(new JLabel("Department:"), gbc);
            gbc.gridx = 1; extraFieldsPanel.add(deptField, gbc);
            gbc.gridx = 0; gbc.gridy = 2; extraFieldsPanel.add(new JLabel("Email:"), gbc);
            gbc.gridx = 1; extraFieldsPanel.add(instructorEmailField, gbc);
        }
        extraFieldsPanel.revalidate();
        extraFieldsPanel.repaint();
    }

    private void loadUserTable() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConfig.getAuthDataSource().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                "SELECT user_id, username, role, status FROM users_auth")) {
            while (rs.next()) {
                tableModel.addRow(new Object[] {
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("role"),
                    rs.getString("status"),
                    "Edit",
                    "Delete"
                });
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteUser(int userId, String role) {
        try {
            // Delete from users_auth
            try (Connection connAuth = DatabaseConfig.getAuthDataSource().getConnection()) {
                PreparedStatement stmt = connAuth.prepareStatement(
                    "DELETE FROM users_auth WHERE user_id = ?");
                stmt.setInt(1, userId);
                stmt.executeUpdate();
            }
            
            // ← ADMIN HANDLING: No extra tables to delete from
            if ("ADMIN".equals(role)) {
                // Admin only exists in users_auth
                loadUserTable();
                return;
            }
            
            // Delete from students/instructors in erp_main
            try (Connection connMain = DatabaseConfig.getMainDataSource().getConnection()) {
                if ("STUDENT".equals(role)) {
                    PreparedStatement delStu = connMain.prepareStatement(
                        "DELETE FROM students WHERE user_id = ?");
                    delStu.setInt(1, userId);
                    delStu.executeUpdate();
                    delStu.close();
                }
                if ("INSTRUCTOR".equals(role)) {
                    PreparedStatement delInstr = connMain.prepareStatement(
                        "DELETE FROM instructors WHERE user_id = ?");
                    delInstr.setInt(1, userId);
                    delInstr.executeUpdate();
                    delInstr.close();
                }
            }
            loadUserTable();
        } 
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting user: " + e.getMessage());
        }
    }

    private void editUserDialog(int userId, String role) {
        // ← ADMIN HANDLING: Simplified edit dialog
        if ("ADMIN".equals(role)) {
            editAdminDialog(userId);
            return;
        }
        
        // Rest of your existing edit code for STUDENT/INSTRUCTOR...
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Edit User", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(400, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); gbc.anchor = GridBagConstraints.WEST;

        JTextField usernameField = new JTextField(18);
        JLabel roleLabel = new JLabel(role);
        JPasswordField passField = new JPasswordField(16);

        JTextField field1 = new JTextField(15);
        JTextField field2 = new JTextField(15);
        JTextField field3 = new JTextField(15);
        JTextField field4 = new JTextField(18);

        // Load user info
        try (Connection conn = DatabaseConfig.getAuthDataSource().getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT username FROM users_auth WHERE user_id=?")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                usernameField.setText(rs.getString("username"));
            }
        } 
        catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading user: " + ex.getMessage());
            return;
        }

        if ("STUDENT".equals(role)) {
            try (Connection conn = DatabaseConfig.getMainDataSource().getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                    "SELECT roll_no, program, year, email FROM students WHERE user_id=?")) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    field1.setText(rs.getString("roll_no"));
                    field2.setText(rs.getString("program"));
                    field3.setText(rs.getString("year"));
                    field4.setText(rs.getString("email"));
                }
            } 
            catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading student info: " + ex.getMessage());
                return;
            }
        } 
        else if ("INSTRUCTOR".equals(role)) {
            try (Connection conn = DatabaseConfig.getMainDataSource().getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                    "SELECT employee_id, department, email FROM instructors WHERE user_id=?")) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    field1.setText(rs.getString("employee_id"));
                    field2.setText(rs.getString("department"));
                    field3.setText(rs.getString("email"));
                }
            } 
            catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading instructor info: " + ex.getMessage());
                return;
            }
        }

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; dialog.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1; dialog.add(roleLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1; dialog.add(passField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        if ("STUDENT".equals(role)) dialog.add(new JLabel("Roll No:"), gbc);
        else dialog.add(new JLabel("Employee ID:"), gbc);
        gbc.gridx = 1; dialog.add(field1, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        if ("STUDENT".equals(role)) dialog.add(new JLabel("Program:"), gbc);
        else dialog.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1; dialog.add(field2, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        if ("STUDENT".equals(role)) dialog.add(new JLabel("Year:"), gbc);
        else dialog.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; dialog.add(field3, gbc);

        if ("STUDENT".equals(role)) {
            gbc.gridx = 0; gbc.gridy = 6;
            dialog.add(new JLabel("Email:"), gbc);
            gbc.gridx = 1;
            dialog.add(field4, gbc);
        }

        JButton saveBtn = new JButton("Save Changes");
        JLabel infoLabel = new JLabel();
        gbc.gridx = 0; gbc.gridy = ("STUDENT".equals(role) ? 7 : 6); gbc.gridwidth = 2;
        dialog.add(saveBtn, gbc);
        gbc.gridy++;
        dialog.add(infoLabel, gbc);

        saveBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String newPass = new String(passField.getPassword());

            try (Connection conn = DatabaseConfig.getAuthDataSource().getConnection()) {
                if (newPass.isEmpty()) {
                    PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE users_auth SET username=? WHERE user_id=?");
                    stmt.setString(1, username);
                    stmt.setInt(2, userId);
                    stmt.executeUpdate();
                } 
                else {
                    String hash = BCrypt.hashpw(newPass, BCrypt.gensalt(10));
                    PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE users_auth SET username=?, password_hash=? WHERE user_id=?");
                    stmt.setString(1, username);
                    stmt.setString(2, hash);
                    stmt.setInt(3, userId);
                    stmt.executeUpdate();
                }
            } 
            catch (Exception ex) {
                infoLabel.setText("Error updating user: " + ex.getMessage());
                return;
            }

            if ("STUDENT".equals(role)) {
                String rollNo = field1.getText().trim();
                String program = field2.getText().trim();
                String yearStr = field3.getText().trim();
                String email = field4.getText().trim();
                try (Connection conn = DatabaseConfig.getMainDataSource().getConnection();
                    PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE students SET roll_no=?, program=?, year=?, email=? WHERE user_id=?")) {
                    stmt.setString(1, rollNo);
                    stmt.setString(2, program);
                    stmt.setInt(3, Integer.parseInt(yearStr));
                    stmt.setString(4, email);
                    stmt.setInt(5, userId);
                    stmt.executeUpdate();
                } 
                catch (Exception ex) {
                    infoLabel.setText("Error updating student: " + ex.getMessage());
                    return;
                }
            } else if ("INSTRUCTOR".equals(role)) {
                String empId = field1.getText().trim();
                String dept = field2.getText().trim();
                String email = field3.getText().trim();
                try (Connection conn = DatabaseConfig.getMainDataSource().getConnection();
                    PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE instructors SET employee_id=?, department=?, email=? WHERE user_id=?")) {
                    stmt.setString(1, empId);
                    stmt.setString(2, dept);
                    stmt.setString(3, email);
                    stmt.setInt(4, userId);
                    stmt.executeUpdate();
                } 
                catch (Exception ex) {
                    infoLabel.setText("Error updating instructor: " + ex.getMessage());
                    return;
                }
            }

            infoLabel.setText("Updated successfully!");
            loadUserTable();
        });

        dialog.setVisible(true);
    }

    // ← NEW METHOD: Simplified edit dialog for ADMIN
    private void editAdminDialog(int userId) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Edit Admin", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField usernameField = new JTextField(18);
        JPasswordField passField = new JPasswordField(18);

        // Load admin info
        try (Connection conn = DatabaseConfig.getAuthDataSource().getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT username FROM users_auth WHERE user_id=?")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                usernameField.setText(rs.getString("username"));
            }
        } 
        catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading admin: " + ex.getMessage());
            return;
        }

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        dialog.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1;
        dialog.add(passField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("(Leave blank to keep current)"), gbc);

        JButton saveBtn = new JButton("Save Changes");
        JLabel infoLabel = new JLabel();
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        dialog.add(saveBtn, gbc);
        gbc.gridy = 4;
        dialog.add(infoLabel, gbc);

        saveBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String newPass = new String(passField.getPassword());

            try (Connection conn = DatabaseConfig.getAuthDataSource().getConnection()) {
                if (newPass.isEmpty()) {
                    PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE users_auth SET username=? WHERE user_id=?");
                    stmt.setString(1, username);
                    stmt.setInt(2, userId);
                    stmt.executeUpdate();
                } else {
                    String hash = BCrypt.hashpw(newPass, BCrypt.gensalt(10));
                    PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE users_auth SET username=?, password_hash=? WHERE user_id=?");
                    stmt.setString(1, username);
                    stmt.setString(2, hash);
                    stmt.setInt(3, userId);
                    stmt.executeUpdate();
                }
                infoLabel.setText("Updated successfully!");
                loadUserTable();
            } catch (Exception ex) {
                infoLabel.setText("Error: " + ex.getMessage());
            }
        });

        dialog.setVisible(true);
    }

    private Integer parseInt(JTextField field) {
        try {
            return Integer.parseInt(field.getText().trim());
        } 
        catch (Exception ex) { return null; }
    }
}
