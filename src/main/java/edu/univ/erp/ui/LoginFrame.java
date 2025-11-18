package edu.univ.erp.ui;

import edu.univ.erp.auth.AuthService;
import edu.univ.erp.data.DatabaseConfig;
import edu.univ.erp.ui.theme.AppColors;
import edu.univ.erp.ui.theme.UIStyles;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
        UIStyles.initFrame(this);

        // Main Layout: Split Screen (Left: Graphic, Right: Form)
        setLayout(new GridLayout(1, 2));

        // LEFT SIDE: Branding
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(AppColors.PRIMARY);
        
        JLabel brandTitle = new JLabel("University ERP");
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        brandTitle.setForeground(Color.WHITE);
        
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
        rightPanel.setBackground(Color.WHITE);

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL;

        // Header
        JLabel loginTitle = UIStyles.headerLabel("Welcome Back");
        loginTitle.setHorizontalAlignment(SwingConstants.CENTER);
        formCard.add(loginTitle, gbc);

        // Username
        gbc.gridy++;
        formCard.add(new JLabel("Username"), gbc);
        gbc.gridy++;
        UIStyles.inputField(userField);
        formCard.add(userField, gbc);

        // Password
        gbc.gridy++;
        formCard.add(new JLabel("Password"), gbc);
        gbc.gridy++;
        UIStyles.inputField(passField);
        formCard.add(passField, gbc);

        // Button
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 10, 0);
        JButton loginBtn = new JButton("Sign In");
        UIStyles.primaryButton(loginBtn);
        formCard.add(loginBtn, gbc);

        // Message
        gbc.gridy++;
        messageLabel.setForeground(AppColors.ERROR);
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

            AuthService.AuthResult result = AuthService.authenticate(user, pass, DatabaseConfig.getAuthDataSource());

            if (result.success) {
                this.dispose();
                // Open specific dashboard based on role
                switch (result.role) {
                    case "ADMIN": new AdminDashboard(result.userId).setVisible(true); break;
                    case "INSTRUCTOR": new InstructorDashboard(result.userId).setVisible(true); break;
                    case "STUDENT": new StudentDashboard(result.userId).setVisible(true); break;
                }
            } else {
                messageLabel.setText(result.message);
            }
        });
    }
}