package edu.univ.erp.ui;

import edu.univ.erp.auth.AuthService;
import edu.univ.erp.data.DatabaseConfig;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final JTextField userField = new JTextField(20);
    private final JPasswordField passField = new JPasswordField(20);
    private final JLabel messageLabel = new JLabel(" ");

    public LoginFrame() {
        setTitle("University ERP");
        setSize(900, 600); // Larger default size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        // Main Layout: Split Screen (Left: Graphic, Right: Form)
        setLayout(new GridLayout(1, 2));

        // LEFT SIDE: Branding
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(UITheme.PRIMARY);
        
        JLabel brandTitle = new JLabel("University ERP");
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        brandTitle.setForeground(UITheme.TEXT_WHITE);
        
        JLabel brandSub = new JLabel("Student & Faculty Management System");
        brandSub.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        brandSub.setForeground(new Color(255, 255, 255, 200));

        GridBagConstraints gbcLeft = new GridBagConstraints();
        gbcLeft.gridx = 0; gbcLeft.gridy = 0;
        leftPanel.add(brandTitle, gbcLeft);
        gbcLeft.gridy = 1;
        gbcLeft.insets = new Insets(10, 0, 0, 0);
        leftPanel.add(brandSub, gbcLeft);
        
        add(leftPanel);

        // RIGHT SIDE: Login Form
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(UITheme.BG_PANEL);

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(UITheme.BG_PANEL);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL;

        // Header
        JLabel loginTitle = new JLabel("Welcome Back");
        loginTitle.setFont(UITheme.FONT_HEADING);
        loginTitle.setForeground(UITheme.PRIMARY_DARK);
        loginTitle.setHorizontalAlignment(SwingConstants.CENTER);
        formCard.add(loginTitle, gbc);

        // Username
        gbc.gridy++;
        JLabel userLabel = new JLabel("Username");
        UITheme.styleLabel(userLabel, true);
        formCard.add(userLabel, gbc);
        
        gbc.gridy++;
        UITheme.styleTextField(userField);
        formCard.add(userField, gbc);

        // Password
        gbc.gridy++;
        JLabel passLabel = new JLabel("Password");
        UITheme.styleLabel(passLabel, true);
        formCard.add(passLabel, gbc);
        
        gbc.gridy++;
        UITheme.stylePasswordField(passField);
        formCard.add(passField, gbc);

        // Button
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 10, 0);
        JButton loginBtn = new JButton("Sign In");
        UITheme.stylePrimaryButton(loginBtn);
        formCard.add(loginBtn, gbc);

        // Message
        gbc.gridy++;
        messageLabel.setForeground(UITheme.ACCENT_ERROR);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formCard.add(messageLabel, gbc);

        rightPanel.add(formCard);
        add(rightPanel);
        
        // Add Key Listener for "Enter" key
        getRootPane().setDefaultButton(loginBtn);

        // Login Logic
        loginBtn.addActionListener(e -> {
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword());

            if (user.isEmpty() || pass.isEmpty()) {
                messageLabel.setText("Please enter both username and password.");
                return;
            }

            AuthService.AuthResult result = AuthService.authenticate(user, pass, DatabaseConfig.getAuthDataSource());

            if (result.success) {
                // Show success dialog
                String roleDisplay = result.role.substring(0, 1).toUpperCase() + result.role.substring(1).toLowerCase();
                JOptionPane.showMessageDialog(
                    this,
                    "Welcome, " + user + "!\nLogin successful as " + roleDisplay + ".",
                    "Login Successful",
                    JOptionPane.INFORMATION_MESSAGE
                );
                
                this.dispose();
                // Open specific dashboard based on role
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
                }
            } else {
                messageLabel.setText(result.message);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
