package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;
import edu.univ.erp.auth.AuthService;
import edu.univ.erp.data.DatabaseConfig;

public class LoginFrame extends JFrame {
    public LoginFrame() {
        setTitle("University ERP Login");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(20);

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(20);

        JButton loginButton = new JButton("Login");
        JLabel messageLabel = new JLabel("");

        JPanel panel = new JPanel(new GridLayout(4, 1));
        panel.add(userLabel);
        panel.add(userField);
        panel.add(passLabel);
        panel.add(passField);
        panel.add(loginButton);
        panel.add(messageLabel);

        add(panel);

        // Adding action listener for login button
        loginButton.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());
            AuthService.AuthResult result = AuthService.authenticate(username, password, DatabaseConfig.getAuthDataSource());
            messageLabel.setText(result.message);
            if (result.success) {
                // TODO: Open dashboard for result.role
                JOptionPane.showMessageDialog(this, "Welcome " + result.role + "!");
                // We can now open the next window based on role
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
