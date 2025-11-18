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
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBackground(UITheme.BG_SIDEBAR);
        sidebar.setBorder(new javax.swing.border.EmptyBorder(20, 0, 20, 0));

        // Sidebar header
        JLabel sidebarTitle = new JLabel("Student Portal");
        sidebarTitle.setFont(UITheme.FONT_SUBHEADING);
        sidebarTitle.setForeground(UITheme.TEXT_WHITE);
        sidebarTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarTitle.setBorder(new javax.swing.border.EmptyBorder(0, 0, 20, 0));
        sidebar.add(sidebarTitle);
        sidebar.add(UITheme.createVerticalSpacer(10));

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
            sidebar.add(UITheme.createVerticalSpacer(3));
        }

        sidebar.add(Box.createVerticalGlue());
        add(sidebar, BorderLayout.WEST);

        // Content area
        contentPanel = new JPanel(new BorderLayout());
        UITheme.styleContentPanel(contentPanel);
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
        UITheme.styleSidebarButton(btn);
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
