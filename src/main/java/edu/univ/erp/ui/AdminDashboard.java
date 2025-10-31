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
        JButton backupBtn = new JButton("Backup/Restore");
        JButton logoutBtn = new JButton("Logout");

        sidebar.add(userBtn);
        sidebar.add(courseBtn);
        sidebar.add(sectionBtn);
        sidebar.add(assignBtn);
        sidebar.add(settingsBtn);
        sidebar.add(backupBtn);
        sidebar.add(logoutBtn);

        // CONTENT PANELS (CARDS)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        AddUserPanel userPanel = new AddUserPanel();
        CoursePanel coursePanel = new CoursePanel();
        SectionPanel sectionPanel = new SectionPanel();
        AssignInstructorPanel assignPanel = new AssignInstructorPanel();
        MaintenanceModePanel settingsPanel = new MaintenanceModePanel();
        BackupRestorePanel backupPanel = new BackupRestorePanel();

        contentPanel.add(userPanel, "USERS");
        contentPanel.add(coursePanel, "COURSES");
        contentPanel.add(sectionPanel, "SECTIONS");
        contentPanel.add(assignPanel, "ASSIGN_INSTRUCTOR");
        contentPanel.add(settingsPanel, "SETTINGS");
        contentPanel.add(backupPanel, "BACKUP");

        // LAYOUT
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        // BUTTON HANDLERS
        userBtn.addActionListener(e -> cardLayout.show(contentPanel, "USERS"));
        courseBtn.addActionListener(e -> cardLayout.show(contentPanel, "COURSES"));
        sectionBtn.addActionListener(e -> cardLayout.show(contentPanel, "SECTIONS"));
        assignBtn.addActionListener(e -> cardLayout.show(contentPanel, "ASSIGN_INSTRUCTOR"));
        settingsBtn.addActionListener(e -> cardLayout.show(contentPanel, "SETTINGS"));
        backupBtn.addActionListener(e -> cardLayout.show(contentPanel, "BACKUP"));
        logoutBtn.addActionListener(e -> {
            dispose();
            System.exit(0);
        });

        // Show user management by default
        cardLayout.show(contentPanel, "USERS");
    }

    // main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminDashboard().setVisible(true));
    }
}
