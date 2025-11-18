package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;

public class InstructorDashboard extends JFrame {
    private int instructorId;
    private JPanel contentPanel;
    private MaintenanceBanner banner;

    public InstructorDashboard(int instructorId) {
        this.instructorId = instructorId;
        setTitle("Instructor Dashboard");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout());
        
        // Add maintenance banner at the top
        banner = new MaintenanceBanner();
        add(banner, BorderLayout.NORTH);
        banner.checkAndDisplay();

        // Left sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBackground(UITheme.BG_SIDEBAR);
        sidebar.setBorder(new javax.swing.border.EmptyBorder(20, 0, 20, 0));

        // Sidebar header
        JLabel sidebarTitle = new JLabel("Instructor Portal");
        sidebarTitle.setFont(UITheme.FONT_SUBHEADING);
        sidebarTitle.setForeground(UITheme.TEXT_WHITE);
        sidebarTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarTitle.setBorder(new javax.swing.border.EmptyBorder(0, 0, 20, 0));
        sidebar.add(sidebarTitle);
        sidebar.add(UITheme.createVerticalSpacer(10));

        String[] menuItems = {
            "My Sections",
            "Grade Entry (Live)",
            "Class Stats",
            "Export CSV",
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

        showPanel("My Sections");
        
        // Add window listener to refresh banner
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
            new ChangePasswordDialog(this, instructorId).setVisible(true);
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
            case "My Sections":
                panel = new InstructorSectionsPanel(instructorId);
                break;
            case "Grade Entry (Live)":
                panel = new InstructorGradeEntryPanel(instructorId);
                break;
            case "Class Stats":
                panel = new InstructorStatsPanel(instructorId);
                break;
            case "Export CSV":
                panel = new GradesExportPanel(instructorId);
                break;
        }

        if (panel != null) {
            contentPanel.add(panel, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
        }
    }
}
