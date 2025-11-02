package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;
import edu.univ.erp.auth.AuthService;
import edu.univ.erp.data.DatabaseConfig;

public class LoginFrame extends JFrame {
    public LoginFrame() {
        setTitle("University ERP Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(20);

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(20);

        JButton loginButton = new JButton("Login");
        JLabel messageLabel = new JLabel("", SwingConstants.CENTER);

        JPanel panel = new JPanel(new GridLayout(6, 1, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.add(userLabel);
        panel.add(userField);
        panel.add(passLabel);
        panel.add(passField);
        panel.add(loginButton);
        panel.add(messageLabel);

        add(panel);

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
