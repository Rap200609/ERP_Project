package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import edu.univ.erp.ui.theme.AppColors;
import edu.univ.erp.ui.theme.UIStyles;

public class AdminDashboard extends JFrame {
    private JPanel contentPanel;
    private int adminId;
    private MaintenanceBanner banner;

    public AdminDashboard(int adminId) {
        this.adminId = adminId;
        setTitle("Admin Dashboard");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // 1. Apply Global Theme
        UIStyles.initFrame(this);
        setLayout(new BorderLayout(0, 0)); // Remove default gaps

        // 2. Top Section (Maintenance Banner)
        JPanel topContainer = new JPanel(new BorderLayout());
        banner = new MaintenanceBanner();
        topContainer.add(banner, BorderLayout.NORTH);
        add(topContainer, BorderLayout.NORTH);
        
        banner.checkAndDisplay();

        // 3. Modern Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(250, 0));
        UIStyles.styleSidebarPanel(sidebar);

        // Sidebar Header
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 20));
        logoPanel.setOpaque(false);
        JLabel heading = new JLabel("Administrator");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        heading.setForeground(AppColors.PRIMARY);
        logoPanel.add(heading);
        sidebar.add(logoPanel);
        
        sidebar.add(Box.createVerticalStrut(10));

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
            JButton btn = new JButton(item);
            btn.setMaximumSize(new Dimension(230, 45));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            UIStyles.styleSidebarButton(btn);
            btn.addActionListener(e -> handleMenuClick(item));
            
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(5));
        }

        sidebar.add(Box.createVerticalGlue());
        add(sidebar, BorderLayout.WEST);

        // 4. Content Area
        contentPanel = new JPanel(new BorderLayout());
        UIStyles.applyContentBackground(contentPanel);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(contentPanel, BorderLayout.CENTER);

        showPanel("Manage Users");
        
        // Refresh banner on window focus
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowActivated(java.awt.event.WindowEvent e) {
                banner.checkAndDisplay();
            }
        });
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
            // Wrap in Card Design
            JPanel cardWrapper = new JPanel(new BorderLayout());
            cardWrapper.setBackground(AppColors.CARD_BG);
            cardWrapper.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDER, 1, true),
                new EmptyBorder(20, 20, 20, 20)
            ));

            JLabel title = new JLabel(panelName);
            title.setFont(new Font("Segoe UI", Font.BOLD, 24));
            title.setForeground(AppColors.TEXT_PRIMARY);
            title.setBorder(new EmptyBorder(0, 0, 20, 0));
            
            cardWrapper.add(title, BorderLayout.NORTH);
            
            // Recursively style the legacy panel
            styleComponentsRecursively(panel);
            panel.setOpaque(false); // Let white card background show through
            
            cardWrapper.add(panel, BorderLayout.CENTER);
            contentPanel.add(cardWrapper, BorderLayout.CENTER);
        }
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    // Helper to modernize components inside loaded panels
    private void styleComponentsRecursively(Container container) {
        for (Component c : container.getComponents()) {
            if (c instanceof JTable) {
                UIStyles.styleTable((JTable) c);
            } else if (c instanceof JButton) {
                JButton b = (JButton) c;
                // Only style if it looks default
                if (b.getBackground().equals(new JButton().getBackground()) || b.getBackground().getAlpha() == 0) {
                   UIStyles.primaryButton(b);
                }
            } else if (c instanceof JTextField) {
                UIStyles.inputField((JTextField) c);
            } else if (c instanceof JScrollPane) {
                UIStyles.softenScrollPane((JScrollPane) c);
            } else if (c instanceof Container) {
                styleComponentsRecursively((Container) c);
            }
        }
    }
}