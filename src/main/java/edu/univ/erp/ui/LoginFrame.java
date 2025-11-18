package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;
import edu.univ.erp.auth.AuthService;
import edu.univ.erp.data.DatabaseConfig;

public class LoginFrame extends JFrame {
    public LoginFrame() {
        setTitle("University ERP System - Login");
        setSize(480, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true); // Allow maximizing
        getContentPane().setBackground(UITheme.BG_MAIN);

        // Main container with card effect
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_MAIN);
        mainPanel.setBorder(new javax.swing.border.EmptyBorder(40, 40, 40, 40));
        
        // Login card panel
        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(UITheme.BG_PANEL);
        cardPanel.setBorder(new javax.swing.border.CompoundBorder(
            new javax.swing.border.LineBorder(UITheme.BORDER_LIGHT, 1, true),
            new javax.swing.border.EmptyBorder(40, 40, 40, 40)
        ));
        cardPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        JLabel titleLabel = new JLabel("University ERP System");
        titleLabel.setFont(UITheme.FONT_HEADING);
        titleLabel.setForeground(UITheme.PRIMARY_DARK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        cardPanel.add(titleLabel, gbc);
        
        JLabel subtitleLabel = new JLabel("Sign in to continue");
        subtitleLabel.setFont(UITheme.FONT_SMALL);
        subtitleLabel.setForeground(UITheme.TEXT_SECONDARY);
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 10, 25, 10);
        cardPanel.add(subtitleLabel, gbc);
        
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;

        // Username field
        JLabel userLabel = new JLabel("Username:");
        UITheme.styleLabel(userLabel, true);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cardPanel.add(userLabel, gbc);

        JTextField userField = new JTextField(20);
        UITheme.styleTextField(userField);
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.ipadx = 20;
        cardPanel.add(userField, gbc);

        // Password field
        JLabel passLabel = new JLabel("Password:");
        UITheme.styleLabel(passLabel, true);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.ipadx = 0;
        cardPanel.add(passLabel, gbc);

        JPasswordField passField = new JPasswordField(20);
        UITheme.stylePasswordField(passField);
        gbc.gridx = 1;
        gbc.ipadx = 20;
        cardPanel.add(passField, gbc);

        // Message label
        JLabel messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(UITheme.FONT_SMALL);
        messageLabel.setForeground(UITheme.ACCENT_ERROR);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 0;
        gbc.insets = new Insets(5, 10, 5, 10);
        cardPanel.add(messageLabel, gbc);

        // Login button
        JButton loginButton = new JButton("Sign In");
        UITheme.stylePrimaryButton(loginButton);
        loginButton.setPreferredSize(new Dimension(200, 42));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 10, 10, 10);
        gbc.fill = GridBagConstraints.NONE;
        cardPanel.add(loginButton, gbc);

        mainPanel.add(cardPanel, BorderLayout.CENTER);
        add(mainPanel);

        loginButton.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Please enter both username and password.");
                return;
            }

            AuthService.AuthResult result = AuthService.authenticate(username, password, DatabaseConfig.getAuthDataSource());
            messageLabel.setText(result.message);

            if (result.success) {
                this.setVisible(false);
                switch (result.role.toUpperCase()) {
                    case "ADMIN":
                        SwingUtilities.invokeLater(() -> new AdminDashboard(result.userId).setVisible(true));
                        break;
                    case "INSTRUCTOR":
                        SwingUtilities.invokeLater(() -> new InstructorDashboard(result.userId).setVisible(true));
                        break;
                    case "STUDENT":
                        SwingUtilities.invokeLater(() -> new StudentDashboard(result.userId).setVisible(true));
                        break;
                    default:
                        JOptionPane.showMessageDialog(this, "Unknown role: " + result.role);
                        this.setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, result.message, "Login Failed", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
