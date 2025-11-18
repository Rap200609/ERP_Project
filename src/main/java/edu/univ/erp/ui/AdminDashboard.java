package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

import edu.univ.erp.ui.theme.AppColors;
import edu.univ.erp.ui.theme.UIStyles;

public class AdminDashboard extends JFrame {
    private JPanel contentPanel;
    private int adminId;

    public AdminDashboard(int adminId) {
        this.adminId = adminId;
        setTitle("Admin Dashboard");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        UIStyles.applyFrameBackground(this);
        
        setLayout(new BorderLayout());

        // Left sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(220, 0));
        UIStyles.styleSidebarPanel(sidebar);

        JLabel heading = new JLabel("Administrator");
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        heading.setForeground(AppColors.TEXT_PRIMARY);
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 18f));
        sidebar.add(heading);
        sidebar.add(Box.createVerticalStrut(16));

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
            sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        sidebar.add(Box.createVerticalGlue());

        add(sidebar, BorderLayout.WEST);

        // Content area
        contentPanel = new JPanel(new BorderLayout());
        UIStyles.applyContentBackground(contentPanel);
        contentPanel.setBorder(new EmptyBorder(16, 16, 16, 16));
        add(contentPanel, BorderLayout.CENTER);

        showPanel("Manage Users");
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
