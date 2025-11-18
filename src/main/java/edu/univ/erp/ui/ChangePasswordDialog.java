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

        setSize(550, 380);
        setLocationRelativeTo(parent);
        setResizable(true);
        getContentPane().setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout());

        // Main panel with card styling
        JPanel mainPanel = new JPanel();
        UITheme.styleCardPanel(mainPanel);
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Current Password
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0;
        JLabel currentLabel = new JLabel("Current Password:");
        UITheme.styleLabel(currentLabel, true);
        mainPanel.add(currentLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.ipadx = 10;
        currentPasswordField = new JPasswordField(25);
        UITheme.stylePasswordField(currentPasswordField);
        currentPasswordField.setPreferredSize(new Dimension(300, 40));
        mainPanel.add(currentPasswordField, gbc);

        // New Password
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.ipadx = 0;
        JLabel newLabel = new JLabel("New Password:");
        UITheme.styleLabel(newLabel, true);
        mainPanel.add(newLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.ipadx = 10;
        newPasswordField = new JPasswordField(25);
        UITheme.stylePasswordField(newPasswordField);
        newPasswordField.setPreferredSize(new Dimension(300, 40));
        mainPanel.add(newPasswordField, gbc);

        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.ipadx = 0;
        JLabel confirmLabel = new JLabel("Confirm Password:");
        UITheme.styleLabel(confirmLabel, true);
        mainPanel.add(confirmLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.ipadx = 10;
        confirmPasswordField = new JPasswordField(25);
        UITheme.stylePasswordField(confirmPasswordField);
        confirmPasswordField.setPreferredSize(new Dimension(300, 40));
        mainPanel.add(confirmPasswordField, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(UITheme.BG_MAIN);
        buttonPanel.setBorder(new javax.swing.border.EmptyBorder(10, 20, 15, 20));
        JButton changeBtn = new JButton("Change Password");
        UITheme.stylePrimaryButton(changeBtn);
        JButton cancelBtn = new JButton("Cancel");
        UITheme.styleSecondaryButton(cancelBtn);

        changeBtn.addActionListener(e -> changePassword());
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(changeBtn);
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
