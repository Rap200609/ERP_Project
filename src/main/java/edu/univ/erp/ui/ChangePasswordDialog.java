package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class ChangePasswordDialog extends JDialog {
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton changeBtn;
    private JButton cancelBtn;
    private int userId;

    public ChangePasswordDialog(JFrame parent, int userId) {
        super(parent, "Change Password", true);
        this.userId = userId;
        
        setSize(400, 250);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // Main panel
        JPanel mainPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Current Password
        mainPanel.add(new JLabel("Current Password:"));
        currentPasswordField = new JPasswordField();
        mainPanel.add(currentPasswordField);

        // New Password
        mainPanel.add(new JLabel("New Password:"));
        newPasswordField = new JPasswordField();
        mainPanel.add(newPasswordField);

        // Confirm Password
        mainPanel.add(new JLabel("Confirm Password:"));
        confirmPasswordField = new JPasswordField();
        mainPanel.add(confirmPasswordField);

        add(mainPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        changeBtn = new JButton("Change Password");
        cancelBtn = new JButton("Cancel");

        changeBtn.addActionListener(e -> changePassword());
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(changeBtn);
        buttonPanel.add(cancelBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void changePassword() {
        String currentPassword = new String(currentPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validation
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "All fields are required!", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (newPassword.length() < 6) {
            JOptionPane.showMessageDialog(this, 
                "New password must be at least 6 characters!", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, 
                "New passwords do not match!", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Database update
        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getAuthDataSource().getConnection()) {
            
            // Verify current password
            PreparedStatement checkStmt = conn.prepareStatement(
                "SELECT password_hash FROM users_auth WHERE user_id = ?");
            checkStmt.setInt(1, userId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, 
                    "User not found!", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            String storedHash = rs.getString("password_hash");
            
            // Verify current password with BCrypt
            if (!BCrypt.checkpw(currentPassword, storedHash)) {
                JOptionPane.showMessageDialog(this, 
                    "Current password is incorrect!", 
                    "Authentication Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Hash new password
            String newHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());

            // Update password
            PreparedStatement updateStmt = conn.prepareStatement(
                "UPDATE users_auth SET password_hash = ? WHERE user_id = ?");
            updateStmt.setString(1, newHash);
            updateStmt.setInt(2, userId);
            updateStmt.executeUpdate();

            JOptionPane.showMessageDialog(this, 
                "Password changed successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error changing password: " + ex.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
