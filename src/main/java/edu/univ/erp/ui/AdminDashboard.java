package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {
    private JPanel contentPanel;
    private int adminId;

    public AdminDashboard(int adminId) {
        this.adminId = adminId;
        setTitle("Admin Dashboard");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout());

        // Left sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBackground(UITheme.BG_SIDEBAR);
        sidebar.setBorder(new javax.swing.border.EmptyBorder(20, 0, 20, 0));

        // Sidebar header
        JLabel sidebarTitle = new JLabel("Admin Portal");
        sidebarTitle.setFont(UITheme.FONT_SUBHEADING);
        sidebarTitle.setForeground(UITheme.TEXT_WHITE);
        sidebarTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarTitle.setBorder(new javax.swing.border.EmptyBorder(0, 0, 20, 0));
        sidebar.add(sidebarTitle);
        sidebar.add(UITheme.createVerticalSpacer(10));

        String[] menuItems = {
            "Manage Users",
            "Manage Courses",
            "Manage Sections",
            "Assign Instructor",
            "Drop Deadline",
            "Maintenance Mode",
            "Backup/Restore",
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

        showPanel("Manage Users");
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        UITheme.styleSidebarButton(btn);
        btn.addActionListener(e -> handleMenuClick(text));
        return btn;
    }

    private void handleMenuClick(String menuItem) {
        if ("Logout".equals(menuItem)) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new edu.univ.erp.ui.LoginFrame().setVisible(true);
            }
        } 
        else if ("Change Password".equals(menuItem)) {
            new ChangePasswordDialog(this, adminId).setVisible(true);
        }
        else {
            showPanel(menuItem);
        }
    }

    private void showPanel(String panelName) {
        contentPanel.removeAll();
        
        JPanel panel = null;
        switch (panelName) {
            case "Manage Users":
                panel = new AddUserPanel();
                break;
            case "Manage Courses":
                panel = new CoursePanel();
                break;
            case "Manage Sections":
                panel = new SectionPanel();
                break;
            case "Assign Instructor":
                panel = new AssignInstructorPanel();
                break;
            case "Drop Deadline":
                panel = new DropDeadlinePanel();
                break;
            case "Maintenance Mode":
                panel = new MaintenanceModePanel();
                break;
            case "Backup/Restore":
                panel = new BackupRestorePanel();
                break;
        }

        if (panel != null) {
            contentPanel.add(panel, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
        }
    }
}
