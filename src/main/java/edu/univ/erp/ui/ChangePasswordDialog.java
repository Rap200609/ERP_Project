package edu.univ.erp.ui;

import edu.univ.erp.api.auth.PasswordApi;
import edu.univ.erp.api.common.ApiResponse;

import javax.swing.*;
import java.awt.*;

public class ChangePasswordDialog extends JDialog {
    private final PasswordApi passwordApi = new PasswordApi();
    private final JPasswordField currentPasswordField;
    private final JPasswordField newPasswordField;
    private final JPasswordField confirmPasswordField;
    private final int userId;

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
        JButton changeBtn = new JButton("Change Password");
        JButton cancelBtn = new JButton("Cancel");

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

        ApiResponse response = passwordApi.changePassword(userId, currentPassword, newPassword);
        if (response.isSuccess()) {
            JOptionPane.showMessageDialog(this,
                    response.getMessage(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    response.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
