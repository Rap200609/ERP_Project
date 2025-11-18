package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

import edu.univ.erp.ui.theme.AppColors;
import edu.univ.erp.ui.theme.UIStyles;

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
        UIStyles.applyFrameBackground(this);
        
        // Main layout
        setLayout(new BorderLayout());
        
        // Add maintenance banner at the top
        banner = new MaintenanceBanner();
        add(banner, BorderLayout.NORTH);
        banner.checkAndDisplay();

        // Left sidebar with navigation buttons
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(220, 0));
        UIStyles.styleSidebarPanel(sidebar);

        JLabel heading = new JLabel("Student Portal");
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        heading.setForeground(AppColors.TEXT_PRIMARY);
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 18f));
        sidebar.add(heading);
        sidebar.add(Box.createVerticalStrut(16));

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

        sidebar.add(Box.createVerticalGlue());

        add(sidebar, BorderLayout.WEST);

        // Content area
        contentPanel = new JPanel(new BorderLayout());
        UIStyles.applyContentBackground(contentPanel);
        contentPanel.setBorder(new EmptyBorder(16, 16, 16, 16));
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
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setPreferredSize(new Dimension(180, 44));
        btn.setFont(btn.getFont().deriveFont(Font.BOLD, 14f));
        UIStyles.styleSidebarButton(btn);
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
