package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;

public class StudentDashboard extends JFrame {
    private int studentId;
    private JPanel contentPanel;
    private MaintenanceBanner banner;

    public StudentDashboard(int studentId) {
        this.studentId = studentId;
        setTitle("Student Dashboard");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main layout
        setLayout(new BorderLayout());
        
        // Add maintenance banner at the top
        banner = new MaintenanceBanner();
        add(banner, BorderLayout.NORTH);
        banner.checkAndDisplay();

        // Left sidebar with navigation buttons
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBackground(new Color(240, 240, 240));

        String[] menuItems = {
            "Course Catalog",
            "Register Section",
            "Drop Section",
            "My Timetable",
            "View Grades",
            "Download Transcript",
            "Change Password",
            "Logout"
        };

        for (String item : menuItems) {
            JButton btn = createMenuButton(item);
            sidebar.add(btn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        add(sidebar, BorderLayout.WEST);

        // Content area
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(contentPanel, BorderLayout.CENTER);

        // Show course catalog by default
        showPanel("Course Catalog");
        
        // Add component listener to refresh banner when window becomes visible
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowActivated(java.awt.event.WindowEvent e) {
                banner.checkAndDisplay();
            }
        });
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(180, 40));
        btn.setPreferredSize(new Dimension(180, 40));
        btn.addActionListener(e -> handleMenuClick(text));
        return btn;
    }

    private void handleMenuClick(String menuItem) {
        if ("Logout".equals(menuItem)) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to logout?", 
                "Logout", 
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new edu.univ.erp.ui.LoginFrame().setVisible(true);
            }
        }
        else if ("Change Password".equals(menuItem)) { 
            new ChangePasswordDialog(this, studentId).setVisible(true);
        }
        else {
            showPanel(menuItem);
        }
    }

    private void showPanel(String panelName) {
        contentPanel.removeAll();
        
        // Refresh banner before showing panel
        banner.checkAndDisplay();
        
        JPanel panel = null;
        switch (panelName) {
            case "Course Catalog":
                panel = new StudentCourseCatalogPanel();
                break;
            case "Register Section":
                panel = new StudentRegisterPanel(studentId);
                break;
            case "Drop Section":
                panel = new StudentDropPanel(studentId);
                break;
            case "My Timetable":
                panel = new StudentTimetablePanel(studentId);
                break;
            case "View Grades":
                panel = new StudentGradesPanel(studentId);
                break;
            case "Download Transcript":
                panel = new StudentTranscriptPanel(studentId);
                break;
        }

        if (panel != null) {
            contentPanel.add(panel, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
        }
    }
}
