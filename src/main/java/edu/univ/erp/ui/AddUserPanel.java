package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;
import edu.univ.erp.data.DatabaseConfig;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class AddUserPanel extends JPanel {
    private JTextField userField;
    private JPasswordField passField;
    private JComboBox<String> roleBox;
    private JLabel messageLabel;

    public AddUserPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Username row
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        userField = new JTextField(15);
        add(userField, gbc);

        // Password row
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passField = new JPasswordField(15);
        add(passField, gbc);

        // Role row
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Role:"), gbc);

        gbc.gridx = 1;
        roleBox = new JComboBox<String>(new String[]{"STUDENT", "INSTRUCTOR"});
        add(roleBox, gbc);

        // Button and message row
        gbc.gridx = 0;
        gbc.gridy = 3;
        JButton addButton = new JButton("Add User");
        add(addButton, gbc);

        gbc.gridx = 1;
        messageLabel = new JLabel();
        add(messageLabel, gbc);

        // Add Button Logic
        addButton.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());
            String role = (String) roleBox.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Username and password required.");
                return;
            }

            String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt(10));
            String sql = "INSERT INTO users_auth (username, role, password_hash) VALUES (?, ?, ?)";
            try (Connection conn = DatabaseConfig.getAuthDataSource().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, role);
                stmt.setString(3, passwordHash);
                stmt.executeUpdate();
                messageLabel.setText("User added successfully!");
                userField.setText("");
                passField.setText("");
            } catch (Exception ex) {
                messageLabel.setText("Error: " + ex.getMessage());
            }
        });
    }
}
