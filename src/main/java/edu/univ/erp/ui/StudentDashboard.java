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
        // ... setup ...
        UIStyles.initFrame(this); // Apply BG color
        setLayout(new BorderLayout(0, 0)); // No gaps

        // --- 1. SIDEBAR ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.WHITE); // White Sidebar
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, AppColors.BORDER)); // Right border only

        // Logo Area
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        logoPanel.setBackground(Color.WHITE);
        JLabel logo = new JLabel("Student Portal");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setForeground(AppColors.PRIMARY);
        logoPanel.add(logo);
        sidebar.add(logoPanel);

        // Menu Items
        String[] menuItems = { "Course Catalog", "Register Section", "Drop Section", "My Timetable", "View Grades", "Download Transcript", "Logout" };
        
        for (String item : menuItems) {
            JButton btn = new JButton(item);
            UIStyles.sidebarButton(btn); // Apply style
            btn.setMaximumSize(new Dimension(220, 45)); // Uniform size
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            btn.addActionListener(e -> handleMenuClick(item)); // Your existing logic
            
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(5)); // Spacing
        }
        
        add(sidebar, BorderLayout.WEST);

        // --- 2. CONTENT AREA ---
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(AppColors.BACKGROUND); // Light Gray
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Padding around the "Card"

        add(contentPanel, BorderLayout.CENTER);
        
        // Initial load
        showPanel("Course Catalog");
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
    
        JPanel activePanel = null;
        // ... switch logic (same as before) ...
        // e.g. activePanel = new StudentCourseCatalogPanel();
    
        if (activePanel != null) {
            // Wrap the functional panel in a visual card
            JPanel cardWrapper = new JPanel(new BorderLayout());
            cardWrapper.setBackground(Color.WHITE);
            cardWrapper.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDER),
                new EmptyBorder(20, 20, 20, 20)
            ));
            
            // Add Title
            JLabel title = new JLabel(panelName);
            title.setFont(new Font("Segoe UI", Font.BOLD, 22));
            title.setBorder(new EmptyBorder(0, 0, 20, 0));
            cardWrapper.add(title, BorderLayout.NORTH);
            
            // If the panel uses a JTable, style it!
            // (You might need to do this inside the specific Panel classes, 
            // or traverse components here)
            styleComponentsRecursively(activePanel);

            cardWrapper.add(activePanel, BorderLayout.CENTER);
            contentPanel.add(cardWrapper, BorderLayout.CENTER);
        }
    
    contentPanel.revalidate();
    contentPanel.repaint();
    }
}
