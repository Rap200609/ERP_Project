package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;

import edu.univ.erp.auth.AuthService;
import edu.univ.erp.data.DatabaseConfig;
import edu.univ.erp.ui.theme.AppColors;
import edu.univ.erp.ui.theme.GradientPanel;
import edu.univ.erp.ui.theme.UIStyles;

public class LoginFrame extends JFrame {

    private final JTextField userField = new JTextField(20);
    private final JPasswordField passField = new JPasswordField(20);
    private final JLabel messageLabel = new JLabel("", SwingConstants.CENTER);

    public LoginFrame() {
        setTitle("University ERP Login");
        setSize(520, 420);
        setMinimumSize(new Dimension(520, 380));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        UIStyles.applyFrameBackground(this);
        getRootPane().setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(false);
        setContentPane(root);

        root.add(createHeroPanel(), BorderLayout.NORTH);
        
        // Wrap form panel in scroll pane
        JPanel formPanel = createFormPanel();
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        UIStyles.softenScrollPane(scrollPane);
        root.add(scrollPane, BorderLayout.CENTER);

        JButton loginButton = new JButton("Sign In");
        UIStyles.stylePrimaryButton(loginButton);
        loginButton.addActionListener(doLogin());
        getRootPane().setDefaultButton(loginButton);

        JPanel actionPanel = new JPanel();
        actionPanel.setOpaque(false);
        actionPanel.setBorder(new EmptyBorder(12, 32, 12, 32));
        actionPanel.add(loginButton);
        root.add(actionPanel, BorderLayout.SOUTH);

        SwingUtilities.invokeLater(() -> userField.requestFocusInWindow());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    private JPanel createHeroPanel() {
        GradientPanel header = new GradientPanel(AppColors.PRIMARY, AppColors.PRIMARY_LIGHT);
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(28, 32, 28, 32));

        JLabel title = new JLabel("University ERP System");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));

        JLabel subtitle = new JLabel("Manage courses, grades, and enrolments seamlessly.");
        subtitle.setForeground(new Color(255, 255, 255, 220));
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 15f));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(6));
        textPanel.add(subtitle);

        header.add(textPanel, BorderLayout.CENTER);
        return header;
    }

    private JPanel createFormPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        JPanel card = UIStyles.createCardPanel();
        card.setLayout(new GridBagLayout());
        card.setOpaque(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(4, 0, 12, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel welcome = new JLabel("Welcome back!");
        welcome.setForeground(AppColors.TEXT_PRIMARY);
        welcome.setFont(welcome.getFont().deriveFont(Font.BOLD, 18f));
        card.add(welcome, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 16, 0);
        JLabel hint = new JLabel("Sign in using the credentials provided by the administrator.");
        hint.setForeground(AppColors.TEXT_SECONDARY);
        hint.setFont(hint.getFont().deriveFont(Font.PLAIN, 13f));
        card.add(hint, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 6, 0);
        JLabel userLabel = new JLabel("Username");
        userLabel.setForeground(AppColors.TEXT_PRIMARY);
        userLabel.setFont(userLabel.getFont().deriveFont(Font.BOLD, 13f));
        card.add(userLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 12, 0);
        userField.setColumns(18);
        UIStyles.styleTextField(userField);
        card.add(userField, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 6, 0);
        JLabel passLabel = new JLabel("Password");
        passLabel.setForeground(AppColors.TEXT_PRIMARY);
        passLabel.setFont(passLabel.getFont().deriveFont(Font.BOLD, 13f));
        card.add(passLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 12, 0);
        passField.setColumns(18);
        UIStyles.styleTextField(passField);
        card.add(passField, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(4, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        messageLabel.setForeground(AppColors.ERROR);
        messageLabel.setVisible(false);
        card.add(messageLabel, gbc);

        GridBagConstraints wrapperGbc = new GridBagConstraints();
        wrapperGbc.gridx = 0;
        wrapperGbc.gridy = 0;
        wrapperGbc.insets = new Insets(0, 0, 0, 0);
        wrapperGbc.anchor = GridBagConstraints.CENTER;
        wrapper.add(card, wrapperGbc);
        return wrapper;
    }

    private ActionListener doLogin() {
        return e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                showInlineMessage("Please enter both username and password.", false);
                return;
            }

            AuthService.AuthResult result = AuthService.authenticate(username, password, DatabaseConfig.getAuthDataSource());
            showInlineMessage(result.message, result.success);

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
        };
    }

    private void showInlineMessage(String message, boolean success) {
        messageLabel.setText(message);
        messageLabel.setForeground(success ? AppColors.SUCCESS : AppColors.ERROR);
        messageLabel.setVisible(true);
    }
}
