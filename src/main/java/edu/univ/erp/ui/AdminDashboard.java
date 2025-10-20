package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {
    private JPanel contentPanel;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(6, 1, 0, 10));
        sidebar.setPreferredSize(new Dimension(180, 0));
        JButton usersBtn = new JButton("Manage Users");
        JButton coursesBtn = new JButton("Manage Courses");
        JButton sectionsBtn = new JButton("Manage Sections");
        JButton assignmentsBtn = new JButton("Assign Instructor");
        JButton maintenanceBtn = new JButton("Maintenance Mode");
        JButton logoutBtn = new JButton("Logout");

        sidebar.add(usersBtn);
        sidebar.add(coursesBtn);
        sidebar.add(sectionsBtn);
        sidebar.add(assignmentsBtn);
        sidebar.add(maintenanceBtn);
        sidebar.add(logoutBtn);

        // Main content panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(new JLabel("Welcome, Admin! Please select an option."), BorderLayout.CENTER);

        // Layout: sidebar left, content center
        setLayout(new BorderLayout());
        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // Wire up buttons (start with Manage Users)
        usersBtn.addActionListener(e -> showUserManagement());
        // You can add listeners for others later

        logoutBtn.addActionListener(e -> {
            this.dispose();
            new LoginFrame().setVisible(true);
        });
    }

    private void showUserManagement() {
        contentPanel.removeAll();
        contentPanel.add(new AddUserPanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
