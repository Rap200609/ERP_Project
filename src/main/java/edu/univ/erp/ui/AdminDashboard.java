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
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBackground(new Color(32, 56, 100));  // Deep Navy Blue

        String[] menuItems = {
            "Manage Users",
            "Manage Courses",
            "Manage Sections",
            "Assign Instructor",
            "Maintenance Mode",
            "Backup/Restore",
            "Change Password",
            "Logout"
        };

        for (String item : menuItems) {
            JButton btn = createMenuButton(item);
            sidebar.add(btn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 5))); // Vertical spacing between buttons
        }

        sidebar.add(Box.createVerticalGlue());

        add(sidebar, BorderLayout.WEST);

        // Content area
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(contentPanel, BorderLayout.CENTER);

        showPanel("Manage Users");
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(180, 40));
        btn.setPreferredSize(new Dimension(180, 40));
        btn.setBackground(new Color(230, 236, 245));
        btn.setForeground(new Color(33, 37, 41));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(200, 215, 240)); // Hover color
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(230, 236, 245));
            }
        });
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
