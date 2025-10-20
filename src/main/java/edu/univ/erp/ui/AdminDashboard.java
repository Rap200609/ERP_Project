package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {
    private JPanel contentPanel; // main area for switching cards
    private CardLayout cardLayout;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        // SIDEBAR
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(0, 1, 0, 10));
        sidebar.setPreferredSize(new Dimension(180, 0));

        JButton userBtn = new JButton("Manage Users");
        JButton courseBtn = new JButton("Manage Courses");
        JButton sectionBtn = new JButton("Manage Sections");
        JButton assignBtn = new JButton("Assign Instructor");
        JButton settingsBtn = new JButton("Maintenance Mode");
        JButton logoutBtn = new JButton("Logout");

        sidebar.add(userBtn);
        sidebar.add(courseBtn);
        sidebar.add(sectionBtn);
        sidebar.add(assignBtn);
        sidebar.add(settingsBtn);
        sidebar.add(logoutBtn);

        // CONTENT PANELS (CARDS)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        AddUserPanel userPanel = new AddUserPanel();
        CoursePanel coursePanel = new CoursePanel();
        JPanel sectionPanel = new SectionPanel();
        JPanel assignPanel = new JPanel();  // Replace with real AssignInstructorPanel
        JPanel settingsPanel = new JPanel(); // Replace with real SettingsPanel

        contentPanel.add(userPanel, "USERS");
        contentPanel.add(coursePanel, "COURSES");
        contentPanel.add(sectionPanel, "SECTIONS");
        contentPanel.add(assignPanel, "ASSIGN");
        contentPanel.add(settingsPanel, "SETTINGS");

        // LAYOUT
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        // BUTTON HANDLERS
        userBtn.addActionListener(e -> cardLayout.show(contentPanel, "USERS"));
        courseBtn.addActionListener(e -> cardLayout.show(contentPanel, "COURSES"));
        sectionBtn.addActionListener(e -> cardLayout.show(contentPanel, "SECTIONS"));
        assignBtn.addActionListener(e -> cardLayout.show(contentPanel, "ASSIGN"));
        settingsBtn.addActionListener(e -> cardLayout.show(contentPanel, "SETTINGS"));
        logoutBtn.addActionListener(e -> {
            dispose();
            System.exit(0);
            // optionally show login frame again
        });

        // Show user management by default
        cardLayout.show(contentPanel, "USERS");
    }

    // main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminDashboard().setVisible(true));
    }
}
